package com.example.food;

import android.os.Bundle;
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

import com.example.food.Model.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderManagerActivity extends AppCompatActivity {
    private Button btn_quaylai;
    private EditText editTextSearch;
    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private List<Order> orderList;
    private String restaurantID;
    private DatabaseReference orderRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_manager);

        // Setup edge-to-edge layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get restaurant ID from intent
        restaurantID = getIntent().getStringExtra("restaurantID");

        // Initialize views
        btn_quaylai = findViewById(R.id.btn_quaylai);
        editTextSearch = findViewById(R.id.editTextSearch);
        recyclerView = findViewById(R.id.rvolder);

        // Setup RecyclerView
        orderList = new ArrayList<>();
        adapter = new OrderAdapter(this, orderList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Populate order list with sample data
        populateOrderList();

        // Handle back button click
        btn_quaylai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void populateOrderList() {
        if (restaurantID == null) {
            Log.d("OrderManagerActivity", "restaurantID is null");
            return;
        }

        orderRef = FirebaseDatabase.getInstance().getReference("Order").child(restaurantID);
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                orderList.clear(); // Clear existing orders to avoid duplicates
                for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                    Order order = orderSnapshot.getValue(Order.class);
                    if (order != null) {
                        try {
                            // Get the order date as a timestamp (milliseconds)
                            long timestamp = Long.parseLong(order.getOrderDate());

                            // Convert the timestamp to a Date object
                            Date date = new Date(timestamp);

                            // Format the date as a string
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String formattedDate = sdf.format(date);

                            // Set the formatted date to the order
                            order.setOrderDate(formattedDate);
                        } catch (Exception e) {
                            Log.d("OrderManagerActivity", "Date format error: " + e.getMessage());
                        }

                        // Add the order to the list
                        orderList.add(order);
                    }
                }
                adapter.notifyDataSetChanged(); // Notify adapter to refresh the RecyclerView
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("OrderManagerActivity", "Failed to read orders: " + databaseError.getMessage());
            }
        });
    }

}

