package com.dku.blindnavigation.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.dku.blindnavigation.R;
import com.dku.blindnavigation.activity.bluetooth.BluetoothConnectActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView capstone_icon = findViewById(R.id.logo);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        capstone_icon.startAnimation(anim);

        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                applicationStart();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void applicationStart() {
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