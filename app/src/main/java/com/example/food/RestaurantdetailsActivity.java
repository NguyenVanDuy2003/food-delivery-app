package com.example.food;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
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
import androidx.appcompat.app.AlertDialog;

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

        btn_update.setOnClickListener(v -> {
            String tenCuaHang = name.getText().toString();
            String soDienThoai = phone_number.getText().toString();
            String soTaiKhoan = stk.getText().toString();
            String diaChi = address.getText().toString();
            String mota = Mota.getText().toString();
            restaurantID = getIntent().getStringExtra("restaurantId");

            if (tenCuaHang.isEmpty() || soDienThoai.isEmpty() || soTaiKhoan.isEmpty() || diaChi.isEmpty()) {
                Toast.makeText(RestaurantdetailsActivity.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo đối tượng nhà hàng mới
            Restaurant updatedRestaurant = new Restaurant(restaurantID, tenCuaHang, 0, soDienThoai, soTaiKhoan, diaChi, mota);

            // Check if a new restaurant image is selected
            if (selectedImageUri != null) {
                StorageReference imageRef = storageReference.child(restaurantID + "_image.jpg");
                imageRef.putFile(selectedImageUri)
                        .addOnSuccessListener(taskSnapshot -> {
                            // Lấy URL của ảnh nhà hàng
                            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                updatedRestaurant.setImageUrl(uri.toString());
                                // Proceed to upload QR code if selected
                                uploadQRCode(updatedRestaurant);
                            }).addOnFailureListener(e -> {
                                Toast.makeText(RestaurantdetailsActivity.this, "Lỗi tải ảnh nhà hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        }).addOnFailureListener(e -> {
                            Toast.makeText(RestaurantdetailsActivity.this, "Lỗi tải ảnh nhà hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                uploadQRCode(updatedRestaurant);
            }
        });


        // Xử lý thoát
        exit.setOnClickListener(v -> finish());

//        Button btnManageFood = findViewById(R.id.btn_manage_food);

//        btnManageFood.setOnClickListener(v -> {
//            Intent intent = new Intent(RestaurantdetailsActivity.this, AdminManagementActivity.class);
//            intent.putExtra("restaurantId", restaurantID); // Pass the restaurant ID if needed
//            startActivity(intent);
//        });

        btn_xoa.setOnClickListener(v -> {
            // Hiển thị hộp thoại xác nhận trước khi xóa
            new AlertDialog.Builder(RestaurantdetailsActivity.this)
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc chắn muốn xóa nhà hàng này không?")
                    .setPositiveButton("Có", (dialog, which) -> {
                        // Gọi phương thức xóa nhà hàng
                        deleteRestaurant(restaurantID);
                    })
                    .setNegativeButton("Không", null)
                    .show();
        });
    }

    private void uploadQRCode(Restaurant updatedRestaurant) {
        if (selectedQrcodeUri != null) {
            StorageReference qrcodeRef = storageReference.child(restaurantID + "_qrcode.jpg");
            qrcodeRef.putFile(selectedQrcodeUri)
                    .addOnSuccessListener(taskSnapshot1 -> {
                        // Lấy URL của ảnh QR code
                        qrcodeRef.getDownloadUrl().addOnSuccessListener(uriQrcode -> {
                            updatedRestaurant.setQrcodeUrl(uriQrcode.toString());
                            // Save restaurant data to Firebase
                            saveRestaurantData(updatedRestaurant);
                        }).addOnFailureListener(e -> {
                            Toast.makeText(RestaurantdetailsActivity.this, "Lỗi tải ảnh QR code: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }).addOnFailureListener(e -> {
                        Toast.makeText(RestaurantdetailsActivity.this, "Lỗi tải ảnh QR code: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            // If no new QR code is selected, retain the existing QR code URL
            saveRestaurantData(updatedRestaurant);
        }
    }

    private void saveRestaurantData(Restaurant updatedRestaurant) {
        if (restaurantID != null) {
            databaseReference.child(restaurantID).setValue(updatedRestaurant)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(RestaurantdetailsActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(RestaurantdetailsActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }


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

    private void deleteRestaurant(String restaurantID) {
        if (restaurantID != null) {
            databaseReference.child(restaurantID).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(RestaurantdetailsActivity.this, "Xóa nhà hàng thành công!", Toast.LENGTH_SHORT).show();
                        finish(); // Quay lại màn hình trước đó
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(RestaurantdetailsActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(RestaurantdetailsActivity.this, "ID nhà hàng không hợp lệ!", Toast.LENGTH_SHORT).show();
        }
    }
}