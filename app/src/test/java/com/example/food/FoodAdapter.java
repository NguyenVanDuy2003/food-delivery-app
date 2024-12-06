package com.example.food;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.food.Model.Food;
import com.example.food.R;

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

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            updateButton = itemView.findViewById(R.id.updateButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        public void bind(Food food) {
            nameTextView.setText(food.getName());
            priceTextView.setText(String.format("$%.2f", food.getPrice()));

            updateButton.setOnClickListener(v -> listener.onUpdate(food));
            deleteButton.setOnClickListener(v -> listener.onDelete(food));
        }
    }
}
