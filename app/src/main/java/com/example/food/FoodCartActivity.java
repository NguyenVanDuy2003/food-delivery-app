package com.example.food;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.food.Common.CommonKey;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class FoodCartActivity extends AppCompatActivity implements FoodCartAdapter.QuantityChangeListener {

    private RecyclerView foodCartItems;
    private Button btn_checkout, return_btn, btn_thongbao;
    private TextView subtotalValue, taxValue, deliveryValue, totalValue, totalItem, empty_cart_text;
    private LinearLayout fees;

    private List<FoodCartModel> foodCartList;
    private DatabaseReference cartReference;
    private FoodCartAdapter foodCartAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        initializeUIComponents();
        setupButtonListeners();
        setupButtonThongbaoListeners();
        cartReference = FirebaseDatabase.getInstance().getReference("Cart");
        foodCartList = new ArrayList<>();

        fetchCartItems();
    }

    @Override
    public void onTotalPriceChanged(double totalPrice) {
        fetchCartItems();
    }

    private void initializeUIComponents() {
        foodCartItems = findViewById(R.id.foodCartItems);
        btn_checkout = findViewById(R.id.btn_checkout);
        return_btn = findViewById(R.id.return_btn);
        subtotalValue = findViewById(R.id.subtotalValue);
        taxValue = findViewById(R.id.taxValue);
        deliveryValue = findViewById(R.id.deliveryValue);
        totalValue = findViewById(R.id.totalValue);
        totalItem = findViewById(R.id.totalItem);
        fees = findViewById(R.id.fees);
        empty_cart_text = findViewById(R.id.empty_cart_text);

        btn_thongbao = findViewById(R.id.btn_thongbao);
    }


    private void setupButtonListeners() {
        btn_checkout.setOnClickListener(v -> {
            Intent intent = new Intent(this, CheckoutActivity.class);
            intent.putExtra("totalValue", totalValue.getText().toString());
            startActivity(intent);
        });
        return_btn.setOnClickListener(v -> finish());
    }
    private void setupButtonThongbaoListeners() {
        btn_thongbao.setOnClickListener(v -> {
            Intent intent = new Intent(this, publicizeMainActivity.class);
            startActivity(intent);
        });
        return_btn.setOnClickListener(v -> finish());
    }


    private void fetchCartItems() {
        cartReference.child(getUserId()).get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                foodCartList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    FoodCartModel model = snapshot.getValue(FoodCartModel.class);
                    if (model != null) {
                        foodCartList.add(model);
                    }
                }
                displayCartItems(foodCartList);
                updatePriceDetails(foodCartList);
            } else {
                displayCartItems(foodCartList);
                updatePriceDetails(foodCartList);
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Không thể fetch cart data", Toast.LENGTH_SHORT).show();
        });
    }

    private void displayCartItems(List<FoodCartModel> foodCartList) {
        foodCartAdapter = new FoodCartAdapter(this, foodCartList, this);
        foodCartItems.setAdapter(foodCartAdapter);
        foodCartItems.setLayoutManager(new GridLayoutManager(this, 1));

        updatePriceDetails(foodCartList);

        if (foodCartList.isEmpty()) {
            btn_checkout.setEnabled(false);
            btn_checkout.setVisibility(View.GONE);
            fees.setVisibility(View.GONE);
            empty_cart_text.setVisibility(View.VISIBLE);
        } else {
            btn_checkout.setEnabled(true);
            btn_checkout.setVisibility(View.VISIBLE);
            fees.setVisibility(View.VISIBLE);
            empty_cart_text.setVisibility(View.GONE);
        }
    }

    private void updatePriceDetails(List<FoodCartModel> foodCartList) {
        double subtotal = 0.0;
        int totalQuantity = 0;
        for (FoodCartModel model : foodCartList) {
            subtotal += model.getPrice() * model.getQuantity();
            totalQuantity += model.getQuantity();
        }

        Double deliveryFee = 1.0;
        subtotalValue.setText(String.format("$%.2f", subtotal));
        taxValue.setText(String.format("$%.2f", subtotal * 0.1));
        deliveryValue.setText(deliveryFee.toString());
        totalValue.setText(String.format("$%.2f", subtotal + subtotal * 0.1 + deliveryFee));
        totalItem.setText(String.format("%d items", totalQuantity));
    }

    @Override
    public void onQuantityChanged() {
        fetchCartItems();
    }

    private String getUserId() {
        SharedPreferences sharedPreferences = getSharedPreferences(CommonKey.MY_APP_PREFS, MODE_PRIVATE);
        return sharedPreferences.getString(CommonKey.USER_ID, null);
    }
}