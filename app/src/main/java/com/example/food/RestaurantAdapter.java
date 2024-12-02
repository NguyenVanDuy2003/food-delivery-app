package com.example.food;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {
    private List<Restaurant> restaurants;

    public RestaurantAdapter(List<Restaurant> restaurants) {
        this.restaurants = restaurants;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_restaurant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Restaurant restaurant = restaurants.get(position);
        holder.imageView.setImageResource(restaurant.getImageResource());
        holder.nameTextView.setText(restaurant.getName());
        holder.ratingTextView.setText(String.format("%.1f ★", restaurant.getRating()));
        holder.deliveryTimeTextView.setText(restaurant.getDeliveryTime());

        // Add click listener to the item
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), CategoryActivity.class);
            intent.putExtra("restaurant_name", restaurant.getName());
            intent.putExtra("restaurant_image", restaurant.getImageResource());
            intent.putExtra("restaurant_rating", restaurant.getRating());
            intent.putExtra("restaurant_delivery_time", restaurant.getDeliveryTime());
            view.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameTextView;
        TextView ratingTextView;
        TextView deliveryTimeTextView;

        ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.iv_restaurant);
            nameTextView = view.findViewById(R.id.tv_restaurant_name);
            ratingTextView = view.findViewById(R.id.tv_rating);
            deliveryTimeTextView = view.findViewById(R.id.tv_delivery_time);
        }
    }
} 