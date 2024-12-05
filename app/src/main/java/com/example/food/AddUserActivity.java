package com.example.food;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.food.Enum.Role;
import com.example.food.Model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddUserActivity extends AppCompatActivity {
    private EditText userFullNameEditText;
    private EditText userEmailEditText;
    private EditText userPasswordEditText;
    private EditText userPhoneNumberEditText;
    private EditText userAddressEditText;
    private Spinner userRoleSpinner;
    private Button saveButton;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        userFullNameEditText = findViewById(R.id.user_full_name_edit_text);
        userEmailEditText = findViewById(R.id.user_email_edit_text);
        userPasswordEditText = findViewById(R.id.user_password_edit_text);
        userPhoneNumberEditText = findViewById(R.id.user_phone_number_edit_text);
        userAddressEditText = findViewById(R.id.user_address_edit_text);
        userRoleSpinner = findViewById(R.id.user_role_spinner);
        saveButton = findViewById(R.id.save_button);

        // Populate the spinner with user roles
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.user_roles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userRoleSpinner.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        saveButton.setOnClickListener(v -> addUser());
    }

    private void addUser() {
        String fullName = userFullNameEditText.getText().toString().trim();
        String email = userEmailEditText.getText().toString().trim();
        String password = userPasswordEditText.getText().toString().trim();
        String phoneNumber = userPhoneNumberEditText.getText().toString().trim();
        String address = userAddressEditText.getText().toString().trim();

        // Check if the selected item is null
        String roleString = userRoleSpinner.getSelectedItem() != null ? userRoleSpinner.getSelectedItem().toString() : null;
        if (roleString == null) {
            Toast.makeText(this, "Please select a role", Toast.LENGTH_SHORT).show();
            return; // Exit the method if no role is selected
        }

        Role role = Role.valueOf(roleString.toUpperCase());

        String userId = databaseReference.push().getKey(); // Generate a unique ID for the user
        User user = new User(userId, fullName, email, password, phoneNumber, address, role);
        databaseReference.child(userId).setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(AddUserActivity.this, "User added successfully", Toast.LENGTH_SHORT).show();
                finish(); // Go back to the previous activity
            } else {
                Toast.makeText(AddUserActivity.this, "Failed to add user", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
