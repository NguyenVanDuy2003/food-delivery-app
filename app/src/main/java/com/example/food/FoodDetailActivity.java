package com.example.food;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.food.model.Food;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class FoodDetailActivity extends AppCompatActivity {

    private TextView foodName, foodPrice, foodDescription;
    private ImageView foodImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        foodName = findViewById(R.id.food_name);
        foodPrice = findViewById(R.id.price_tag);
        foodDescription = findViewById(R.id.ingredient_txt);
        foodImage = findViewById(R.id.food_image);

        int foodId = getIntent().getIntExtra("foodID", -1);

        if (foodId != -1) {
             fetchFoodDetails(foodId);
        } else {
            Toast.makeText(this, "Food ID not provided", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void fetchFoodDetails(int foodId) {
        DatabaseReference foodRef = FirebaseDatabase.getInstance().getReference("Foods").child(String.valueOf(foodId));

        foodRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                DataSnapshot snapshot = task.getResult();
                Food food = snapshot.getValue(Food.class);

                if (food != null) {
                    foodName.setText(food.getNameFood());
                    foodPrice.setText(String.format("$%.2f", food.getPrice()));
                    foodDescription.setText(food.getDescription());
                    Picasso.get().load(food.getImage()).into(foodImage); // Assuming image is a URL
                }
            } else {
                Toast.makeText(this, "Failed to load food details", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}
