package com.example.food;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        holder.tvOrderId.setText(order.getOrderId());
        holder.tvDishName.setText(order.getDishName());
        holder.tvQuantity.setText(String.valueOf(order.getQuantity()));
        holder.tvTotalPrice.setText(String.format("%.2f", order.getPricePerDish()));
        holder.tvOrderDate.setText(order.getOrderDate());
        holder.tvOrdererName.setText(order.getOrdererName());
        holder.tvPaymentMethod.setText(order.getPaymentMethod());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView tvOrderId, tvDishName, tvQuantity, tvTotalPrice, tvOrderStatus, tvOrderDate, tvOrdererName, tvPaymentMethod;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDishName = itemView.findViewById(R.id.tvDishName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvTotalPrice = itemView.findViewById(R.id.tvPricePerDish);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrdererName = itemView.findViewById(R.id.tvOrdererName);
            tvPaymentMethod = itemView.findViewById(R.id.tvPaymentMethod);
        }
    }
}
