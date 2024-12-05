package com.example.food;

import android.content.Intent;
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
import androidx.appcompat.app.AppCompatActivity;

import com.example.food.Model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
                            // Hiển thị ảnh đầu tiên trong danh sách
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

            if (name.isEmpty() || phoneNumber.isEmpty() || email.isEmpty() || address.isEmpty()) {
                Toast.makeText(ProfileActivity.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedImgUris.isEmpty()) {
                Toast.makeText(ProfileActivity.this, "Vui lòng chọn ảnh!", Toast.LENGTH_SHORT).show();
                return;
            }

            String userId = databaseReference.push().getKey();
            if (userId == null) {
                Toast.makeText(ProfileActivity.this, "Lỗi khi tạo ID người dùng!", Toast.LENGTH_SHORT).show();
                return;
            }

            User newUser = new User(userId, name, phoneNumber, address, email);
            List<String> imageUrls = new ArrayList<>();

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
                            imageUrls.add(downloadUri.toString());

                            if (imageUrls.size() == selectedImgUris.size()) {
                                newUser.setImageUser(imageUrls.toString());
                                databaseReference.child(userId).setValue(newUser)
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
        });
    }
}