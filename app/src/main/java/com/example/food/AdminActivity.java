package com.example.food;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.food.Common.CommonKey;

public class AdminActivity extends AppCompatActivity {
    private Button  btn_quanliadmin, btn_quanliuser , btn_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Khởi tạo Các view

        btn_quanliadmin = findViewById(R.id.btn_quanliadmin);
        btn_quanliuser = findViewById(R.id.btn_quanliuser);
        btn_logout = findViewById(R.id.btn_logout);


        btn_quanliadmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminActivity.this, AdminManagementActivity.class);
                startActivity(intent);
            }
        });
        btn_quanliuser.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminActivity.this, UserManagementActivity.class);
                startActivity(intent);
            }
        });
        btn_logout.setOnClickListener(v -> logout());
    }

    private void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences(CommonKey.MY_APP_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
