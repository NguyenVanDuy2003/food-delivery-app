package com.example.food;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FoodCartActivity extends AppCompatActivity {
    private RecyclerView foodCartItems;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        List<FoodCartModel> foodCartList = new ArrayList<FoodCartModel>();
        foodCartList.add(new FoodCartModel("Red n hot pizza", "Spicy chicken, beef", 15.30, 2, R.drawable.cart_img_placeholder_1));
        foodCartList.add(new FoodCartModel("Greek salad", "with baked salmon", 12.00, 2, R.drawable.cart_img_placeholder_2));

        foodCartItems = findViewById(R.id.foodCartItems);

        FoodCartAdapter foodCartAdapter = new FoodCartAdapter(foodCartList);
        foodCartItems.setAdapter(foodCartAdapter);
        foodCartItems.setLayoutManager(new GridLayoutManager(this, 1));


        Button returnButton = findViewById(R.id.return_btn);
        returnButton.setOnClickListener(v -> finish());
    }
}
