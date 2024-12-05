package com.example.food;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import com.example.food.Enum.Role;
import com.example.food.Model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserDetailActivity extends AppCompatActivity {
    private TextView userIdTextView; // TextView để hiển thị ID
    private EditText userFullNameEditText; // EditText để hiển thị tên đầy đủ
    private EditText userEmailEditText; // EditText để hiển thị email
    private EditText userPasswordEditText; // EditText để hiển thị mật khẩu
    private Spinner userRoleSpinner; // Spinner để chọn role
    private Button updateButton; // Nút cập nhật
    private Button deleteButton; // Nút xóa
    private Button backButton; // Nút quay lại

    private DatabaseReference databaseReference; // Tham chiếu đến cơ sở dữ liệu
    private String userId; // ID của người dùng

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        userIdTextView = findViewById(R.id.user_id_text_view);
        userFullNameEditText = findViewById(R.id.user_full_name_edit_text);
        userEmailEditText = findViewById(R.id.user_email_edit_text);
        userPasswordEditText = findViewById(R.id.user_password_edit_text);
        userRoleSpinner = findViewById(R.id.user_role_spinner);
        updateButton = findViewById(R.id.update_button);
        deleteButton = findViewById(R.id.delete_button);
        backButton = findViewById(R.id.back_button); // Initialize the back button

        // Nhận dữ liệu từ Intent
        userId = getIntent().getStringExtra("USER_ID");
        String userFullName = getIntent().getStringExtra("USER_FULL_NAME");
        String userEmail = getIntent().getStringExtra("USER_EMAIL");
        String userPassword = getIntent().getStringExtra("USER_PASSWORD");
        String userRole = getIntent().getStringExtra("USER_ROLE");

        // Hiển thị thông tin người dùng
        userIdTextView.setText(userId);
        userFullNameEditText.setText(userFullName);
        userEmailEditText.setText(userEmail);
        userPasswordEditText.setText(userPassword);

        // Khởi tạo Spinner cho role
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.user_roles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userRoleSpinner.setAdapter(adapter);

        // Chọn role từ Intent
        if (userRole != null) {
            int spinnerPosition = adapter.getPosition(userRole);
            userRoleSpinner.setSelection(spinnerPosition);
        } else {
            // Set default role if userRole is null
            userRoleSpinner.setSelection(0); // Assuming the first item is a default role
        }

        // Set up back button listener
        backButton.setOnClickListener(v -> finish()); // Go back to the previous activity

        // Khởi tạo DatabaseReference
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        // Cập nhật thông tin người dùng
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser();
            }
        });

        // Xóa người dùng
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDeleteUser(); // Call the confirmation method
            }
        });
    }

    private void updateUser() {
        String fullName = userFullNameEditText.getText().toString().trim();
        String email = userEmailEditText.getText().toString().trim();
        String password = userPasswordEditText.getText().toString().trim();
        String roleString = userRoleSpinner.getSelectedItem().toString();
        Role role = Role.valueOf(roleString.toUpperCase());
        User user = new User(userId, fullName, email, password, role);
        databaseReference.setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(UserDetailActivity.this, "User updated successfully", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK); // Set result to OK
                finish();
            } else {
                Toast.makeText(UserDetailActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteUser() {
        databaseReference.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(UserDetailActivity.this, "User deleted successfully", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK); // Set result to OK
                finish(); // Quay lại activity trước đó
            } else {
                Toast.makeText(UserDetailActivity.this, "Delete failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to confirm deletion
    private void confirmDeleteUser() {
        new AlertDialog.Builder(this)
            .setTitle("Confirm Delete")
            .setMessage("Are you sure you want to delete this user?")
            .setPositiveButton("Yes", (dialog, which) -> deleteUser()) // Proceed to delete if confirmed
            .setNegativeButton("No", null) // Do nothing if cancelled
            .show();
    }
}