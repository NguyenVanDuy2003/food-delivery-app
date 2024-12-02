package com.example.food;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.food.model.User;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private final ArrayList<User> users;
    private final UserActionListener actionListener;

    public interface UserActionListener {
        void onUserAction(User user);
    }

    public UserAdapter(ArrayList<User> users, UserActionListener actionListener) {
        this.users = users;
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.tvEmail.setText(user.getEmail());
        holder.tvPassword.setText(user.getPassword());

        holder.itemView.setOnClickListener(v -> actionListener.onUserAction(user));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmail, tvPassword;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmail = itemView.findViewById(R.id.tv_email);
            tvPassword = itemView.findViewById(R.id.tv_password);
        }
    }
}
