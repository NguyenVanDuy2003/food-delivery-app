package com.example.food;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ThemNhaHangMainActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    ImageButton exit, imageResource, qrcode;
    EditText name, phone_number, stk, address;
    Button add_TaiKhoan;
    ImageView imgRestaurantView, qrcodeView;

    private int imageSelected = 0;
    private Uri selectedImageUri;  // Để lưu trữ URI của ảnh đã chọn

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_nha_hang_main);

        // Khởi tạo Firebase Database và Storage
        databaseReference = FirebaseDatabase.getInstance().getReference("restaurants");
        storageReference = FirebaseStorage.getInstance().getReference("restaurant_images");

        // Ánh xạ các view
        exit = findViewById(R.id.exit);
        imageResource = findViewById(R.id.imageResource);
        qrcode = findViewById(R.id.qrcode);
        name = findViewById(R.id.name);
        phone_number = findViewById(R.id.phone_number);
        stk = findViewById(R.id.stk);
        address = findViewById(R.id.address);
        add_TaiKhoan = findViewById(R.id.add_TaiKhoan);
        imgRestaurantView = findViewById(R.id.imgRestaurantView);
        qrcodeView = findViewById(R.id.qrcodeView);

        // Phương thức cho phép chọn ảnh từ thư viện
        ActivityResultLauncher<Intent> selectImage = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            selectedImageUri = imageUri;  // Lưu URI của ảnh đã chọn
                            if (imageSelected == 1) {
                                imgRestaurantView.setImageURI(imageUri);
                            } else if (imageSelected == 2) {
                                qrcodeView.setImageURI(imageUri);
                            }
                        }
                    }
                });

        // Mở thư viện chọn ảnh khi bấm vào imgRestaurant
        imageResource.setOnClickListener(v -> {
            imageSelected = 1;  // Đánh dấu chọn ảnh cho imgRestaurant
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            selectImage.launch(intent);
        });

        // Mở thư viện chọn ảnh khi bấm vào qrcode
        qrcode.setOnClickListener(v -> {
            imageSelected = 2;  // Đánh dấu chọn ảnh cho qrcode
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            selectImage.launch(intent);
        });

        // Xử lý thêm tài khoản
        add_TaiKhoan.setOnClickListener(v -> {
            // Lấy dữ liệu từ các trường nhập liệu
            String tenCuaHang = name.getText().toString();
            String soDienThoai = phone_number.getText().toString();
            String soTaiKhoan = stk.getText().toString();
            String diaChi = address.getText().toString();

            // Kiểm tra xem thông tin đã được nhập đầy đủ chưa
            if(tenCuaHang.isEmpty() || soDienThoai.isEmpty() || soTaiKhoan.isEmpty() || diaChi.isEmpty()){
                Toast.makeText(ThemNhaHangMainActivity.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra nếu có ảnh đã chọn
            if (selectedImageUri == null) {
                Toast.makeText(ThemNhaHangMainActivity.this, "Vui lòng chọn ảnh!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo ID cho nhà hàng
            String restaurantId = databaseReference.push().getKey();
            // Tạo đối tượng đại diện cho nhà hàng
            Restaurant newRestaurant = new Restaurant(tenCuaHang, soDienThoai, soTaiKhoan, diaChi);

            // Lưu ảnh lên Firebase Storage
            StorageReference fileReference = storageReference.child(restaurantId + "_image.jpg");
            fileReference.putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Lấy URL của ảnh đã upload
                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Lưu URL ảnh vào Firebase Realtime Database
                            newRestaurant.setImageUrl(uri.toString());
                            if (restaurantId != null) {
                                databaseReference.child(restaurantId).setValue(newRestaurant)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(ThemNhaHangMainActivity.this, "Thêm nhà hàng thành công!", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(ThemNhaHangMainActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            }
                        }).addOnFailureListener(e -> {
                            Toast.makeText(ThemNhaHangMainActivity.this, "Lỗi tải ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }).addOnFailureListener(e -> {
                        Toast.makeText(ThemNhaHangMainActivity.this, "Lỗi tải ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        // Xử lý thoát
        exit.setOnClickListener(v -> finish());
    }
}
