package com.example.food;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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


import com.example.food.Common.CommonKey;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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

        // Thiết lập RecyclerView
        rvNhaHang.setLayoutManager(new LinearLayoutManager(this));
        rvNhaHang.setAdapter(adapterRestaurant);

        // Lấy userId từ firebase
        SharedPreferences sharedPreferences = getSharedPreferences(CommonKey.MY_APP_PREFS, MODE_PRIVATE);
        String userId = sharedPreferences.getString(CommonKey.USER_ID, null);

        // Lấy danh sách nhà hàng theo userId
        if(userId != null){
            databaseReference = FirebaseDatabase.getInstance().getReference("restaurants");

            Query query = databaseReference.orderByChild("userId").equalTo(userId);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    listRestaurant.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Restaurant restaurant = snapshot.getValue(Restaurant.class);
                        if (restaurant != null) {
                            listRestaurant.add(restaurant);
                        }
                    }
                    adapterRestaurant.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Xử lý l��i
                }
            });
        }

        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminManagementActivity.this, AddRestaurantsActivity.class);
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
