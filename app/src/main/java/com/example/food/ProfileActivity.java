package com.example.food;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.food.Common.CommonKey;
import com.example.food.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    Button btn_editProfile;
    ImageButton imgBProfile;
    ImageView imgVProfile;
    EditText et_name, et_email, et_phoneNumber, et_address;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private List<Uri> selectedImgUris = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase references
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference("Users_images");

        // Retrieve user ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(CommonKey.MY_APP_PREFS, MODE_PRIVATE);
        String userId = sharedPreferences.getString(CommonKey.USER_ID, null);

        // Back button functionality
        Button btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish()); // Close the activity and return to the previous one

        if (userId != null) {
            // Query the database to get user information
            databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            // Populate the EditText fields with user data
                            et_name.setText(user.getFullName());
                            et_email.setText(user.getEmail());
                            et_phoneNumber.setText(user.getPhoneNumber());
                            et_address.setText(user.getAddress());

                            // Load profile image if available
                            if (user.getImageUser() != null && !user.getImageUser().isEmpty()) {
                                Glide.with(ProfileActivity.this)
                                     .load(user.getImageUser())
                                     .into(imgVProfile);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ProfileActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
        }

        // View mapping
        et_name = findViewById(R.id.et_name);
        et_email = findViewById(R.id.et_email);
        et_phoneNumber = findViewById(R.id.et_phoneNumber);
        et_address = findViewById(R.id.et_address);
        btn_editProfile = findViewById(R.id.btn_editProfie);
        imgBProfile = findViewById(R.id.imgBProfie);
        imgVProfile = findViewById(R.id.imgVProfie);

        // Image picker
        ActivityResultLauncher<Intent> selectImages = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        if (data.getClipData() != null) {
                            int count = data.getClipData().getItemCount();
                            selectedImgUris.clear();
                            for (int i = 0; i < count; i++) {
                                Uri imageUri = data.getClipData().getItemAt(i).getUri();
                                selectedImgUris.add(imageUri);
                            }
                            imgVProfile.setImageURI(selectedImgUris.get(0));
                            Toast.makeText(ProfileActivity.this, "Đã chọn " + count + " ảnh.", Toast.LENGTH_SHORT).show();
                        } else if (data.getData() != null) {
                            selectedImgUris.clear();
                            selectedImgUris.add(data.getData());
                            imgVProfile.setImageURI(selectedImgUris.get(0));
                            Toast.makeText(ProfileActivity.this, "Đã chọn 1 ảnh.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        imgBProfile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            selectImages.launch(intent);
        });

        btn_editProfile.setOnClickListener(v -> {
            String name = et_name.getText().toString();
            String phoneNumber = et_phoneNumber.getText().toString();
            String email = et_email.getText().toString();
            String address = et_address.getText().toString();

            // Use the retrieved user ID from SharedPreferences
            if (userId == null) {
                Toast.makeText(ProfileActivity.this, "Lỗi khi lấy ID người dùng!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create a User object with the updated fields
            User updatedUser = new User();
            updatedUser.setId(userId);
            if (!name.isEmpty()) updatedUser.setFullName(name);
            if (!email.isEmpty()) updatedUser.setEmail(email);
            if (!phoneNumber.isEmpty()) updatedUser.setPhoneNumber(phoneNumber);
            if (!address.isEmpty()) updatedUser.setAddress(address);

            StringBuilder imageUrls = new StringBuilder(); // Use StringBuilder to concatenate URLs

            // Check if new images are selected
            if (selectedImgUris.isEmpty()) {
                // If no new images are selected, keep the existing image URL
                databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            User existingUser = dataSnapshot.getValue(User.class);
                            if (existingUser != null && existingUser.getImageUser() != null) {
                                updatedUser.setImageUser(existingUser.getImageUser()); // Retain existing image URL
                            }
                        }
                        // Update the user profile in the database
                        databaseReference.child(userId).setValue(updatedUser)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(ProfileActivity.this, "Cập nhật hồ sơ thành công!", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(ProfileActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(ProfileActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // If new images are selected, upload them
                for (Uri uri : selectedImgUris) {
                    String fileName = userId + "_" + System.currentTimeMillis() + ".jpg";
                    StorageReference imageRef = storageReference.child(fileName);

                    imageRef.putFile(uri)
                            .continueWithTask(task -> {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }
                                return imageRef.getDownloadUrl();
                            })
                            .addOnSuccessListener(downloadUri -> {
                                if (imageUrls.length() > 0) {
                                    imageUrls.append(","); // Append a comma if there are already URLs
                                }
                                imageUrls.append(downloadUri.toString()); // Append the new URL

                                if (imageUrls.length() == selectedImgUris.size()) {
                                    updatedUser.setImageUser(imageUrls.toString()); // Set the concatenated string
                                    databaseReference.child(userId).setValue(updatedUser) // Update existing user
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(ProfileActivity.this, "Cập nhật hồ sơ thành công!", Toast.LENGTH_SHORT).show();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(ProfileActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            });
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(ProfileActivity.this, "Lỗi tải ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            }
        });
    }
}