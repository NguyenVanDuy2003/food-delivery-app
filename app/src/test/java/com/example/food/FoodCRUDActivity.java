package com.example.food;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.food.model.Food;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FoodCRUDActivity extends AppCompatActivity {

    private EditText etFoodName, etFoodPrice, etFoodIngredients, etFoodImg;
    private CheckBox cbFoodAvailable;
    private Button btnAddFood, btnUpdateFood, btnDeleteFood;

    private DatabaseReference foodDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_crud);

        // Initialize Firebase Database reference
        foodDatabase = FirebaseDatabase.getInstance().getReference("foods");

        // Initialize UI components
        etFoodName = findViewById(R.id.et_food_name);
        etFoodPrice = findViewById(R.id.et_food_price);
        etFoodIngredients = findViewById(R.id.et_food_ingredients);
        etFoodImg = findViewById(R.id.et_food_img);
        cbFoodAvailable = findViewById(R.id.cb_food_available);
        btnAddFood = findViewById(R.id.btn_add_food);
        btnUpdateFood = findViewById(R.id.btn_update_food);
        btnDeleteFood = findViewById(R.id.btn_delete_food);

        // Set button listeners
        btnAddFood.setOnClickListener(v -> addFood());
        btnUpdateFood.setOnClickListener(v -> updateFood());
        btnDeleteFood.setOnClickListener(v -> deleteFood());
    }

    private void addFood() {
        String name = etFoodName.getText().toString().trim();
        String priceStr = etFoodPrice.getText().toString().trim();
        String ingredients = etFoodIngredients.getText().toString().trim();
        String imgUrl = etFoodImg.getText().toString().trim();
        boolean isAvailable = cbFoodAvailable.isChecked();

        // Validation
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(priceStr) || TextUtils.isEmpty(ingredients)) {
            Toast.makeText(this, "Please fill in all required fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);

        // Generate unique ID for the new food item
        String id = foodDatabase.push().getKey();

        // Create a new Food object
        Food food = new Food(name, price, Integer.parseInt(id), 0, ingredients, isAvailable, new ArrayList<>());

        // Add to Firebase
        foodDatabase.child(id).setValue(food)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Food added successfully!", Toast.LENGTH_SHORT).show();
                    clearFields();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to add food: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void updateFood() {
        String id = etFoodName.getTag() != null ? etFoodName.getTag().toString() : null; // Assume ID is stored as a tag for simplicity
        String name = etFoodName.getText().toString().trim();
        String priceStr = etFoodPrice.getText().toString().trim();
        String ingredients = etFoodIngredients.getText().toString().trim();
        String imgUrl = etFoodImg.getText().toString().trim();
        boolean isAvailable = cbFoodAvailable.isChecked();

        // Validation
        if (TextUtils.isEmpty(id)) {
            Toast.makeText(this, "Food ID is missing. Cannot update!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(priceStr) || TextUtils.isEmpty(ingredients)) {
            Toast.makeText(this, "Please fill in all required fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);

        // Create updated Food object
        Food updatedFood = new Food(name, price, Integer.parseInt(id), 0, ingredients, isAvailable, new ArrayList<>());

        // Update in Firebase
        foodDatabase.child(id).setValue(updatedFood)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Food updated successfully!", Toast.LENGTH_SHORT).show();
                    clearFields();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update food: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void deleteFood() {
        String id = etFoodName.getTag() != null ? etFoodName.getTag().toString() : null;

        // Validation
        if (TextUtils.isEmpty(id)) {
            Toast.makeText(this, "Food ID is missing. Cannot delete!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Delete from Firebase
        foodDatabase.child(id).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Food deleted successfully!", Toast.LENGTH_SHORT).show();
                    clearFields();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to delete food: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void clearFields() {
        etFoodName.setText("");
        etFoodPrice.setText("");
        etFoodIngredients.setText("");
        etFoodImg.setText("");
        cbFoodAvailable.setChecked(false);
        etFoodName.setTag(null); // Clear stored ID
    }
}
