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

public class AddRestauntsActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    ImageButton exit, imageResource, qrcode;
    EditText name, phone_number, stk, address,Mota;
    Button add_TaiKhoan;
    ImageView imgRestaurantView, qrcodeView;

    // Biến lưu URI của các ảnh
    private Uri selectedQrcodeUri = null;
    private Uri selectedImageUri;  // Để lưu trữ URI của ảnh đã chọn
    private int imageSelected = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_restaunts_main);

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
        Mota  = findViewById(R.id.Mota);


        // Phương thức cho phép chọn ảnh từ thư viện
        ActivityResultLauncher<Intent> selectImage = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            if (imageSelected == 1) {
                                selectedImageUri = imageUri;  // Lưu URI ảnh nhà hàng
                                imgRestaurantView.setImageURI(imageUri);
                            } else if (imageSelected == 2) {
                                selectedQrcodeUri = imageUri;  // Lưu URI ảnh QR code
                                qrcodeView.setImageURI(imageUri);
                            }
                        }
                    }
                });

        // Chọn ảnh nhà hàng
        imageResource.setOnClickListener(v -> {
            imageSelected = 1;
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            selectImage.launch(intent);
        });

        // Chọn ảnh QR code
        qrcode.setOnClickListener(v -> {
            imageSelected = 2;
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
            if (tenCuaHang.isEmpty() || soDienThoai.isEmpty() || soTaiKhoan.isEmpty() || diaChi.isEmpty()) {
                Toast.makeText(AddRestauntsActivity.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra nếu có ảnh đã chọn
            if (selectedImageUri == null || selectedQrcodeUri == null) {
                Toast.makeText(AddRestauntsActivity.this, "Vui lòng chọn cả 2 ảnh!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo ID cho nhà hàng
            String restaurantId = databaseReference.push().getKey();
            Restaurant newRestaurant = new Restaurant(tenCuaHang, soDienThoai, soTaiKhoan, diaChi);

            // Lưu ảnh nhà hàng lên Firebase Storage
            StorageReference imageRef = storageReference.child(restaurantId + "_image.jpg");
            imageRef.putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Lấy URL của ảnh nhà hàng
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            newRestaurant.setImageUrl(uri.toString());

                            // Lưu ảnh QR code lên Firebase Storage
                            StorageReference qrcodeRef = storageReference.child(restaurantId + "_qrcode.jpg");
                            qrcodeRef.putFile(selectedQrcodeUri)
                                    .addOnSuccessListener(taskSnapshot1 -> {
                                        // Lấy URL của ảnh QR code
                                        qrcodeRef.getDownloadUrl().addOnSuccessListener(uriQrcode -> {
                                            newRestaurant.setQrcodeUrl(uriQrcode.toString());

                                            // Lưu thông tin nhà hàng vào Firebase Realtime Database
                                            if (restaurantId != null) {
                                                databaseReference.child(restaurantId).setValue(newRestaurant)
                                                        .addOnSuccessListener(aVoid -> {
                                                            Toast.makeText(AddRestauntsActivity.this, "Thêm nhà hàng thành công!", Toast.LENGTH_SHORT).show();
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            Toast.makeText(AddRestauntsActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        });
                                            }
                                        }).addOnFailureListener(e -> {
                                            Toast.makeText(AddRestauntsActivity.this, "Lỗi tải ảnh QR code: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(AddRestauntsActivity.this, "Lỗi tải ảnh QR code: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }).addOnFailureListener(e -> {
                            Toast.makeText(AddRestauntsActivity.this, "Lỗi tải ảnh nhà hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }).addOnFailureListener(e -> {
                        Toast.makeText(AddRestauntsActivity.this, "Lỗi tải ảnh nhà hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        // Xử lý thoát
        exit.setOnClickListener(v -> finish());
    }

}
