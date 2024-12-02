package com.example.food;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnSkip;
    private Button btnEmailPhone;
    private TextView btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo các View từ layout
        btnSkip = findViewById(R.id.btn_skip);
        btnEmailPhone = findViewById(R.id.btn_email_phone);
        btnSignIn = findViewById(R.id.tv_sign_in);

        // Xử lý sự kiện cho nút Skip
        btnSkip.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        // Xử lý sự kiện cho nút Email/Phone
        btnEmailPhone.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
            startActivity(intent);
        });

        // Xử lý sự kiện cho nút Sign Up
        btnSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
