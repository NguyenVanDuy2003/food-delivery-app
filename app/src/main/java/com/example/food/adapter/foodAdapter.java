package com.example.food.adapter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.food.model.Food;
import com.example.food.R;
import com.example.food.FoodDetailActivity;

import java.util.ArrayList;


public class foodAdapter extends ArrayAdapter {
    Activity context;
    int resource;
    ArrayList<Food> listFood;
    public foodAdapter(Activity context, int resource , ArrayList<Food> listF){
        super(context, resource);
        this.context = context;
        this.resource = resource;
        this.listFood = listF;
    }

    @Override
    public int getCount() {
        return listFood.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = this.context.getLayoutInflater();
        View customView = inflater.inflate(this.resource, null);

        customView.setClickable(true);
        customView.setFocusable(true);

        ImageView image = customView.findViewById(R.id.image);
        TextView tvNameFood = customView.findViewById(R.id.tvNameFood);
        TextView tvPriceFood = customView.findViewById(R.id.tvPrice);
        TextView tvDescription = customView.findViewById(R.id.tvDescription);

        Food food = this.listFood.get(position);

        image.setImageResource(food.getImage());
        tvNameFood.setText(food.getNameFood());
        tvPriceFood.setText(food.getPrice() + "");
        tvDescription.setText(food.getDescription());

        customView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("foodAdapter", "Item clicked: " + food.getNameFood());
                Toast.makeText(context, "Item clicked: " + food.getNameFood(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, FoodDetailActivity.class);
                intent.putExtra("foodID", food.getFoodID());
                context.startActivity(intent);
            }
        });

        return customView;

    }
}