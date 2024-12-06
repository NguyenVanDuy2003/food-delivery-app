package com.example.food;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.food.model.Food;
import com.example.food.adapter.foodAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class CategoryActivity extends AppCompatActivity {

    ListView lvFood;
    ArrayList<Food> listFood;
    foodAdapter adapterfood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fastfood);

        // Retrieve data from Intent
        String restaurantName = getIntent().getStringExtra("restaurant_name");
        int restaurantImage = getIntent().getIntExtra("restaurant_image", -1);
        float restaurantRating = getIntent().getFloatExtra("restaurant_rating", 0);
        String restaurantDeliveryTime = getIntent().getStringExtra("restaurant_delivery_time");

        // Use the retrieved data as needed
        // For example, you can set it to TextViews or ImageView in your layout
        // TextView nameTextView = findViewById(R.id.tv_restaurant_name);
        // nameTextView.setText(restaurantName);
        // ImageView imageView = findViewById(R.id.iv_restaurant);
        // imageView.setImageResource(restaurantImage);
        // ... and so on

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
                Log.d("CategoryActivity", "Toast Triggered: " + selectedSort);  // Debugging log
                Toast.makeText(CategoryActivity.this, "Selected: " + selectedSort, Toast.LENGTH_SHORT).show();

                // Sắp xếp theo tùy chọn của người dùng
                switch (selectedSort) {
                    case "Low to High":
                        // Sắp xếp danh sách theo giá từ thấp đến cao
                        Collections.sort(listFood, new Comparator<Food>() {
                            @Override
                            public int compare(Food f1, Food f2) {
                                return Double.compare(f1.getPrice(), f2.getPrice());
                            }
                        });
                        break;

                    case "High to Low":
                        // Sắp xếp danh sách theo giá từ cao xuống thấp
                        Collections.sort(listFood, new Comparator<Food>() {
                            @Override
                            public int compare(Food f1, Food f2) {
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

        lvFood = findViewById(R.id.lvFood);

        // Khởi tạo danh sách các món ăn
        listFood = new ArrayList<>();

        // Thêm dữ liệu vào danh sách
        listFood.add(new Food("Chicken Hawaiian", 10.35, 101, 1, R.drawable.a2, "Chicken, Cheese and Pineapple", true, new ArrayList<>()));
        listFood.add(new Food("Pepperoni Pizza", 9.99, 102, 1, R.drawable.a3, "Pepperoni, Cheese", true, new ArrayList<>()));

        // Khởi tạo adapter và gán cho ListView
        adapterfood = new foodAdapter(this, R.layout.fast_food_item, listFood);
        lvFood.setAdapter(adapterfood);

//        lvFood.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Food selectedFood = listFood.get(position);
//                Intent intent = new Intent(CategoryActivity.this, FoodDetailActivity.class);
//                intent.putExtra("foodID", selectedFood.getId());
//                startActivity(intent);
//            }
//        });

    }
}
