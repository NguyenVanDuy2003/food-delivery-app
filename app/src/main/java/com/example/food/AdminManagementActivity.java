package com.example.food;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminManagementActivity extends AppCompatActivity {
    private Button btnThem, btnQuayLai;
    RecyclerView rvNhaHang;
    ArrayList<Restaurant> listRestaurant;
    RestaurantAdapter adapterRestaurant;
    private DatabaseReference databaseReference;
    private EditText editTextSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_managent_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnThem = findViewById(R.id.btn_them);
        btnQuayLai = findViewById(R.id.btn_quaylai);
        rvNhaHang = findViewById(R.id.rvNhaHang);
        editTextSearch = findViewById(R.id.editTextSearch);

        listRestaurant = new ArrayList<>();
        adapterRestaurant = new RestaurantAdapter(this, listRestaurant);

        // Set up RecyclerView
        rvNhaHang.setLayoutManager(new LinearLayoutManager(this));
        rvNhaHang.setAdapter(adapterRestaurant);

        // Set RecyclerView Item Click Listener
        adapterRestaurant.setOnItemClickListener(new RestaurantAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Restaurant restaurant) {
                // Handle the click event here
                Intent intent = new Intent(AdminManagementActivity.this, FoodListActivity.class);
                intent.putExtra("restaurantID", restaurant.getId());
                Log.d("AdminManagementActivity", "Starting FoodListActivity with restaurantID:" + restaurant.getId());
                startActivity(intent);
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("restaurants");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listRestaurant.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Restaurant restaurant = snapshot.getValue(Restaurant.class);
                    listRestaurant.add(restaurant);
                }
                adapterRestaurant.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi
            }
        });

        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminManagementActivity.this, AddRestauntsActivity.class);
                startActivity(intent);
            }
        });

        btnQuayLai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filter(String text) {
        ArrayList<Restaurant> filteredList = new ArrayList<>();
        for (Restaurant restaurant : listRestaurant) {
            if (restaurant.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(restaurant);
            }
        }
        adapterRestaurant.updateList(filteredList);
    }
}
