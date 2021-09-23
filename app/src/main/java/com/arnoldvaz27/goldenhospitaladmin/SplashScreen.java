package com.arnoldvaz27.goldenhospitaladmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.golden));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.white));
        setContentView(R.layout.splash_screen);
        new Handler().postDelayed(() -> {
            Intent i = new Intent(SplashScreen.this,
                    MainActivity.class);
            SplashScreen.this.startActivity(i);
            SplashScreen.this.finishAffinity();
        }, 3000);
    }
}