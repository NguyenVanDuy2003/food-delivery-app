package com.example.food;

import static java.util.Locale.filter;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
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
    private Button btn_quaylai,btn_sapxeptang , btn_sapxepgiam;
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

        // Thiết lập giao diện Edge-to-Edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Lấy ID nhà hàng từ Intent
        restaurantID = getIntent().getStringExtra("restaurantID");

        // Ánh Xạ
        btn_quaylai = findViewById(R.id.btn_quaylai);
        btn_sapxeptang = findViewById(R.id.btn_sapxeptang);
        btn_sapxepgiam = findViewById(R.id.btn_sapxepgiam);
        editTextSearch = findViewById(R.id.editTextSearch);
        recyclerView = findViewById(R.id.rvolder);

        // Thiết lập RecyclerView
        orderList = new ArrayList<>();
        adapter = new OrderAdapter(this, orderList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Thêm dữ liệu mẫu vào danh sách đơn hàng
        populateOrderList();

        btn_quaylai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }

        });
        // Sắp xếp tăng
        btn_sapxeptang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderList.sort((o1, o2) -> o1.getOrderDate().compareTo(o2.getOrderDate()));
                adapter.notifyDataSetChanged();
            }
        });
        // Sắp xếp giảm dần
        btn_sapxepgiam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderList.sort((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()));
                adapter.notifyDataSetChanged();
            }
        });

        // Chức năng tìm kiếm đơn hàng theo tên món ăn
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
        ArrayList<Order> filteredList = new ArrayList<>();
        for (Order order : orderList) {
            if (order.getDishName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(order);
            }
        }
        adapter.updateList(filteredList);
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
                orderList.clear(); // Xóa danh sách đơn hàng cũ
                for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                    Order order = orderSnapshot.getValue(Order.class);
                    if (order != null) {
                        try {
                            // Lấy ngày đặt hàng dưới dạng timestamp (milisecond)
                            long timestamp = Long.parseLong(order.getOrderDate());

                            // Chuyển timestamp thành đối tượng Date
                            Date date = new Date(timestamp);

                            // Định dạng ngày theo chuỗi
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String formattedDate = sdf.format(date);

                            // Gán ngày đã định dạng cho đơn hàng
                            order.setOrderDate(formattedDate);
                        } catch (Exception e) {
                            Log.d("OrderManagerActivity", "Date format error: " + e.getMessage());
                        }

                        // Thêm đơn hàng vào danh sách
                        orderList.add(order);
                    }
                }
                adapter.notifyDataSetChanged();// Cập nhật lại RecyclerView
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("OrderManagerActivity", "Failed to read orders: " + databaseError.getMessage());
            }
        });
    }

}

