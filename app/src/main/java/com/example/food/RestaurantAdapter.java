package com.example.food;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.example.food.FoodListActivity;

import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {
    private Context context;
    private List<Restaurant> restaurants;

    public RestaurantAdapter(Context context, List<Restaurant> restaurants) {
        this.context = context;
        this.restaurants = restaurants;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_restaurant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Restaurant restaurant = restaurants.get(position);
        holder.nameTextView.setText(restaurant.getName());
        holder.ratingTextView.setText(String.valueOf(restaurant.getRating()));
        
        // Load imageUrl using Glide
        Glide.with(context)
            .load(restaurant.getImageUrl())
            .into(holder.imageView);

        // Set click listener for the item
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, FoodListActivity.class);
            intent.putExtra("restaurant_id", restaurant.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    public void updateList(List<Restaurant> newList) {
        restaurants = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView ratingTextView;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.tv_restaurant_name);
            ratingTextView = itemView.findViewById(R.id.tv_restaurant_rating);
            imageView = itemView.findViewById(R.id.iv_restaurant_image);
        }
    }
}