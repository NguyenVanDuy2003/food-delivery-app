package com.example.food;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import androidx.core.splashscreen.SplashScreen;

import com.example.food.Common.CommonKey;
import com.example.food.Enum.Role;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        splashScreen.setKeepOnScreenCondition(() -> false);

        new Handler().postDelayed(this::checkLoginStatus, 2000);
    }

    public void checkLoginStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences(CommonKey.MY_APP_PREFS, MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean(CommonKey.IS_LOGGED_IN, false);
        String role = sharedPreferences.getString(CommonKey.ROLE, String.valueOf(Role.USER));


        if (isLoggedIn) {

            if (String.valueOf(Role.USER).equals(role)) {
                Intent intent = new Intent(this, HomeActivity.class);
                this.startActivity(intent);
                this.finish();
            } else {
                Intent intent = new Intent(this, MainActivity.class);
                this.startActivity(intent);
                this.finish();
            }

        } else {
            Intent intent = new Intent(this, MainActivity.class);
            this.startActivity(intent);
            this.finish();
        }
    }
}
