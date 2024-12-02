package com.example.food;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.food.Enum.Role;
import com.example.food.model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddUserActivity extends AppCompatActivity {

    private EditText edtFullName, edtEmail, edtPassword;
    private Button btnSave;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        // Khởi tạo các View
        edtFullName = findViewById(R.id.edtFullName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnSave = findViewById(R.id.btnSave);

        // Khởi tạo Firebase references
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Lưu thông tin người dùng
        btnSave.setOnClickListener(v -> saveUserToDatabase());
    }

    private void saveUserToDatabase() {
        String fullName = edtFullName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String userId = databaseReference.push().getKey();
        Role role = Role.USER;

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(AddUserActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        User newUser = new User(userId, fullName, email, password, role);

        if (userId != null) {
            databaseReference.child(userId).setValue(newUser)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(AddUserActivity.this, "Tài khoản đã được thêm thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AddUserActivity.this, "Lỗi khi lưu dữ liệu", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
