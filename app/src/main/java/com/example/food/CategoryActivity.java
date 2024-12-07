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

        // Step 1: Create sorting options
        String[] sortOptions = {"Popular", "Low to High", "High to Low"};

        // Step 2: Find Spinner in the layout
        Spinner spinnerSort = findViewById(R.id.spinnerSort);

        // Step 3: Create ArrayAdapter for Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, sortOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Step 4: Set the Adapter for Spinner
        spinnerSort.setAdapter(adapter);

        // Step 5: Handle item selection from the Spinner
        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedSort = parentView.getItemAtPosition(position).toString();
                Toast.makeText(CategoryActivity.this, "Selected: " + selectedSort, Toast.LENGTH_SHORT).show();

                // Sorting based on user selection
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

                // Update the adapter after sorting
                if (adapterFood != null) {
                    adapterFood.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // No action needed
            }
        });

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Food");

        String restaurantID = getIntent().getStringExtra("restaurantID");
        lvFood = findViewById(R.id.lvFood);

        // Initialize food list
        listFood = new ArrayList<>();

        // Load food data from Firebase Realtime Database
        loadFoodData(restaurantID);
    }

    private void loadFoodData(String restaurantID) {
        // Fetching food data from Firebase Realtime Database
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

                // Set up the adapter after fetching data
                adapterFood = new foodAdapter(CategoryActivity.this, R.layout.fast_food_item, listFood);
                lvFood.setAdapter(adapterFood);

                // Notify adapter that data has changed
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
