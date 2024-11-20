package com.example.food;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.food.Model.food;
import com.example.food.adapter.foodAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class CategoryActivity extends AppCompatActivity {

    ListView lvFood;
    ArrayList<food> listFood;
    foodAdapter adapterfood;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fastfood);

        // Khởi tạo danh sách và adapter
        lvFood = findViewById(R.id.lvFood);
        listFood = new ArrayList<>();
        adapterfood = new foodAdapter(this, R.layout.fast_food_item, listFood);
        lvFood.setAdapter(adapterfood);

        // Khởi tạo Firebase Realtime Database
        databaseRef = FirebaseDatabase.getInstance().getReference("foods");

        // Xử lý chức năng thoát khi nhấn nút back
        ImageButton backbutton = findViewById(R.id.back);
        backbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // Bước 1: Tạo danh sách các tùy chọn
        String[] sortOptions = {"Popular", "Low to High", "High to Low"};

        // Bước 2: Tìm Spinner trong layout
        Spinner spinnerSort = findViewById(R.id.spinnerSort);

        // Bước 3: Tạo ArrayAdapter cho Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,  R.layout.spinner_item, sortOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Bước 4: Gán Adapter cho Spinner
        spinnerSort.setAdapter(adapter);

        // Bước 5: Xử lý sự kiện khi người dùng chọn một mục
        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Hiển thị lựa chọn của người dùng (tuỳ chọn)
                String selectedSort = parentView.getItemAtPosition(position).toString();
                Toast.makeText(CategoryActivity.this, "Selected: " + selectedSort, Toast.LENGTH_SHORT).show();

                // Sắp xếp theo tùy chọn của người dùng
                switch (selectedSort) {
                    case "Low to High":
                        // Sắp xếp danh sách theo giá từ thấp đến cao
                        Collections.sort(listFood, new Comparator<food>() {
                            @Override
                            public int compare(food f1, food f2) {
                                return Double.compare(f1.getPrice(), f2.getPrice());
                            }
                        });
                        break;

                    case "High to Low":
                        // Sắp xếp danh sách theo giá từ cao xuống thấp
                        Collections.sort(listFood, new Comparator<food>() {
                            @Override
                            public int compare(food f1, food f2) {
                                return Double.compare(f2.getPrice(), f1.getPrice());
                            }
                        });
                        break;

                    // Các tùy chọn khác...
                    case "Popular":
                        // Nếu có logic sắp xếp khác, thêm vào đây
                        break;
                }

                // Cập nhật lại adapter sau khi sắp xếp
                adapterfood.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Không làm gì nếu không có lựa chọn
            }
        });

        // Khởi tạo danh sách các món ăn
        listFood = new ArrayList<>();

        // Thêm dữ liệu vào danh sách
        listFood.add(new food("Chicken Hawaiian", new Date(), true, 10.35, "Chicken, Cheese and Pineapple", 1, 101, R.drawable.a2));
        listFood.add(new food("Pepperoni Pizza", new Date(), true, 9.99, "Pepperoni, Cheese", 1, 102, R.drawable.a3));
        listFood.add(new food("Chicken Hawaiian", new Date(), true, 10.35, "Chicken, Cheese and Pineapple", 1, 103, R.drawable.a2));
        listFood.add(new food("Pepperoni Pizza", new Date(), true, 9.99, "Pepperoni, Cheese", 1, 104, R.drawable.a3));
        listFood.add(new food("Chicken Hawaiian", new Date(), true, 10.35, "Chicken, Cheese and Pineapple", 1, 107, R.drawable.a2));
        listFood.add(new food("Pepperoni Pizza", new Date(), true, 9.99, "Pepperoni, Cheese", 1, 106, R.drawable.a3));

        // Khởi tạo adapter và gán cho ListView
        adapterfood = new foodAdapter(this, R.layout.fast_food_item, listFood);
        lvFood.setAdapter(adapterfood);

        uploadFoodDataToFirebase();
    }

    private void loadFoodDataFromFirebase() {
        // Lắng nghe sự kiện thay đổi dữ liệu từ Firebase
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Xóa danh sách cũ để tránh trùng lặp
                listFood.clear();

                // Lặp qua tất cả các con của nút "foods"
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Chuyển đổi từng phần tử về đối tượng food
                    food foodItem = snapshot.getValue(food.class);
                    if (foodItem != null) {
                        listFood.add(foodItem);
                    }
                }

                // Cập nhật adapter sau khi lấy dữ liệu
                adapterfood.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CategoryActivity.this, "Không thể tải dữ liệu: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void uploadFoodDataToFirebase() {
        // Reference to Firebase Realtime Database under "foods" node
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("foods");

        // Loop through listFood and upload each item
        for (food foodItem : listFood) {
            databaseRef.push().setValue(foodItem).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(CategoryActivity.this, "Món ăn đã được thêm thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    // Log detailed error if upload fails
                    String error = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                    Toast.makeText(CategoryActivity.this, "Không thể thêm món ăn: " + error, Toast.LENGTH_SHORT).show();
                    Log.e("FirebaseUpload", "Error uploading food item: " + error);
                }
            });
        }
    }
    private void checkDatabaseConnection() {
        DatabaseReference testRef = FirebaseDatabase.getInstance().getReference("test_connection");

        testRef.setValue("connected").addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("FirebaseConnection", "Kết nối đến database thành công.");
            } else {
                Log.e("FirebaseConnection", "Kết nối đến database thất bại: " + task.getException().getMessage());
            }
        });
    }






}
