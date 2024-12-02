package com.example.food;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;

import com.example.food.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

    public class UserDetailActivity extends Activity {
        private EditText edtFullName, edtEmail, edtPassword;
        private TextView tvFullName, tvEmail, tvPassword;
        private Button btnUpdate, btnCancel;

        private int userId;
        private User user;


        private DatabaseReference databaseReference;
        private FirebaseAuth firebaseAuth;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.item_user);

            initViews();
            initFirebase();
            loadData();
            setupOnClickListeners();
        }

        private void initViews() {
            edtFullName = findViewById(R.id.et_full_name);
            edtEmail = findViewById(R.id.et_email);
            edtPassword = findViewById(R.id.et_password);

            tvFullName = findViewById(R.id.tv_fullName);
            tvEmail = findViewById(R.id.tv_email);
            tvPassword = findViewById(R.id.tv_password);

            btnUpdate = findViewById(R.id.btnUpdate);
            btnCancel = findViewById(R.id.btnCancel);
        }

        private void initFirebase() {
            // Initialize Firebase Database reference
            databaseReference = FirebaseDatabase.getInstance().getReference("users");
        }

        private void loadData() {
            userId = getIntent().getIntExtra("user_id", -1);

            if (userId == -1) {
                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Load user data from Firebase
            databaseReference.child(String.valueOf(userId)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            tvFullName.setText(user.getFullName());
                            tvEmail.setText(user.getEmail());
                            tvPassword.setText(user.getPassword());

                            edtFullName.setText(user.getFullName());
                            edtEmail.setText(user.getEmail());
                            edtPassword.setText(user.getPassword());
                        }
                    } else {
                        Toast.makeText(UserDetailActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(UserDetailActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void setupOnClickListeners() {
            btnUpdate.setOnClickListener(v -> {
                if (validateUserData()) {
                    updateUser();
                }
            });

            btnCancel.setOnClickListener(v -> finish());
        }

        private boolean validateUserData() {
            if (edtFullName.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Please enter full name", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (edtEmail.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (edtPassword.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }

        private void updateUser() {
            user.setFullName(edtFullName.getText().toString().trim());
            user.setEmail(edtEmail.getText().toString().trim());
            user.setPassword(edtPassword.getText().toString().trim());


            databaseReference.child(String.valueOf(userId)).setValue(user)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "User updated successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
