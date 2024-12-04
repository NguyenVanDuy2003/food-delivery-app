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

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ThemNhaHangMainActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    ImageButton exit, imageResource, qrcode;
    EditText name, phone_number, stk, address;
    Button add_TaiKhoan;
    ImageView imgRestaurantView, qrcodeView;

    private int imageSelected = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_them_nha_hang_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("restaurants");

        exit = findViewById(R.id.exit);
        imageResource = findViewById(R.id.imageResource);
        qrcode = findViewById(R.id.qrcode);
        name = findViewById(R.id.name);
        phone_number = findViewById(R.id.phone_number);
        stk = findViewById(R.id.stk);
        address = findViewById(R.id.address);
        add_TaiKhoan = findViewById(R.id.add_TaiKhoan);
        imgRestaurantView = findViewById(R.id.imgRestaurantView);  // ImageView để hiển thị ảnh nhà hàng
        qrcodeView = findViewById(R.id.qrcodeView);  // ImageView để hiển thị ảnh QR code

        // Phương thức cho phép chọn ảnh từ thư viện
        ActivityResultLauncher<Intent> selectImage = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            // Kiểm tra biến đánh dấu để chọn ảnh nào
                            if (imageSelected == 1) {
                                imgRestaurantView.setImageURI(imageUri);  // Cập nhật ảnh cho ImageView imgRestaurant
                            } else if (imageSelected == 2) {
                                qrcodeView.setImageURI(imageUri);  // Cập nhật ảnh cho ImageView qrcode
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

            // ktr đã nhập chưa  hay bỏ trống
            if(tenCuaHang.isEmpty() || soDienThoai.isEmpty() || soTaiKhoan.isEmpty() || diaChi.isEmpty()){
                Toast.makeText(ThemNhaHangMainActivity.this, "Vui lòng nhập đầy đử thông tin!", Toast.LENGTH_SHORT).show();
                return; //
            }

            // Tạo đối tượng dữ liệu
            String restaurantId = databaseReference.push().getKey(); // Tạo ID ngẫu nhiên cho nhà hàng
            // Tạo một object đại diện cho nhà hàng
            Restaurant newRestaurant = new Restaurant(tenCuaHang, soDienThoai, soTaiKhoan, diaChi);

            // Lưu dữ liệu vào Firebase Realtime Database
            if (restaurantId != null) {
                databaseReference.child(restaurantId).setValue(newRestaurant)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(ThemNhaHangMainActivity.this, "Thêm nhà hàng thành công!", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(ThemNhaHangMainActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });

        // Xử lý thoát
        exit.setOnClickListener(v -> finish());
    }
}
