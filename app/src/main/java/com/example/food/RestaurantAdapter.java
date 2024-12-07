package com.example.food;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.food.Common.CommonKey;
import com.example.food.Enum.Role;

import java.util.ArrayList;
import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {
    private List<Restaurant> restaurants;
    private Context context;
    private AdapterView.OnItemClickListener onItemClickListener;
    public RestaurantAdapter(Context context ,List<Restaurant> restaurants) {
        this.restaurants = restaurants;
        this.context = context;
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
        Glide.with(context)
                .load(restaurant.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.imageView);

        holder.nameTextView.setText(restaurant.getName());
        holder.ratingTextView.setText(String.format("%.1f ★", restaurant.getRating()));
        holder.deliveryTimeTextView.setText(restaurant.getDeliveryTime());

        holder.itemView.setOnClickListener(view -> {
            String restaurantId = restaurant.getId();
            if (restaurantId != null && !restaurantId.isEmpty()) {
                SharedPreferences sharedPreferences = context.getSharedPreferences(CommonKey.MY_APP_PREFS, MODE_PRIVATE);
                String role = sharedPreferences.getString(CommonKey.ROLE, Role.USER.name());

                Intent intent;
                if (Role.USER.name().equals(role)) {
                    intent = new Intent(view.getContext(), CategoryActivity.class);
                } else {
                    intent = new Intent(view.getContext(), RestaurantdetailsActivity.class);
                }
                intent.putExtra("restaurantID", restaurantId);
                view.getContext().startActivity(intent);
            } else {
                Toast.makeText(context, "Restaurant ID is missing!", Toast.LENGTH_SHORT).show();
            }
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

    public void updateList(ArrayList<Restaurant> newList) {
        restaurants = newList;
        notifyDataSetChanged();
    }
}