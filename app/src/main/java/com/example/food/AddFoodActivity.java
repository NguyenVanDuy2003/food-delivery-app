package com.example.food;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Button;
import androidx.annotation.Nullable;

import com.example.food.Model.Food;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.UUID;

public class AddFoodActivity extends Activity {

    private ImageView foodImageView;
    private EditText foodNameEditText;
    private EditText foodPriceEditText;
    private EditText foodIngredientsEditText;
    private Button uploadImageButton;
    private Button saveFoodButton;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    // Firebase references
    private FirebaseStorage firebaseStorage;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference foodDatabaseRef;

    private String restaurantID; // Removed static, use an instance variable instead

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        // Initialize Firebase
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        foodDatabaseRef = firebaseDatabase.getReference("Food");

        // Initialize views
        foodImageView = findViewById(R.id.foodImageView);
        foodNameEditText = findViewById(R.id.foodNameEditText);
        foodPriceEditText = findViewById(R.id.foodPriceEditText);
        foodIngredientsEditText = findViewById(R.id.foodIngredientsEditText);
        uploadImageButton = findViewById(R.id.uploadImageButton);
        saveFoodButton = findViewById(R.id.saveFoodButton);

        // Get the restaurantID from the Intent, and assign it to a final variable
        restaurantID = getIntent().getStringExtra("restaurantID");

        // Set the onClickListener for the upload image button
        uploadImageButton.setOnClickListener(v -> onUploadImageClick());

        // Set the onClickListener for the save food button
        saveFoodButton.setOnClickListener(v -> saveFood());
    }

    // Method for the return button click (back navigation)
    public void onReturnButtonClick(View view) {
        onBackPressed();
    }

    // Method to handle image upload action
    private void onUploadImageClick() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            foodImageView.setImageURI(imageUri);
        }
    }

    private void saveFood() {
        // Disable the save button to prevent multiple clicks
        saveFoodButton.setEnabled(false);

        String foodName = foodNameEditText.getText().toString();
        String foodPriceStr = foodPriceEditText.getText().toString();
        String foodIngredients = foodIngredientsEditText.getText().toString();

        if (foodName.isEmpty() || foodPriceStr.isEmpty() || foodIngredients.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Please fill all fields and upload an image", Toast.LENGTH_SHORT).show();
            saveFoodButton.setEnabled(true); // Re-enable the button
            return;
        }

        // Convert food price to double
        final double foodPrice;  // Mark it as final
        try {
            foodPrice = Double.parseDouble(foodPriceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price format", Toast.LENGTH_SHORT).show();
            saveFoodButton.setEnabled(true); // Re-enable the button
            return;
        }

        // Upload image to Firebase Storage
        StorageReference storageRef = firebaseStorage.getReference("food_images/" + UUID.randomUUID().toString());
        UploadTask uploadTask = storageRef.putFile(imageUri);

        uploadTask.addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // Get the image URL after upload
            String imageUrl = uri.toString();

            // Create a food object without id (since id will be set after saving)
            Food food = new Food(foodName, foodPrice, imageUrl, restaurantID, foodIngredients, true, new ArrayList<>());

            // Generate a unique key for the food
            String foodId = foodDatabaseRef.push().getKey();
            food.setId(foodId); // Set the food's id

            // Save the food data to Firebase Realtime Database with foodId as key
            if (foodId != null) {
                foodDatabaseRef.child(foodId).setValue(food)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(AddFoodActivity.this, "Food added successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(AddFoodActivity.this, "Failed to add food", Toast.LENGTH_SHORT).show();
                            }
                            saveFoodButton.setEnabled(true); // Re-enable the button
                        });
            } else {
                saveFoodButton.setEnabled(true); // Re-enable the button if foodId is null
            }
        })).addOnFailureListener(e -> {
            Toast.makeText(AddFoodActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();
            saveFoodButton.setEnabled(true); // Re-enable the button
        });
    }



}
