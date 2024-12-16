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

    private String restaurantID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        // Khởi tạo Firebase
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        foodDatabaseRef = firebaseDatabase.getReference("Food");

        // Ánh xạ
        foodImageView = findViewById(R.id.foodImageView);
        foodNameEditText = findViewById(R.id.foodNameEditText);
        foodPriceEditText = findViewById(R.id.foodPriceEditText);
        foodIngredientsEditText = findViewById(R.id.foodIngredientsEditText);
        uploadImageButton = findViewById(R.id.uploadImageButton);
        saveFoodButton = findViewById(R.id.saveFoodButton);

        // Lấy restaurantID từ Intent, và gán nó cho một biến cuối cùng
        restaurantID = getIntent().getStringExtra("restaurantID");

        // Đặt onClickListener cho nút tải lên hình ảnh
        uploadImageButton.setOnClickListener(v -> onUploadImageClick());

        // Đặt onClickListener cho nút lưu thực phẩm
        saveFoodButton.setOnClickListener(v -> saveFood());
    }

    // Phương thức cho sự kiện nhấn nút quay lại (back navigation)
    public void onReturnButtonClick(View view) {
        onBackPressed();
    }

    // Phương thức để xử lý hành động tải lên hình ảnh
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
        // Vô hiệu hóa nút lưu để ngăn chặn nhiều lần nhấp chuột
        saveFoodButton.setEnabled(false);

        String foodName = foodNameEditText.getText().toString();
        String foodPriceStr = foodPriceEditText.getText().toString();
        String foodIngredients = foodIngredientsEditText.getText().toString();

        if (foodName.isEmpty() || foodPriceStr.isEmpty() || foodIngredients.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Please fill all fields and upload an image", Toast.LENGTH_SHORT).show();
            saveFoodButton.setEnabled(true); // Re-enable the button
            return;
        }

        // Chuyển đổi giá thực phẩm thành số thực
        final double foodPrice;  // Mark it as final
        try {
            foodPrice = Double.parseDouble(foodPriceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price format", Toast.LENGTH_SHORT).show();
            saveFoodButton.setEnabled(true); // Re-enable the button
            return;
        }

        // Tải hình ảnh lên Firebase Storage
        StorageReference storageRef = firebaseStorage.getReference("food_images/" + UUID.randomUUID().toString());
        UploadTask uploadTask = storageRef.putFile(imageUri);

        uploadTask.addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // Lấy URL hình ảnh sau khi tải lên
            String imageUrl = uri.toString();

            // Tạo một đối tượng thực phẩm không có id (since id will be set after saving)
            Food food = new Food(foodName, foodPrice, imageUrl, restaurantID, foodIngredients, true, new ArrayList<>());

            // Tạo một khóa duy nhất cho món ăn
            String foodId = foodDatabaseRef.push().getKey();
            food.setId(foodId);

            // Lưu dữ liệu thực phẩm vào Firebase Realtime Database với foodId làm khóa
            if (foodId != null) {
                foodDatabaseRef.child(foodId).setValue(food)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(AddFoodActivity.this, "Food added successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(AddFoodActivity.this, "Failed to add food", Toast.LENGTH_SHORT).show();
                            }
                            saveFoodButton.setEnabled(true);
                        });
            } else {
                saveFoodButton.setEnabled(true);
            }
        })).addOnFailureListener(e -> {
            Toast.makeText(AddFoodActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();
            saveFoodButton.setEnabled(true);
        });
    }



}
