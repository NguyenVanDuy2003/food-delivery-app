package com.example.food;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.food.Model.Food;
import com.example.food.adapter.foodAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CategoryActivity extends AppCompatActivity {

    ListView lvFood;
    ArrayList<Food> listFood;
    foodAdapter adapterFood;
    FirebaseDatabase database;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fastfood);

        // Xử lý chức năng thoát khi nhấn nút back
        ImageButton backButton = findViewById(R.id.back);
        backButton.setOnClickListener(view -> onBackPressed());

        // Bước 1: Tạo các tùy chọn sắp xếp
        String[] sortOptions = {"Mặc định", "Thấp đến Cao", "Cao đến Thấp"};

        // Bước 2: Tìm Spinner trong bố cục
        Spinner spinnerSort = findViewById(R.id.spinnerSort);

        // Bước 3: Tạo ArrayAdapter cho Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, sortOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Bước 4: Đặt Adapter cho Spinner
        spinnerSort.setAdapter(adapter);

        // Bước 5: Xử lý việc chọn mục từ SpinneR
        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedSort = parentView.getItemAtPosition(position).toString();
                Toast.makeText(CategoryActivity.this, "Selected: " + selectedSort, Toast.LENGTH_SHORT).show();

                // Sắp xếp dựa trên lựa chọn của người dùng
                switch (selectedSort) {
                    case "Thấp đến Cao":
                        Collections.sort(listFood, new Comparator<Food>() {
                            @Override
                            public int compare(Food f1, Food f2) {
                                return Double.compare(f1.getPrice(), f2.getPrice());
                            }
                        });
                        break;

                    case "Cao đến Thấp":
                        Collections.sort(listFood, new Comparator<Food>() {
                            @Override
                            public int compare(Food f1, Food f2) {
                                return Double.compare(f2.getPrice(), f1.getPrice());
                            }
                        });
                        break;

                    case "Mặc định":
                        break;
                }

                // Update the adapter after sorting
                if (adapterFood != null) {
                    adapterFood.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        // Khởi tạo Firebase
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Food");

        String restaurantID = getIntent().getStringExtra("restaurantID");
        lvFood = findViewById(R.id.lvFood);

        listFood = new ArrayList<>();

        // Tải dữ liệu thực phẩm từ Firebase Realtime Database
        loadFoodData(restaurantID);
    }

    private void loadFoodData(String restaurantID) {
        // Lấy dữ liệu thực phẩm từ Firebase Realtime Database
        databaseReference.orderByChild("restaurantID").equalTo(restaurantID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                listFood.clear();

                for (DataSnapshot foodSnapshot : snapshot.getChildren()) {
                    Food food = foodSnapshot.getValue(Food.class);
                    if (food != null) {
                        listFood.add(food);
                    }
                }

                TextView tvEmptyMessage = findViewById(R.id.tvEmptyMessage);
                if (listFood.isEmpty()) {
                    lvFood.setVisibility(View.GONE);
                    tvEmptyMessage.setVisibility(View.VISIBLE);
                } else {
                    lvFood.setVisibility(View.VISIBLE);
                    tvEmptyMessage.setVisibility(View.GONE);
                }

                // Thiết lập adapter sau khi lấy dữ liệu
                adapterFood = new foodAdapter(CategoryActivity.this, R.layout.fast_food_item, listFood);
                lvFood.setAdapter(adapterFood);

                // Thông báo cho adapter rằng dữ liệu đã thay đổi
                if (adapterFood != null) {
                    adapterFood.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(CategoryActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
