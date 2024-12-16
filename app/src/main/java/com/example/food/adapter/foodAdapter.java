package com.example.food.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.food.Model.Food;
import com.example.food.R;
import com.example.food.FoodDetailActivity;

import java.util.ArrayList;

public class foodAdapter extends ArrayAdapter<Food> {

    Activity context;
    int resource;
    ArrayList<Food> listFood;

    public foodAdapter(Activity context, int resource, ArrayList<Food> listFood) {
        super(context, resource, listFood);
        this.context = context;
        this.resource = resource;
        this.listFood = listFood;
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

        // Load the image from the URL using Glide
        Glide.with(context)
                .load(food.getImageUrl()) // URL of the image
                .into(image); // Set it into the ImageView

        tvNameFood.setText(food.getName());
        tvPriceFood.setText("$" + String.valueOf(food.getPrice()));
        tvDescription.setText(food.getIngredients());

        customView.setOnClickListener(v -> {
            Intent intent = new Intent(context, FoodDetailActivity.class);
            intent.putExtra("foodID", food.getId());
            context.startActivity(intent);
        });

        return customView;
    }
}
