package com.dku.blindnavigation.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.dku.blindnavigation.R;
import com.dku.blindnavigation.activity.bluetooth.BluetoothConnectActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Intent intent = new Intent(this,
                getSharedPreferences("setting", MODE_PRIVATE).getString("MAC_ADDR", null) != null ?
                        MainMenuActivity.class :
                        BluetoothConnectActivity.class);
        new Handler().postDelayed(() -> {
            startActivity(intent);
            finish();
        }, 1500);
    }
}