package com.example.food;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.food.Model.Order;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orderList;

    public OrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.tvDishName.setText(order.getDishName());
        holder.tvQuantity.setText(String.valueOf(order.getQuantity()));
        holder.tvPricePerDish.setText(String.format("%.2f", order.getPricePerDish()));
        holder.tvOrderDate.setText(order.getOrderDate());
        holder.tvOrdererName.setText(order.getOrdererName());
        holder.tvPaymentMethod.setText(order.getPaymentMethod());

        // Load the dish image using Glide
        Glide.with(context)
                .load(order.getDishImg()) // Dish image URL
                .into(holder.ivDishImg);  // ImageView where the image should be loaded
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }


    // Hàm cập nhật lại dữ liệu trong adapter và làm mới RecyclerView
    public void updateList(List<Order> newList) {
        orderList = newList; // Thay thế danh sách hiện tại bằng danh sách mới
        notifyDataSetChanged(); // Thông báo cho adapter về sự thay đổi dữ liệu
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView tvDishName, tvQuantity, tvPricePerDish, tvOrderDate, tvOrdererName, tvPaymentMethod;
        ImageView ivDishImg;  // ImageView để hiển thị hình ảnh món ăn

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDishName = itemView.findViewById(R.id.tvDishName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvPricePerDish = itemView.findViewById(R.id.tvPricePerDish);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrdererName = itemView.findViewById(R.id.tvOrdererName);
            tvPaymentMethod = itemView.findViewById(R.id.tvPaymentMethod);
            ivDishImg = itemView.findViewById(R.id.ivDishImg);  // Initialize ImageView
        }
    }
}