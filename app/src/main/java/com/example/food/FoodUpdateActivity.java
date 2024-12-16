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

        // Khởi tạo tham chiếu Firebase
        foodDatabaseReference = FirebaseDatabase.getInstance().getReference("Food");

        // Ánh xạ
        editTextName = findViewById(R.id.editTextName);
        editTextPrice = findViewById(R.id.editTextPrice);
        editTextIngredients = findViewById(R.id.editTextIngredients);
        imageViewFood = findViewById(R.id.imageViewFood);
        chooseImageButton = findViewById(R.id.chooseImageButton);
        saveButton = findViewById(R.id.saveButton);

        // Lấy đối tượng món ăn từ activity trước
        Intent intent = getIntent();
        food = (Food) intent.getSerializableExtra("food");

        // Điền dữ liệu hiện tại vào các view
        if (food != null) {
            editTextName.setText(food.getName());
            editTextPrice.setText(String.valueOf(food.getPrice()));
            editTextIngredients.setText(food.getIngredients());
            Glide.with(this)
                    .load(food.getImageUrl()) // Load the image URL using Glide
                    .into(imageViewFood);
        }

        // Đặt hành động cho nút chọn hình ảnh
        chooseImageButton.setOnClickListener(v -> {
            // Mở intent chọn hình ảnh
            Intent pickImageIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickImageIntent, PICK_IMAGE_REQUEST);
        });

        // Đặt hành động cho nút lưu
        saveButton.setOnClickListener(v -> {
            // Lấy dữ liệu cập nhật từ các trường EditText
            String updatedName = editTextName.getText().toString();
            String updatedPrice = editTextPrice.getText().toString();
            String updatedIngredients = editTextIngredients.getText().toString();

            // Kiểm tra tính hợp lệ của dữ liệu nhập vào
            if (updatedName.isEmpty() || updatedPrice.isEmpty() || updatedIngredients.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Cập nhật đối tượng món ăn
            if (food != null) {
                food.setName(updatedName);
                food.setPrice(Double.parseDouble(updatedPrice));
                food.setIngredients(updatedIngredients);

                // Nếu có hình ảnh mới được chọn, cập nhật URL hình ảnh
                if (selectedImageUri != null) {
                    // Chuyển đổi URI thành chuỗi (đường dẫn tệp hoặc URI)
                    food.setImageUrl(selectedImageUri.toString());
                }

                // Cập nhật dữ liệu món ăn trong Firebase
                foodDatabaseReference.child(food.getId()).setValue(food)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(FoodUpdateActivity.this, "Food updated successfully", Toast.LENGTH_SHORT).show();
                                // Trả về đối tượng món ăn đã cập nhật cho activity trước
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("updatedFood", food);
                                setResult(RESULT_OK, resultIntent);
                                finish();
                            } else {
                                Toast.makeText(FoodUpdateActivity.this, "Failed to update food", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    // Xử lý kết quả từ trình chọn hình ảnh
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE_REQUEST) {
            if (data != null) {
                selectedImageUri = data.getData();   // Lấy URI của hình ảnh đã chọn
                Glide.with(this)
                        .load(selectedImageUri)  // Tải hình ảnh từ URI bằng Glide
                        .into(imageViewFood);
            }
        }
    }
}
