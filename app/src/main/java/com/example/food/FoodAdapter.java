package com.example.food;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.food.Model.Food;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private List<Food> foodList;
    private OnFoodActionListener listener;

    public interface OnFoodActionListener {
        void onUpdate(Food food);
        void onDelete(Food food);
    }

    public FoodAdapter(List<Food> foodList, OnFoodActionListener listener) {
        this.foodList = foodList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.food_item, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Food food = foodList.get(position);
        holder.bind(food);
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    class FoodViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTextView, priceTextView;
        private Button updateButton, deleteButton;
        private ImageView foodImageView; // ImageView for the food image

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            updateButton = itemView.findViewById(R.id.updateButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            foodImageView = itemView.findViewById(R.id.foodImageView); // Initialize ImageView
        }

        public void bind(Food food) {
            nameTextView.setText(food.getName());
            priceTextView.setText(String.format("$%.2f", food.getPrice()));

            // Use Glide to load the image from the URL
            Glide.with(itemView.getContext())
                    .load(food.getImageUrl())  // Use the image URL from the food object
                    .into(foodImageView);  // Set the image into the ImageView

            updateButton.setOnClickListener(v -> {
                listener.onUpdate(food);
                Intent intent = new Intent(v.getContext(), FoodUpdateActivity.class);
                intent.putExtra("food", food); // Pass the food object to the update activity
                v.getContext().startActivity(intent);
            });

            deleteButton.setOnClickListener(v -> listener.onDelete(food));
        }
    }
}
