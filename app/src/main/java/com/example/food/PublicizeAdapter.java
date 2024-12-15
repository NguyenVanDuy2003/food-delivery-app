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
import com.example.food.Model.Order;

import java.util.List;

public class PublicizeAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder>{

    private Context context;
    private List<Order> orderList;

    public PublicizeAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderAdapter.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order, parent, false);
        return new OrderAdapter.OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.tvDishName.setText(order.getDishName());
        holder.tvQuantity.setText(String.valueOf(order.getQuantity()));
        holder.tvPricePerDish.setText(String.format("%.2f", order.getPricePerDish()));
        holder.tvOrderDate.setText(order.getOrderDate());
        holder.tvOrdererName.setText(order.getOrdererName());
        holder.tvPaymentMethod.setText(order.getPaymentMethod());

        // Tải hình ảnh món ăn từ URL sử dụng Glide
        Glide.with(context)
                .load(order.getDishImg()) // URL của hình ảnh món ăn
                .into(holder.ivDishImg);  // ImageView để hiển thị hình ảnh

        // Thiết lập sự kiện click cho mỗi mục
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, publicizeMainActivity.class); // Activity bạn sẽ tạo
            intent.putExtra("dishName", order.getDishName());
            intent.putExtra("quantity", order.getQuantity());
            intent.putExtra("pricePerDish", order.getPricePerDish());
            intent.putExtra("orderDate", order.getOrderDate());
            intent.putExtra("ordererName", order.getOrdererName());
            intent.putExtra("paymentMethod", order.getPaymentMethod());
            intent.putExtra("dishImg", order.getDishImg()); // URL hình ảnh
            context.startActivity(intent);
        });
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

        // Khai báo các TextView và ImageView để hiển thị thông tin món ăn
        TextView tvDishName, tvQuantity, tvPricePerDish, tvOrderDate, tvOrdererName, tvPaymentMethod;
        ImageView ivDishImg;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDishName = itemView.findViewById(R.id.tvDishName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvPricePerDish = itemView.findViewById(R.id.tvPricePerDish);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrdererName = itemView.findViewById(R.id.tvOrdererName);
            tvPaymentMethod = itemView.findViewById(R.id.tvPaymentMethod);
            ivDishImg = itemView.findViewById(R.id.ivDishImg);
        }
    }
}
