package com.example.food;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

        int foodId = getIntent().getIntExtra("foodID", -1);
        if (foodId != -1) {
            fetchFoodData(foodId);
        }

        returnBtn.setOnClickListener(v -> finish());
        increaseQuantityBtn.setOnClickListener(v -> updateQuantity(1));
        decreaseQuantityBtn.setOnClickListener(v -> updateQuantity(-1));
        addToCartBtn.setOnClickListener(v -> setAddToCart(foodId));
    }

//    private void fetchFoodData(int foodId) {
//        Toast.makeText(this, "Food detail: " + foodId, Toast.LENGTH_SHORT).show();
//
//        // Listen for the food item with the given ID
//        databaseReference.child(String.valueOf(foodId))
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot snapshot) {
//                        if (snapshot.exists()) {
//                            // Convert the snapshot into a Food object
//                            Food food = snapshot.getValue(Food.class);
//
//                            if (food != null) {
//                                // Update UI with the fetched data
//                                foodName.setText(food.getName());
//                                foodPrice.setText(String.format("$%.2f", food.getPrice()));
//                                foodDescription.setText(food.getIngredients());
//                                // Load the image into foodImage (use a library like Glide or Picasso)
//                                // Glide.with(FoodDetailActivity.this).load(food.getImageUrl()).into(foodImage);
//                            }
//                        } else {
//                            Toast.makeText(FoodDetailActivity.this, "Food not found", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError error) {
//                        Log.e("FoodDetailActivity", "Error fetching data: " + error.getMessage());
//                        Toast.makeText(FoodDetailActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }

    private void fetchFoodData(int foodId) {
        Toast.makeText(this, "Fetching food detail: " + foodId, Toast.LENGTH_SHORT).show();

        // Iterate through all food items
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean found = false;
                for (DataSnapshot foodSnapshot : snapshot.getChildren()) {
                    Food food = foodSnapshot.getValue(Food.class);

                    if (food != null && food.getId() == foodId) {
                        // Update UI with the fetched data
                        foodName.setText(food.getName());
                        foodPrice.setText(String.format("$%.2f", food.getPrice()));
                        foodDescription.setText(food.getIngredients());
                        // Load the image into foodImage (use a library like Glide or Picasso)
                        // Glide.with(FoodDetailActivity.this).load(food.getImageUrl()).into(foodImage);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    Toast.makeText(FoodDetailActivity.this, "Food not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("FoodDetailActivity", "Error fetching data: " + error.getMessage());
                Toast.makeText(FoodDetailActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updateQuantity(int change) {
        currentQuantity += change;
        if (currentQuantity < 1) currentQuantity = 1;
        quantity.setText(String.format("%02d", currentQuantity));
    }

    private void setAddToCart(int foodId) {
        // Code to add to cart
        Intent intent = new Intent(FoodDetailActivity.this, FoodCartActivity.class);
        startActivity(intent);
    }
}
