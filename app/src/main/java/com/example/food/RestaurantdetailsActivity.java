package com.example.food;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.bumptech.glide.Glide;

public class RestaurantdetailsActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    ImageButton exit, imageResource, qrcode;
    EditText name, phone_number, stk, address,Mota;
    Button btn_update, btn_xoa;
    ImageView imgRestaurantView, qrcodeView;



    // Biến lưu URI của các ảnh
    private Uri selectedQrcodeUri = null;
    private Uri selectedImageUri =null;  // Để lưu trữ URI của ảnh đã chọn
    private int imageSelected = 0;

    //Id nhà hàng
    private String restaurantID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_restaunt_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
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
        btn_update = findViewById(R.id.btn_update);
        btn_xoa = findViewById(R.id.btn_xoa);
        imgRestaurantView = findViewById(R.id.imgRestaurantView);
        qrcodeView = findViewById(R.id.qrcodeView);
        Mota  = findViewById(R.id.Mota);

        restaurantID = getIntent().getStringExtra("restaurantId");

        // hiển thị restaurantID
        Log.d("RestaurantID", "ID nhà hàng: " + restaurantID);

        if (restaurantID != null) {
            loadRestaurantData(restaurantID);
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin nhà hàng!", Toast.LENGTH_SHORT).show();
        }

        // Phương thức cho phép chọn ảnh từ thư viện
        ActivityResultLauncher<Intent> selectImage = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
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
        // Lấy ID cửa hàng hiện có từ Intent


        // Xử lý cập nhật tài khoản
        btn_update.setOnClickListener(v -> {
            // Lấy dữ liệu từ các trường nhập liệu
            String tenCuaHang = name.getText().toString();
            String soDienThoai = phone_number.getText().toString();
            String soTaiKhoan = stk.getText().toString();
            String diaChi = address.getText().toString();
            String mota = Mota.getText().toString();
            // Thêm khai báo này trong onCreate()
            restaurantID = getIntent().getStringExtra("restaurantId");




            if (tenCuaHang.isEmpty() || soDienThoai.isEmpty() || soTaiKhoan.isEmpty() || diaChi.isEmpty()) {
                Toast.makeText(RestaurantdetailsActivity.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo đối tượng nhà hàng mới
            Restaurant updatedRestaurant = new Restaurant(tenCuaHang, soDienThoai, soTaiKhoan, diaChi, mota);

            // Cập nhật thông tin vào Firebase Database
            databaseReference.child(restaurantID).setValue(updatedRestaurant)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(RestaurantdetailsActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(RestaurantdetailsActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        // Xử lý thoát
        exit.setOnClickListener(v -> finish());


    }

    // Hàm tải dữ liẹu nhà hàng từ Firebase
    // Di chuyển loadRestaurantData() ra khỏi onCreate()
    private void loadRestaurantData(String restaurantID) {
        databaseReference.child(restaurantID).get().addOnSuccessListener(snapshot ->  {
            if (snapshot.exists()) {
                Restaurant restaurant = snapshot.getValue(Restaurant.class);
                // Hiển thị dữ liệu lên các trường nhập liệu
                name.setText(restaurant.getName());
                phone_number.setText(restaurant.getPhone_number());
                stk.setText(restaurant.getStk());
                address.setText(restaurant.getAddress());
                Mota.setText(restaurant.getMota());

                // Load hình ảnh từ URL vào ImageView (dùng thư viện Glide)
                if (restaurant.getImageUrl() != null) {
                    Glide.with(this)
                            .load(restaurant.getImageUrl())
                            .into(imgRestaurantView);
                }

                Glide.with(this)
                        .load(restaurant.getQrcodeUrl())
                        .into(qrcodeView);
            }
            else {
                Toast.makeText(RestaurantdetailsActivity.this, "Không tìm thấy dữ liệu nhà hàng!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(RestaurantdetailsActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}