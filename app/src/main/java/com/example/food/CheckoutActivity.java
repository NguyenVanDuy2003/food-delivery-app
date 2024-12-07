package com.example.food;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CheckoutActivity extends AppCompatActivity {
    private Button btnthanhtoanOnline, btnthanhtoanKhiNhanHang , btnquaylai;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnthanhtoanOnline = findViewById(R.id.btn_ThanhToanOnline);
        btnthanhtoanKhiNhanHang = findViewById(R.id.btn_ThanhToanKhiNhanHang);
        btnquaylai = findViewById(R.id.btn_quaylai);


        //
        btnthanhtoanOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckoutActivity.this, ThanhToanOnlActivity.class );
                startActivity(intent);
            }
        });

        btnthanhtoanKhiNhanHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiển thị thông báo bạn có đồng ý hay hủy
                //Tạo một AlertDialog.Builder
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Xác nhận");
                builder.setMessage("Bạn có muốn thanh toán khi nhận hàng?");

                // Nut "Dong y"
                builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Code nếu người dùng chọn nút "Dong y"
                        Toast.makeText(v.getContext(), "Bạn đã chọn thanh toán khi nhận hàng",Toast.LENGTH_SHORT).show();
                        dialog.dismiss(); // đóng hộp hội thoại
                    }

                });

                // Nut "Hủy"
                builder.setNegativeButton("Hủy", (dialog, which) -> {
                    // Code nếu người dùng chọn nút "Hủy"
                });

                // Tạo và hiển thị dialog
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        btnquaylai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close the checkout activity
            }
        });
    }

}