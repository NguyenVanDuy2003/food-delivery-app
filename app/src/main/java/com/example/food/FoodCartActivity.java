package com.example.food;

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
    private Button btn_checkout, return_btn;
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
    }

    private void setupButtonListeners() {
        btn_checkout.setOnClickListener(v -> {
            Toast.makeText(this, "Checkout clicked", Toast.LENGTH_SHORT).show();
        });
        return_btn.setOnClickListener(v -> finish());
    }

    private void fetchCartItems() {
        Log.d("Cart Path", "Fetching data from path: Cart/" + getUserId());
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
                Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
                displayCartItems(foodCartList);
                updatePriceDetails(foodCartList);
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to fetch cart data", Toast.LENGTH_SHORT).show();
        });
    }

    private void displayCartItems(List<FoodCartModel> foodCartList) {
        foodCartAdapter = new FoodCartAdapter(this, foodCartList, this);
        foodCartItems.setAdapter(foodCartAdapter);
        foodCartItems.setLayoutManager(new GridLayoutManager(this, 1));

        updatePriceDetails(foodCartList);
        Log.d("FoodCartActivity", "Cart Items: " + foodCartList.toString());

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

        subtotalValue.setText(String.format("$%.2f", subtotal));
        taxValue.setText(String.format("$%.2f", subtotal * 0.1)); // Assuming 10% tax
        deliveryValue.setText("1.0"); // Flat delivery fee
        totalValue.setText(String.format("$%.2f", subtotal + subtotal * 0.1 + 5.0));
        totalItem.setText(String.format("%d items", totalQuantity));
    }

    @Override
    public void onQuantityChanged() {
        Toast.makeText(this, "Quantity Changed, refreshing totals", Toast.LENGTH_SHORT).show();
        fetchCartItems();
    }

    private String getUserId() {
        SharedPreferences sharedPreferences = getSharedPreferences(CommonKey.MY_APP_PREFS, MODE_PRIVATE);
        return sharedPreferences.getString(CommonKey.USER_ID, null);
    }
}
