package com.example.food;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.food.model.User;
import com.google.firebase.database.*;

import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity {

    private ListView listViewUsers;
    private ArrayAdapter<User> userAdapter;
    private ArrayList<User> userList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Initialize ListView and Adapter
        listViewUsers = findViewById(R.id.listView_users);
        userList = new ArrayList<>();
        userAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userList);
        listViewUsers.setAdapter(userAdapter);

        // Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        loadUsers();

        // Add user button listener
        findViewById(R.id.btn_add_user).setOnClickListener(v -> showAddUserDialog());

        // Edit and delete buttons
        Button btnEditUser = findViewById(R.id.btn_edit_user);
        Button btnDeleteUser = findViewById(R.id.btn_delete_user);

        // Button functionality
        btnEditUser.setOnClickListener(v -> {
            // Kiểm tra xem người dùng có được chọn trong ListView không
            int position = listViewUsers.getCheckedItemPosition();
            if (position != AdapterView.INVALID_POSITION) {
                User selectedUser = userList.get(position);
                showEditUserDialog(selectedUser);
            } else {
                Toast.makeText(AdminActivity.this, "Please select a user to edit.", Toast.LENGTH_SHORT).show();
            }
        });

        btnDeleteUser.setOnClickListener(v -> {
            int position = listViewUsers.getCheckedItemPosition();
            if (position != AdapterView.INVALID_POSITION) {
                User selectedUser = userList.get(position);
                deleteUser(selectedUser);
            } else {
                Toast.makeText(AdminActivity.this, "Please select a user to delete.", Toast.LENGTH_SHORT).show();
            }
        });


        // Handling item click for editing or deleting users
        listViewUsers.setOnItemClickListener((parent, view, position, id) -> {
            User selectedUser = userList.get(position);
            showEditDeleteDialog(selectedUser);
        });
    }

    // Load users from Firebase
    private void loadUsers() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user != null) {
                        userList.add(user);
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminActivity.this, "Unable to load users.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Show dialog to add new user
    private void showAddUserDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.item_user, null);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setView(dialogView);

        EditText etFullName = dialogView.findViewById(R.id.tv_fullName);
        EditText etEmail = dialogView.findViewById(R.id.tv_email);
        EditText etPassword = dialogView.findViewById(R.id.tv_password);

        dialog.setTitle("Add User")
                .setPositiveButton("Add", (d, which) -> {
                    String fullName = etFullName.getText().toString().trim();
                    String email = etEmail.getText().toString().trim();
                    String password = etPassword.getText().toString().trim();

                    if (!fullName.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                        String key = email.replace(".", ",");
                        databaseReference.child(key).setValue(new User(fullName, email, password))
                                .addOnSuccessListener(aVoid -> Toast.makeText(this, "User added successfully.", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(this, "Failed to add user.", Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Show dialog to edit or delete user
    private void showEditDeleteDialog(User user) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Edit or Delete User")
                .setPositiveButton("Edit", (d, which) -> showEditUserDialog(user))
                .setNegativeButton("Delete", (d, which) -> deleteUser(user))
                .show();
    }

    // Show dialog to edit user
    private void showEditUserDialog(User user) {
        View dialogView = getLayoutInflater().inflate(R.layout.item_user, null);  // Reuse the same layout for edit
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setView(dialogView);

        EditText etFullName = dialogView.findViewById(R.id.tv_fullName);
        EditText etEmail = dialogView.findViewById(R.id.tv_email);
        EditText etPassword = dialogView.findViewById(R.id.tv_password);

        // Set existing values in the fields
        etFullName.setText(user.getFullName());
        etEmail.setText(user.getEmail());
        etPassword.setText(user.getPassword());
        etEmail.setEnabled(false);  // Prevent email modification

        dialog.setTitle("Edit User")
                .setPositiveButton("Save", (d, which) -> {
                    String newFullName = etFullName.getText().toString().trim();
                    String newPassword = etPassword.getText().toString().trim();

                    if (!newFullName.isEmpty() && !newPassword.isEmpty()) {
                        String key = user.getEmail().replace(".", ",");
                        databaseReference.child(key).child("fullName").setValue(newFullName);
                        databaseReference.child(key).child("password").setValue(newPassword)
                                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Update successful.", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(this, "Update failed.", Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(this, "Fields cannot be empty.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Delete a user from Firebase
    private void deleteUser(User user) {
        String key = user.getEmail().replace(".", ",");
        databaseReference.child(key).removeValue()
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "User deleted successfully.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to delete user.", Toast.LENGTH_SHORT).show());
    }

}
