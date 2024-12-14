package com.example.food;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.food.Common.CommonKey;
import com.example.food.Model.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CheckoutActivity extends AppCompatActivity {
    private Button btnthanhtoanOnline, btnthanhtoanKhiNhanHang , btnquaylai;
    private DatabaseReference cartRef, userRef;
    private String currentUserId;
    private String userName;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnthanhtoanOnline = findViewById(R.id.btn_ThanhToanOnline);
        btnthanhtoanKhiNhanHang = findViewById(R.id.btn_ThanhToanKhiNhanHang);
        btnquaylai = findViewById(R.id.btn_quaylai);

        String totalValue = getIntent().getStringExtra("totalValue");
        SharedPreferences sharedPreferences = getSharedPreferences(CommonKey.MY_APP_PREFS, MODE_PRIVATE);
        String currentUserId = sharedPreferences.getString(CommonKey.USER_ID, null);
        cartRef = FirebaseDatabase.getInstance().getReference("Cart");

        btnthanhtoanOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createOrder(currentUserId, "Thanh toán online");
                Intent intent = new Intent(CheckoutActivity.this, ThanhToanOnlActivity.class );
                intent.putExtra("totalValue", totalValue);
                startActivity(intent);
            }
        });

        btnthanhtoanKhiNhanHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createOrder(currentUserId, "Thanh toán khi nhận hàng");
                // Hiển thị thông báo bạn có đồng ý hay hủy
                //Tạo một AlertDialog.Builder
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Xác nhận");
                builder.setMessage("Bạn có muốn thanh toán khi nhận hàng?");

                // Nut "Dong y"
                builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Code nếu người dùng chọn nút "Dong y"
                        Toast.makeText(v.getContext(), "Bạn đã chọn thanh toán khi nhận hàng",Toast.LENGTH_SHORT).show();
                        dialog.dismiss(); // đóng hộp hội thoại
                        Intent intent = new Intent(CheckoutActivity.this, HomeActivity.class);
                        startActivity(intent);  // Start the HomeActivity
                        finish();
                    }

                });

                // Nut "Hủy"
                builder.setNegativeButton("Hủy", (dialog, which) -> {
                    // Code nếu người dùng chọn nút "Hủy"
                });

                // Tạo và hiển thị dialog
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        btnquaylai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close the checkout activity
            }
        });
    }

    private void createOrder(String userId, String paymentMethod) {
        cartRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Create a map to store items grouped by restaurant
                    Map<String, List<Order>> restaurantOrders = new HashMap<>();

                    for (DataSnapshot foodItemSnapshot : dataSnapshot.getChildren()) {
                        String restaurantId = foodItemSnapshot.child("restaurant_id").getValue(String.class);
                        if (restaurantId == null) {
                            Toast.makeText(CheckoutActivity.this, "Restaurant ID is null", Toast.LENGTH_SHORT).show();
                            Log.e("RestaurantID", "Restaurant ID is null");
                            return;
                        }

                        // Create the order item for each cart item
                        String dishName = foodItemSnapshot.child("name").getValue(String.class);
                        Integer quantity = foodItemSnapshot.child("quantity").getValue(Integer.class);
                        Double pricePerDish = foodItemSnapshot.child("price").getValue(Double.class);
                        String dishImg = foodItemSnapshot.child("imageUrl").getValue(String.class);

                        if (dishName != null && quantity != null && pricePerDish != null) {
                            String orderId = "ORD_" + restaurantId + "_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString(); // More robust unique order ID

                            Order order = new Order(
                                    orderId,  // Unique Order ID
                                    dishName,                            // Dish name
                                    quantity,                            // Quantity
                                    pricePerDish,                        // Price per dish
                                    String.valueOf(System.currentTimeMillis()),  // Order date
                                    "",                                  // User name placeholder (fetch later)
                                    paymentMethod,                       // Payment method
                                    dishImg                              // Dish image URL
                            );

                            // Group the orders by restaurant ID
                            if (!restaurantOrders.containsKey(restaurantId)) {
                                restaurantOrders.put(restaurantId, new ArrayList<>());
                            }
                            restaurantOrders.get(restaurantId).add(order);

                            // Log each item being added
                            Log.d("OrderAdded", "Added order: " + order.toString());
                        }
                    }

                    // Now process the orders for each restaurant
                    for (Map.Entry<String, List<Order>> entry : restaurantOrders.entrySet()) {
                        String restaurantId = entry.getKey();
                        List<Order> orders = entry.getValue();

                        // Get the user's name
                        userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
                        userRef.child("fullName").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                userName = dataSnapshot.getValue(String.class);
                                if (userName != null) {
                                    Log.d("UserName", "User name: " + userName);
                                } else {
                                    Log.w("UserName", "User name not found.");
                                }

                                // Create orders for the restaurant
                                for (Order order : orders) {
                                    order.setOrdererName(userName);  // Set the user name for the order

                                    // Save the order to the database under the specific restaurant
                                    DatabaseReference orderReference = FirebaseDatabase.getInstance().getReference("Order")
                                            .child(restaurantId).child(order.getOrderId());
                                    orderReference.setValue(order);

                                    Log.d("OrderCreated", "Created order: " + order.toString());
                                }

                                // After all orders are processed, clear the cart
                                cartRef.child(userId).removeValue();
                                Toast.makeText(CheckoutActivity.this, "Orders created successfully!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e("FirebaseError", "Error fetching user name: " + databaseError.getMessage());
                            }
                        });
                    }
                } else {
                    Toast.makeText(CheckoutActivity.this, "No items in the cart.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseError", "Failed to fetch cart data: " + databaseError.getMessage());
            }
        });
    }



}