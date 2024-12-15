package com.example.food;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.food.Common.CommonKey;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FoodCartAdapter extends RecyclerView.Adapter<FoodCartAdapter.FoodCartViewHolder> {

    private List<FoodCartModel> foodList;
    private DatabaseReference databaseReference;
    private Context context;

    private final QuantityChangeListener quantityChangeListener;

    public FoodCartAdapter(Context context, List<FoodCartModel> foodList, QuantityChangeListener listener) {
        this.context = context;
        this.foodList = foodList;
        this.databaseReference = FirebaseDatabase.getInstance().getReference("Cart");
        this.quantityChangeListener = listener;
    }

    @Override
    public FoodCartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_cart_card, parent, false);
        return new FoodCartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodCartViewHolder holder, int position) {
        FoodCartModel food = foodList.get(position);

        Picasso.get()
                .load(food.getImageUrl())
                .into(holder.itemImg);

        holder.itemName.setText(food.getName());
        holder.itemIngredient.setText(food.getIngredient());
        holder.itemPrice.setText(String.format("$%.2f", food.getPrice()));
        holder.itemQuantity.setText(String.valueOf(food.getQuantity()));

        holder.increaseButton.setOnClickListener(v -> increaseQuantity(food, holder, position));
        holder.decreaseButton.setOnClickListener(v -> decreaseQuantity(food, position, holder));
        holder.removeButton.setOnClickListener(v -> removeItemFromCart(food, position));
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    private void decreaseQuantity(FoodCartModel food, int position, FoodCartViewHolder holder) {
        if (food.getQuantity() > 1) {
            food.setQuantity(food.getQuantity() - 1);
            databaseReference.child(getUserId()).child(food.getId()).child("quantity")
                    .setValue(food.getQuantity())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            holder.itemQuantity.setText(String.valueOf(food.getQuantity()));
                            notifyTotalPrice();
                        }
                    });
        } else {
            removeItemFromCart(food, position);
        }
    }

    private void increaseQuantity(FoodCartModel food, FoodCartViewHolder holder, int position) {
        food.setQuantity(food.getQuantity() + 1);
        databaseReference.child(getUserId()).child(food.getId()).child("quantity")
                .setValue(food.getQuantity())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        holder.itemQuantity.setText(String.valueOf(food.getQuantity()));
                        notifyTotalPrice();
                    }
                });
    }

    private void removeItemFromCart(FoodCartModel food, int position) {
        databaseReference.child(getUserId()).child(food.getId()).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                foodList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, foodList.size());
                notifyTotalPrice();
                Log.d("Firebase", "Mục đã được gỡ bỏ khỏi Firebase");
            } else {
                Log.e("Firebase", "Không thể xóa mục khỏi trong Firebase", task.getException());
            }
        });
    }

    private void notifyTotalPrice() {
        double totalPrice = 0.0;
        for (FoodCartModel item : foodList) {
            totalPrice += item.getPrice() * item.getQuantity();
        }
        quantityChangeListener.onTotalPriceChanged(totalPrice);
    }

    private String getUserId() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CommonKey.MY_APP_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(CommonKey.USER_ID, null);
    }

    static class FoodCartViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private ImageView itemImg;
        private TextView itemName;
        private TextView itemIngredient;
        private TextView itemPrice;
        private TextView itemQuantity;
        private Button decreaseButton;
        private Button increaseButton;
        private Button removeButton;

        public FoodCartViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.foodComponent);
            itemImg = itemView.findViewById(R.id.itemImg);
            itemName = itemView.findViewById(R.id.itemName);
            itemIngredient = itemView.findViewById(R.id.itemIngredient);
            itemPrice = itemView.findViewById(R.id.pricePerItem);
            itemQuantity = itemView.findViewById(R.id.itemQuantity);
            decreaseButton = itemView.findViewById(R.id.decreaseCartItemQuantity);
            increaseButton = itemView.findViewById(R.id.increaseCartItemQuantity);
            removeButton = itemView.findViewById(R.id.removeItemBtn);
        }
    }

    public interface QuantityChangeListener {
        void onQuantityChanged();
        void onTotalPriceChanged(double totalPrice);
    }
}
