package com.dku.blindnavigation.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

import com.dku.blindnavigation.PermissionUtils;
import com.dku.blindnavigation.R;
import com.dku.blindnavigation.activity.bluetooth.BluetoothConnectActivity;
import com.dku.blindnavigation.activity.guide.DestinationSelectActivity;
import com.dku.blindnavigation.activity.setting.SettingActivity;
import com.dku.blindnavigation.tts.TTSHelper;

public class MainMenuActivity extends AppCompatActivity {
    private static final int PERMISSION_CODE = 101;
    private TTSHelper ttsHelper;
    private boolean locationPermGranted = false;
    private boolean backgroundPermGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        locationPermGranted = PermissionUtils.checkLocationPermissions(this);
        if (locationPermGranted) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                backgroundPermGranted = PermissionUtils.checkBackgroundLocationPermissions(this);
        }

        initButtons();
    }

    private void initButtons() {
        this.<Button>findViewById(R.id.routeGuideBT).setOnClickListener(v -> {
            if(locationPermGranted && backgroundPermGranted) {
                Intent destinationSelectIntent = new Intent(this, DestinationSelectActivity.class);
                startActivity(destinationSelectIntent);
            }
            else ttsHelper.speakString("위치 권한이 허용되지 않았습니다", 1);
        });
        this.<Button>findViewById(R.id.btConnectBT).setOnClickListener(v ->
                startActivity(new Intent(this, BluetoothConnectActivity.class)));
        this.<Button>findViewById(R.id.settingBT).setOnClickListener(v ->
                startActivity(new Intent(this, SettingActivity.class)));
        this.<Button>findViewById(R.id.exitBT).setOnClickListener(v ->
                finish());
    }

    @Override
    protected void onResume() {
        ttsHelper = new TTSHelper(this);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ttsHelper.stopUsing();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != PERMISSION_CODE) return;

        // ACCESS_BACKGROUND_LOCATION
        if(permissions.length == 1) {
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED) return;
            backgroundPermGranted = true;
        }

        // ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION
        else if(permissions.length == 2) {
            for (int grantResult : grantResults)
                if (grantResult != PackageManager.PERMISSION_GRANTED) return;
            locationPermGranted = true;

            // before ACCESS_BACKGROUND_LOCATION granted, ACCESS_COARSE/FIND_LOCATION must be granted
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                backgroundPermGranted = PermissionUtils.checkBackgroundLocationPermissions(this);
        }
    }
}