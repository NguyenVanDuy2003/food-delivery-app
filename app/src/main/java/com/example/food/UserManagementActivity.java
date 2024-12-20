package com.example.food;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.food.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserManagementActivity extends AppCompatActivity {
    private EditText searchUser;
    private Button addUserButton;
    private Button backButton;
    private ListView userList;
    private DatabaseReference databaseReference;
    private ArrayList<User> userListData;
    private UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        searchUser = findViewById(R.id.search_user);
        addUserButton = findViewById(R.id.add_user_button);
        backButton = findViewById(R.id.back_button);
        userList = findViewById(R.id.user_list);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        userListData = new ArrayList<>();
        adapter = new UserAdapter(this, userListData);
        userList.setAdapter(adapter);

        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserManagementActivity.this, AddUserActivity.class);
                startActivity(intent); // Start the AddUserActivity
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserManagementActivity.this, AdminActivity.class);
                startActivity(intent);
            }
        });

        searchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        fetchUsers();

        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User selectedUser = userListData.get(position);
                Intent intent = new Intent(UserManagementActivity.this, UserDetailActivity.class);
                intent.putExtra("USER_ID", selectedUser.getId());
                intent.putExtra("USER_FULL_NAME", selectedUser.getFullName());
                intent.putExtra("USER_EMAIL", selectedUser.getEmail());
                intent.putExtra("USER_PASSWORD", selectedUser.getPassword());
                startActivityForResult(intent, 1);
            }
        });
    }

    private void fetchUsers() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userListData.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        userListData.add(user);
                    }
                }
                adapter.notifyDataSetChanged();// Cập nhật lại giao diện
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu cần
            }
        });
    }
    // Lọc danh sách người dùng theo từ khóa tìm kiếm
    private void filterUsers(String query) {
        ArrayList<User> filteredList = new ArrayList<>();
        for (User user : userListData) {
            if (user.getEmail().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(user); // Thêm người dùng nếu email chứa từ khóa tìm kiếm
            }
        }

        adapter = new UserAdapter(this, filteredList);// Cập nhật adapter với danh sách lọc
        userList.setAdapter(adapter);
    }

    // Xử lý kết quả từ Activity chi tiết người dùng
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            fetchUsers(); // Tải lại danh sách người dùng khi quay lại từ màn hình chi tiết
        }
    }
} 