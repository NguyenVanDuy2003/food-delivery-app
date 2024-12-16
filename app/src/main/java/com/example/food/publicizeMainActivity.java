package com.example.food;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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

public class publicizeMainActivity extends AppCompatActivity {
    private Button btn_quaylai;
    private RecyclerView recyclerView;
    private PublicizeAdapter adapter;
    private List<Order> PublicizeList;
    private String restaurantID;
    private DatabaseReference orderRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_publicize_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Thiết lập sự kiện cho nút quay lại
        restaurantID = getIntent().getStringExtra("restaurantID");
        btn_quaylai = findViewById(R.id.btn_quaylai);
        recyclerView = findViewById(R.id.rvolder);
        PublicizeList = new ArrayList<>();
        adapter = new PublicizeAdapter(this, PublicizeList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        btn_quaylai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }

        });
        // Lấy danh sách đơn hàng từ Firebase
        populateOrderList();


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
                PublicizeList.clear(); // Xóa danh sách đơn hàng cũ để tránh trùng lặp
                for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                    Order order = orderSnapshot.getValue(Order.class);
                    if (order != null) {
                        try {
                            // Lấy ngày đặt hàng dưới dạng timestamp (miliseconds)
                            long timestamp = Long.parseLong(order.getOrderDate());

                            // Chuyển đổi timestamp thành đối tượng Date
                            Date date = new Date(timestamp);

                            // Định dạng ngày tháng thành chuỗi
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String formattedDate = sdf.format(date);

                            // Gán ngày đã định dạng vào đơn hàng
                            order.setOrderDate(formattedDate);
                        } catch (Exception e) {
                            Log.d("OrderManagerActivity", "Date format error: " + e.getMessage());
                        }

                        // Thêm đơn hàng vào danh sách
                        PublicizeList.add(order);
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