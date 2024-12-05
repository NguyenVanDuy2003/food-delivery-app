package com.example.food;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.food.Model.User;

import java.util.List;

public class UserAdapter extends ArrayAdapter<User> {
    public UserAdapter(Context context, List<User> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        User user = getItem(position); // Lấy đối tượng User tại vị trí này

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        }

        TextView text1 = convertView.findViewById(android.R.id.text1);
        TextView text2 = convertView.findViewById(android.R.id.text2);

        // Hiển thị tên đầy đủ và email
        text1.setText(user.getFullName());
        text2.setText(user.getEmail());

        return convertView;
    }
}
