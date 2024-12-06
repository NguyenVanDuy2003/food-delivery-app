package com.example.food;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.food.Model.Food;
import com.example.food.adapter.foodAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CategoryActivity extends AppCompatActivity {

    ListView lvFood;
    ArrayList<Food> listFood;
    foodAdapter adapterfood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fastfood);

        // Xử lý chức năng thoát khi nhấn nút back
        ImageButton backbutton = findViewById(R.id.back);
        backbutton.setOnClickListener(view -> onBackPressed());

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
                String selectedSort = parentView.getItemAtPosition(position).toString();
                Toast.makeText(CategoryActivity.this, "Selected: " + selectedSort, Toast.LENGTH_SHORT).show();

                // Sắp xếp theo tùy chọn của người dùng
                switch (selectedSort) {
                    case "Low to High":
                        Collections.sort(listFood, new Comparator<Food>() {
                            @Override
                            public int compare(Food f1, Food f2) {
                                return Double.compare(f1.getPrice(), f2.getPrice());
                            }
                        });
                        break;

                    case "High to Low":
                        Collections.sort(listFood, new Comparator<Food>() {
                            @Override
                            public int compare(Food f1, Food f2) {
                                return Double.compare(f2.getPrice(), f1.getPrice());
                            }
                        });
                        break;

                    case "Popular":
                        // Handle other sorting logic if needed
                        break;
                }

                // Cập nhật lại adapter sau khi sắp xếp
                adapterfood.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // No action needed
            }
        });

        lvFood = findViewById(R.id.lvFood);

        // Khởi tạo danh sách các món ăn
        listFood = new ArrayList<>();

        // Thêm dữ liệu vào danh sách
        listFood.add(new Food("Chicken Hawaiian", 10.35, 101, 1, R.drawable.a2, "Chicken, Cheese and Pineapple", true, new ArrayList<>()));
        listFood.add(new Food("Pepperoni Pizza", 9.99, 102, 1, R.drawable.a3, "Pepperoni, Cheese", true, new ArrayList<>()));

        // Khởi tạo adapter và gán cho ListView
        adapterfood = new foodAdapter(this, R.layout.fast_food_item, listFood);
        lvFood.setAdapter(adapterfood);


    }
}
