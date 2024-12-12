package com.example.food;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.food.Common.CommonKey;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.io.File;
import java.io.OutputStream;

public class ThanhToanOnlActivity extends AppCompatActivity {
    // ánh xa
    private DatabaseReference databaseReference,cartdatabaseReference;
    private ImageView qrcode ;
    private TextView name, stk, price;
    private Button btnquaylai , btnluuanh;
    private String restaurantID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_thanh_toan_onl);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Ánh xạ
        name = findViewById(R.id.name);
        qrcode = findViewById(R.id.qrcode);
        stk = findViewById(R.id.stk);
        price = findViewById(R.id.price);
        btnquaylai = findViewById(R.id.btn_quaylai);
        btnluuanh = findViewById(R.id.btn_luuanh);

        // Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("restaurants");
        cartdatabaseReference = FirebaseDatabase.getInstance().getReference("Cart");

        // lấy userId từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(CommonKey.MY_APP_PREFS, MODE_PRIVATE);
        String userId = sharedPreferences.getString(CommonKey.USER_ID, null);

        if (userId != null) {
            fetchRestaurantIdsForUser(userId);
        } else {
            Log.w("UserID", "No userId found in SharedPreferences.");
        }

        btnquaylai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }

        });
        btnluuanh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1 lấy Bitmap từ ImageView
                qrcode.setDrawingCacheEnabled(true); // Bật cache cho ImageView
                qrcode.buildDrawingCache(); // tạo bản vẽ cache từ nội dung của ImageView
                Bitmap bitmap = Bitmap.createBitmap(qrcode.getDrawingCache());
                qrcode.setDrawingCacheEnabled(false); //Tắt cache để giải phóng bộ nhớ

                // 2 Lưu ảnh vào thư viện điện thoại
                try {
                    // 3. Khởi tạo đường dẫn cho ảnh
                    String savedImageURL;

                    // 4 Kiểm tra phiên bản Androi
                    if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q){
                        // với Androi 10 trở lên , sử dụng MediaStore

                        ContentResolver resolver = getContentResolver();
                        // Lấy đối tượng ContenResolver để thao tác với MediaStore

                        ContentValues contentValues = new ContentValues();
                        // Khởi taoj đối tượng để chứa thông tin ảnh

                        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "QRCode.png"); // Tên ảnh
                        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");  //Định dạng file ảnh
                        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/QRCode"); // Đường dẫn đến

                        // tạo Uri cho ảnh mới trong MediaStore
                        Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);


                        // 5. Ghi ảnh vào MediaStore
                        if(imageUri != null){
                            OutputStream outputStream = resolver.openOutputStream(imageUri);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                            outputStream.close();
                            savedImageURL = outputStream.toString(); // lưu đường dẫn
                        } else {
                            Toast.makeText(ThanhToanOnlActivity.this, "Lưu ảnh thất bại", Toast.LENGTH_SHORT).show();
                            return;
                        }



                    } else {
                        // với phiên bản Android thấp hơn Androi 10, sử dụng File
                        File file = new File(Environment.getExternalStorageDirectory() + "/QRCode.png");
                        savedImageURL = file.getAbsolutePath();
                    }
                    // 6. Scan ảnh vào thư viện điện thoại
                    Toast.makeText(ThanhToanOnlActivity.this,"Ảnh được lưu tại: " + savedImageURL,Toast.LENGTH_SHORT).show();
                }catch(Exception e){
                    e.printStackTrace();
                    Toast.makeText(ThanhToanOnlActivity.this, "Lưu ảnh thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void loadCartAndRestaurantData(String userId){
        cartdatabaseReference.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot cartSnapshot : dataSnapshot.getChildren()) {
                        // Lấy restaurant_id từ giỏ hàng
                        String restaurantId = cartSnapshot.child("restaurant_id").getValue(String.class);
                        String itemName = cartSnapshot.child("name").getValue(String.class);
                        double itemPrice = cartSnapshot.child("price").getValue(double.class);

                        Log.d("CartItem", "Item: " + itemName + ", Price: " + itemPrice);

                        // Lấy thông tin nhà hàng từ restaurantId
                        if (restaurantId != null) {
                            loadRestaurantData(restaurantId, itemName, itemPrice);
                        }
                    }
                } else {
                    Toast.makeText(ThanhToanOnlActivity.this, "Giỏ hàng trống!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseError", databaseError.getMessage());
            }
        });

    }

    private void loadRestaurantData(String restaurantId, String restaurantID, double itemPrice) {
        databaseReference.child(restaurantId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Restaurant restaurant = snapshot.getValue(Restaurant.class);

                    if (restaurant != null) {
                        // Hiển thị dữ liệu lên giao diện
                        name.setText(restaurant.getName());
                        stk.setText(restaurant.getStk());
                        price.setText(String.valueOf(itemPrice)); // Giá của sản phẩm trong giỏ hàng

                        Glide.with(ThanhToanOnlActivity.this)
                                .load(restaurant.getQrcodeUrl())
                                .into(qrcode);
                    }
                } else {
                    Toast.makeText(ThanhToanOnlActivity.this, "Không tìm thấy nhà hàng!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseError", databaseError.getMessage());
            }
        });
    }

    private void fetchRestaurantIdsForUser(String userId) {
        Log.d("RestaurantID", "Fetching restaurant IDs for userId: " + userId);

        // Access the specific UserID node using the provided userId
        cartdatabaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot foodItemSnapshot : dataSnapshot.getChildren()) {
                        // Access restaurant_id under FoodID
                        String restaurantId = foodItemSnapshot.child("restaurant_id").getValue(String.class);
                        if (restaurantId != null) {
                            Log.d("RestaurantID", "Found restaurant_id: " + restaurantId);
                        } else {
                            Log.w("RestaurantID", "No restaurant_id found for this item.");
                        }
                    }
                } else {
                    Log.w("Cart", "No data found for userId: " + userId);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseError", "Failed to fetch cart data: " + databaseError.getMessage());
            }
        });
    }



}