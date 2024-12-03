package com.example.food;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class SellerActivity extends AppCompatActivity {

    private EditText storeName, storeProfile, foodName, foodDescription;
    private ImageView storeAvatar, storeCover, foodImage;
    private Button updateStoreButton, addFoodButton;
    private Uri avatarUri, coverUri, foodUri;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private ProgressDialog progressDialog;

    private static final int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("Sellers");
        storageReference = FirebaseStorage.getInstance().getReference("Sellers");

        // Initialize UI elements
        storeName = findViewById(R.id.store_name);
        storeProfile = findViewById(R.id.store_profile);
        foodName = findViewById(R.id.food_name);
        foodDescription = findViewById(R.id.food_description);
        storeAvatar = findViewById(R.id.store_avatar);
        storeCover = findViewById(R.id.store_cover);
        foodImage = findViewById(R.id.food_image);
        updateStoreButton = findViewById(R.id.update_store_button);
        addFoodButton = findViewById(R.id.add_food_button);

        progressDialog = new ProgressDialog(this);

        // Image selection
        storeAvatar.setOnClickListener(v -> pickImage("avatar"));
        storeCover.setOnClickListener(v -> pickImage("cover"));
        foodImage.setOnClickListener(v -> pickImage("food"));

        // Update store info
        updateStoreButton.setOnClickListener(v -> updateStoreInfo());

        // Add food
        addFoodButton.setOnClickListener(v -> addFood());
    }

    private void pickImage(String type) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);

        // Assign image type to handle in onActivityResult
        switch (type) {
            case "avatar":
                storeAvatar.setTag("avatar");
                break;
            case "cover":
                storeCover.setTag("cover");
                break;
            case "food":
                foodImage.setTag("food");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImage = data.getData();
            String tag = (String) storeAvatar.getTag();
            if (tag.equals("avatar")) {
                avatarUri = selectedImage;
                storeAvatar.setImageURI(avatarUri);
            } else if (tag.equals("cover")) {
                coverUri = selectedImage;
                storeCover.setImageURI(coverUri);
            } else if (tag.equals("food")) {
                foodUri = selectedImage;
                foodImage.setImageURI(foodUri);
            }
        }
    }

    private void updateStoreInfo() {
        String name = storeName.getText().toString();
        String profile = storeProfile.getText().toString();

        if (TextUtils.isEmpty(name) || avatarUri == null || coverUri == null) {
            Toast.makeText(this, "Please fill all fields and upload images", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Updating store...");
        progressDialog.show();

        StorageReference avatarRef = storageReference.child("avatars").child(System.currentTimeMillis() + ".jpg");
        avatarRef.putFile(avatarUri).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                avatarRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String avatarUrl = uri.toString();

                    StorageReference coverRef = storageReference.child("covers").child(System.currentTimeMillis() + ".jpg");
                    coverRef.putFile(coverUri).addOnCompleteListener(task2 -> {
                        if (task2.isSuccessful()) {
                            coverRef.getDownloadUrl().addOnSuccessListener(uri2 -> {
                                String coverUrl = uri2.toString();

                                HashMap<String, Object> storeMap = new HashMap<>();
                                storeMap.put("name", name);
                                storeMap.put("profile", profile);
                                storeMap.put("avatar", avatarUrl);
                                storeMap.put("cover", coverUrl);

                                databaseReference.child("StoreInfo").setValue(storeMap).addOnCompleteListener(task3 -> {
                                    if (task3.isSuccessful()) {
                                        progressDialog.dismiss();
                                        Toast.makeText(this, "Store updated successfully", Toast.LENGTH_SHORT).show();
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(this, "Failed to update store", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            });
                        }
                    });
                });
            }
        });
    }

    private void addFood() {
        String food = foodName.getText().toString();
        String description = foodDescription.getText().toString();

        if (TextUtils.isEmpty(food) || foodUri == null) {
            Toast.makeText(this, "Please fill all fields and upload an image", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Adding food...");
        progressDialog.show();

        StorageReference foodRef = storageReference.child("foods").child(System.currentTimeMillis() + ".jpg");
        foodRef.putFile(foodUri).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                foodRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String foodUrl = uri.toString();

                    HashMap<String, Object> foodMap = new HashMap<>();
                    foodMap.put("name", food);
                    foodMap.put("description", description);
                    foodMap.put("image", foodUrl);

                    databaseReference.child("Foods").push().setValue(foodMap).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(this, "Food added successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(this, "Failed to add food", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            }
        });
    }
}