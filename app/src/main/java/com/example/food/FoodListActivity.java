package com.example.food;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.food.Model.Food;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;


public class FoodListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditText editTextSearch;
    private Button addFoodButton, btnBack;
    private DatabaseReference databaseReference;
    private List<Food> foodList = new ArrayList<>();
    private List<Food> filteredFoodList = new ArrayList<>();
    private FoodAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        recyclerView = findViewById(R.id.recyclerView);
        editTextSearch = findViewById(R.id.editTextSearch);
        addFoodButton = findViewById(R.id.addFoodButton);
        btnBack = findViewById(R.id.btn_back);

        databaseReference = FirebaseDatabase.getInstance().getReference("Food");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new FoodAdapter(filteredFoodList, new FoodAdapter.OnFoodActionListener() {
            @Override
            public void onUpdate(Food food) {
                showUpdateDialog(food);
            }

            @Override
            public void onDelete(Food food) {
                deleteFood(food);
            }
        });
        recyclerView.setAdapter(adapter);

        String restaurantID = getIntent().getStringExtra("restaurantID");
        Toast.makeText(this, "Restaurant id: " + restaurantID, Toast.LENGTH_SHORT).show();

        Log.d("FoodListActivity", "Taken restaurant ID: " + restaurantID);
        loadFoodData(restaurantID);

        // Search functionality
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterFoodList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        addFoodButton.setOnClickListener(v -> {
            Intent intent = new Intent(FoodListActivity.this, AddFoodActivity.class);
            intent.putExtra("restaurantID", restaurantID);
            startActivity(intent);
        });
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadFoodData(String restaurantID) {
        databaseReference.orderByChild("restaurantID").equalTo(restaurantID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                foodList.clear();
                filteredFoodList.clear();

                for (DataSnapshot foodSnapshot : snapshot.getChildren()) {
                    Food food = foodSnapshot.getValue(Food.class);
                    if (food != null) {
                        foodList.add(food);
                    }
                }

                filteredFoodList.addAll(foodList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FoodListActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void filterFoodList(String query) {
        filteredFoodList.clear();

        if (query.isEmpty()) {
            filteredFoodList.addAll(foodList);
        } else {
            for (Food food : foodList) {
                if (food.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredFoodList.add(food);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void showUpdateDialog(Food food) {
        // Implement update dialog
    }

    private void deleteFood(Food food) {
        databaseReference.child(String.valueOf(food.getId())).removeValue()
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(FoodListActivity.this, "Food deleted", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(FoodListActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void showCreateDialog() {
        // Implement create dialog
    }
}
