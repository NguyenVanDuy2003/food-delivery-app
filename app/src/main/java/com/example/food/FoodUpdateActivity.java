package com.example.food;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.food.Model.Food;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FoodUpdateActivity extends Activity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText editTextName, editTextPrice, editTextIngredients;
    private ImageView imageViewFood;
    private Button chooseImageButton, saveButton;

    private Food food;
    private Uri selectedImageUri;

    private DatabaseReference foodDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_update);

        // Initialize the Firebase reference
        foodDatabaseReference = FirebaseDatabase.getInstance().getReference("Food");

        // Initialize the views
        editTextName = findViewById(R.id.editTextName);
        editTextPrice = findViewById(R.id.editTextPrice);
        editTextIngredients = findViewById(R.id.editTextIngredients);
        imageViewFood = findViewById(R.id.imageViewFood);
        chooseImageButton = findViewById(R.id.chooseImageButton);
        saveButton = findViewById(R.id.saveButton);

        // Get the food object passed from the previous activity
        Intent intent = getIntent();
        food = (Food) intent.getSerializableExtra("food");

        // Populate the views with the current data
        if (food != null) {
            editTextName.setText(food.getName());
            editTextPrice.setText(String.valueOf(food.getPrice()));
            editTextIngredients.setText(food.getIngredients());
            Glide.with(this)
                    .load(food.getImageUrl()) // Load the image URL using Glide
                    .into(imageViewFood);
        }

        // Set the action for the choose image button
        chooseImageButton.setOnClickListener(v -> {
            // Open the image picker intent
            Intent pickImageIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickImageIntent, PICK_IMAGE_REQUEST);
        });

        // Set the action for the save button
        saveButton.setOnClickListener(v -> {
            // Get the updated data from the EditText fields
            String updatedName = editTextName.getText().toString();
            String updatedPrice = editTextPrice.getText().toString();
            String updatedIngredients = editTextIngredients.getText().toString();

            // Validate inputs
            if (updatedName.isEmpty() || updatedPrice.isEmpty() || updatedIngredients.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update the food object
            if (food != null) {
                food.setName(updatedName);
                food.setPrice(Double.parseDouble(updatedPrice));
                food.setIngredients(updatedIngredients);

                // If a new image was selected, update the image URL
                if (selectedImageUri != null) {
                    // Convert URI to string (file path or URI)
                    food.setImageUrl(selectedImageUri.toString());
                }

                // Update the food data in Firebase
                foodDatabaseReference.child(food.getId()).setValue(food)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(FoodUpdateActivity.this, "Food updated successfully", Toast.LENGTH_SHORT).show();
                                // Return the updated food object to the previous activity
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("updatedFood", food);
                                setResult(RESULT_OK, resultIntent);
                                finish(); // Close the update activity
                            } else {
                                Toast.makeText(FoodUpdateActivity.this, "Failed to update food", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    // Handle the result from image picker
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE_REQUEST) {
            if (data != null) {
                selectedImageUri = data.getData();  // Get URI of the selected image
                Glide.with(this)
                        .load(selectedImageUri)  // Load the image from URI using Glide
                        .into(imageViewFood);
            }
        }
    }
}
