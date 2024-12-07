package com.example.food;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class ThanhToanOnlActivity extends AppCompatActivity {
    // ánh xa
    private ImageView qrcode ;
    private TextView nameRestaurants , idstk, price ;
    private Button btnquaylai , btnluuanh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_thanh_toan_onl);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        qrcode = findViewById(R.id.id_qrcode);
        nameRestaurants = findViewById(R.id.nameRestaurants);
        idstk = findViewById(R.id.id_stk);
        btnquaylai = findViewById(R.id.btn_quaylai);
        btnluuanh = findViewById(R.id.btn_luuanh);

        btnquaylai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }

        });
        btnluuanh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1 lấy Bitmap từ ImageView
                qrcode.setDrawingCacheEnabled(true); // Bật cache cho ImageView
                qrcode.buildDrawingCache(); // tạo bản vẽ cache từ nội dung của ImageView
                Bitmap bitmap = Bitmap.createBitmap(qrcode.getDrawingCache());
                qrcode.setDrawingCacheEnabled(false); //Tắt cache để giải phóng bộ nhớ

                // 2 Lưu ảnh vào thư viện điện thoại
                try {
                    // 3. Khởi tạo đường dẫn cho ảnh
                    String savedImageURL;

                    // 4 Kiểm tra phiên bản Androi
                    if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q){
                        // với Androi 10 trở lên , sử dụng MediaStore

                        ContentResolver resolver = getContentResolver();
                        // Lấy đối tượng ContenResolver để thao tác với MediaStore

                        ContentValues contentValues = new ContentValues();
                        // Khởi taoj đối tượng để chứa thông tin ảnh

                        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "QRCode.png"); // Tên ảnh
                        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");  //Định dạng file ảnh
                        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/QRCode"); // Đường dẫn đến

                        // tạo Uri cho ảnh mới trong MediaStore
                        Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);


                        // 5. Ghi ảnh vào MediaStore
                        if(imageUri != null){
                            OutputStream outputStream = resolver.openOutputStream(imageUri);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                            outputStream.close();
                            savedImageURL = outputStream.toString(); // lưu đường dẫn
                        } else {
                            Toast.makeText(ThanhToanOnlActivity.this, "Lưu ảnh thất bại", Toast.LENGTH_SHORT).show();
                            return;
                        }



                    } else {
                        // với phiên bản Android thấp hơn Androi 10, sử dụng File
                        File file = new File(Environment.getExternalStorageDirectory() + "/QRCode.png");
                        savedImageURL = file.getAbsolutePath();
                    }
                    // 6. Scan ảnh vào thư viện điện thoại
                    Toast.makeText(ThanhToanOnlActivity.this,"Ảnh được lưu tại: " + savedImageURL,Toast.LENGTH_SHORT).show();
                }catch(Exception e){
                    e.printStackTrace();
                    Toast.makeText(ThanhToanOnlActivity.this, "Lưu ảnh thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}