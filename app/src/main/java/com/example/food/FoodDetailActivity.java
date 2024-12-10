package com.example.food;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.food.Common.CommonKey;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.example.food.Model.Food;

public class FoodDetailActivity extends AppCompatActivity {

    private TextView foodName, foodPrice, foodDescription, quantity;
    private ImageView foodImage;
    private Button returnBtn, addToCartBtn, increaseQuantityBtn, decreaseQuantityBtn;

    private int currentQuantity = 1;
    private DatabaseReference databaseReference;
    private Food food; // Hold the fetched food object

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        foodName = findViewById(R.id.food_name);
        foodPrice = findViewById(R.id.price_tag);
        foodDescription = findViewById(R.id.ingredient_txt);
        quantity = findViewById(R.id.quantity);
        foodImage = findViewById(R.id.food_image);

        returnBtn = findViewById(R.id.return_btn);
        addToCartBtn = findViewById(R.id.add_to_cart_btn);
        increaseQuantityBtn = findViewById(R.id.increase_quantity);
        decreaseQuantityBtn = findViewById(R.id.decrease_quantity);

        databaseReference = FirebaseDatabase.getInstance().getReference("Food");

        String foodId = getIntent().getStringExtra("foodID");
        fetchFoodData(foodId);

        returnBtn.setOnClickListener(v -> finish());
        increaseQuantityBtn.setOnClickListener(v -> updateQuantity(1));
        decreaseQuantityBtn.setOnClickListener(v -> updateQuantity(-1));
        addToCartBtn.setOnClickListener(v -> {
            if (food != null) {
                setAddToCart(food);
            } else {
                Toast.makeText(FoodDetailActivity.this, "Không thể fetch food details, food = null", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchFoodData(String foodId) {
        databaseReference.child(foodId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    food = snapshot.getValue(Food.class);

                    if (food != null) {
                        foodName.setText(food.getName());
                        foodPrice.setText(String.format("%.2f", food.getPrice()));
                        foodDescription.setText(food.getIngredients());

                        Glide.with(FoodDetailActivity.this).load(food.getImageUrl()).into(foodImage);
                    } else {
                        Toast.makeText(FoodDetailActivity.this, "Food = null", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(FoodDetailActivity.this, "Không tìm thấy món ăn.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("FoodDetailActivity", "Database error: " + error.getMessage());
                Toast.makeText(FoodDetailActivity.this, "Không thể load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateQuantity(int change) {
        currentQuantity += change;

        if (currentQuantity < 1) currentQuantity = 1;
        quantity.setText(String.format("%02d", currentQuantity));
    }

    private void setAddToCart(Food food) {
        SharedPreferences sharedPreferences = getSharedPreferences(CommonKey.MY_APP_PREFS, MODE_PRIVATE);
        String userId = sharedPreferences.getString(CommonKey.USER_ID, null);

        if (userId == null) {
            Toast.makeText(FoodDetailActivity.this, "Bạn chưa đăng nhập.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("Cart").child(userId).child(food.getId());

        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Integer existingQuantity = snapshot.child("quantity").getValue(Integer.class);
                    if (existingQuantity != null) {
                        cartRef.child("quantity").setValue(existingQuantity + currentQuantity);
                        Toast.makeText(FoodDetailActivity.this, "Đã cập nhật số lượng trong giỏ hàng", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(FoodDetailActivity.this, "Item tồn tại nhưng không có quantity", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    cartRef.child("id").setValue(food.getId());
                    cartRef.child("quantity").setValue(currentQuantity);
                    cartRef.child("price").setValue(food.getPrice());
                    cartRef.child("name").setValue(food.getName());
                    cartRef.child("imageUrl").setValue(food.getImageUrl());
                    Toast.makeText(FoodDetailActivity.this, "Đã thêm" + currentQuantity + " sản phẩm" + food.getName() + " vào giỏ hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("CartDebug", "Failed to add item to cart: " + error.getMessage());
                Toast.makeText(FoodDetailActivity.this, "Không thể thêm sản phẩm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
