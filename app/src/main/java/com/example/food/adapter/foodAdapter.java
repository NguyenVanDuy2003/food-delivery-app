package com.example.food.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.food.R;

import java.util.ArrayList;

public class foodAdapter extends ArrayAdapter {
    Activity context;
    int resource;
    ArrayList<com.example.food.Model.food> listFood;
    public foodAdapter(Activity context, int resource , ArrayList<com.example.food.Model.food> listF){
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

        ImageView image = customView.findViewById(R.id.image);
        TextView tvNameFood = customView.findViewById(R.id.tvNameFood);
        TextView tvPriceFood = customView.findViewById(R.id.tvPrice);
        TextView tvDescription = customView.findViewById(R.id.tvDescription);



        com.example.food.Model.food food = this.listFood.get(position);

        image.setImageResource(food.getImage());
        tvNameFood.setText(food.getNameFood());
        tvPriceFood.setText(food.getPrice() + "");
        tvDescription.setText(food.getDescription());
        return customView;

    }
}