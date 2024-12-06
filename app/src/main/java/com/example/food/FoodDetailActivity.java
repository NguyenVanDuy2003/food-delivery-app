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

import com.example.food.R;
import com.example.food.model.Food;


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
//        int foodId = 101;
        if (foodId != -1) {
            fetchFoodData(foodId);
        }

        // Set up button listeners
        returnBtn.setOnClickListener(v -> finish()); // Return to the previous screen
        increaseQuantityBtn.setOnClickListener(v -> updateQuantity(1));
        decreaseQuantityBtn.setOnClickListener(v -> updateQuantity(-1));
        addToCartBtn.setOnClickListener(v -> setAddToCart(foodId));
    }

    private void fetchFoodData(int foodId) {
        Toast.makeText(this, "Fetching food data...", Toast.LENGTH_SHORT).show();

        databaseReference.orderByChild("id").equalTo(foodId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Retrieve the food data
                        Food food = snapshot.getValue(Food.class);
                        if (food != null) {
                            quantity.setText(String.format("%02d", currentQuantity));
                            foodName.setText(food.getName());
                            foodPrice.setText(String.format("$%.2f", food.getPrice()));
                            foodDescription.setText(food.getIngredients());
                            foodImage.setImageResource(food.getImageResource());
                        }
                    }
                } else {
                    Toast.makeText(FoodDetailActivity.this, "Food not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(FoodDetailActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                Log.e("FoodDetailActivity", "Error fetching data: " + databaseError.getMessage());
            }
        });

    }

    private void updateQuantity(int change) {
        currentQuantity += change;
        currentQuantity = Math.max(currentQuantity, 1);
        quantity.setText(String.format("%02d", currentQuantity));
    }

    private void setAddToCart(int foodId) {
        Intent intent = new Intent(FoodDetailActivity.this, FoodCartActivity.class);
        startActivity(intent);
    }
}