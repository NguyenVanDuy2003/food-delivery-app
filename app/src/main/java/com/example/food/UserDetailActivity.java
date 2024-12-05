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
    private TextView userIdTextView;
    private EditText userFullNameEditText;
    private EditText userEmailEditText;
    private EditText userPasswordEditText;
    private EditText userPhoneNumberEditText;
    private EditText userAddressEditText;
    private Spinner userRoleSpinner;
    private Button updateButton;
    private Button deleteButton;
    private Button backButton;

    private DatabaseReference databaseReference;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        userIdTextView = findViewById(R.id.user_id_text_view);
        userFullNameEditText = findViewById(R.id.user_full_name_edit_text);
        userEmailEditText = findViewById(R.id.user_email_edit_text);
        userPasswordEditText = findViewById(R.id.user_password_edit_text);
        userPhoneNumberEditText = findViewById(R.id.user_phone_number_edit_text);
        userAddressEditText = findViewById(R.id.user_address_edit_text);
        userRoleSpinner = findViewById(R.id.user_role_spinner);
        updateButton = findViewById(R.id.update_button);
        deleteButton = findViewById(R.id.delete_button);
        backButton = findViewById(R.id.back_button);

        // Get user data from Intent
        userId = getIntent().getStringExtra("USER_ID");
        String userFullName = getIntent().getStringExtra("USER_FULL_NAME");
        String userEmail = getIntent().getStringExtra("USER_EMAIL");
        String userPassword = getIntent().getStringExtra("USER_PASSWORD");
        String userPhoneNumber = getIntent().getStringExtra("USER_PHONE_NUMBER");
        String userAddress = getIntent().getStringExtra("USER_ADDRESS");
        String userRole = getIntent().getStringExtra("USER_ROLE");

        // Display user information
        userIdTextView.setText(userId);
        userFullNameEditText.setText(userFullName);
        userEmailEditText.setText(userEmail);
        userPasswordEditText.setText(userPassword);
        userPhoneNumberEditText.setText(userPhoneNumber);
        userAddressEditText.setText(userAddress);

        // Initialize Spinner for role
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.user_roles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userRoleSpinner.setAdapter(adapter);

        // Select role from Intent
        if (userRole != null) {
            int spinnerPosition = adapter.getPosition(userRole);
            userRoleSpinner.setSelection(spinnerPosition);
        } else {
            userRoleSpinner.setSelection(0); // Default role
        }

        // Set up back button listener
        backButton.setOnClickListener(v -> finish());

        // Initialize DatabaseReference
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        // Update user information
        updateButton.setOnClickListener(v -> updateUser());

        // Delete user
        deleteButton.setOnClickListener(v -> confirmDeleteUser());
    }

    private void updateUser() {
        String fullName = userFullNameEditText.getText().toString().trim();
        String email = userEmailEditText.getText().toString().trim();
        String password = userPasswordEditText.getText().toString().trim();
        String phoneNumber = userPhoneNumberEditText.getText().toString().trim();
        String address = userAddressEditText.getText().toString().trim();
        String roleString = userRoleSpinner.getSelectedItem().toString();
        Role role = Role.valueOf(roleString.toUpperCase());

        User user = new User(userId, fullName, email, password, phoneNumber, address, role);
        databaseReference.setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(UserDetailActivity.this, "User updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(UserDetailActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmDeleteUser() {
        new AlertDialog.Builder(this)
            .setTitle("Confirm Delete")
            .setMessage("Are you sure you want to delete this user?")
            .setPositiveButton("Yes", (dialog, which) -> deleteUser())
            .setNegativeButton("No", null)
            .show();
    }

    private void deleteUser() {
        databaseReference.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(UserDetailActivity.this, "User deleted successfully", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(UserDetailActivity.this, "Delete failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}