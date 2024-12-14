package com.example.food;

import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

public class OrderManagerActivity extends AppCompatActivity {
    private Button btn_quaylai;
    private EditText editTextSearch;
    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private List<Order> orderList;

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
        orderList.add(new Order("001", "Pizza", 2, 200.0, "2024-12-14", "John Doe", "Credit Card"));
        orderList.add(new Order("002", "Burger", 1, 100.0, "2024-12-13", "Jane Smith", "Cash"));
        orderList.add(new Order("003", "Pasta", 3, 300.0, "2024-12-12", "Mike Ross", "Debit Card"));
    }
}
