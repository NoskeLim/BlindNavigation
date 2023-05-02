package com.dku.blindnavigation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

public class TestLocationActivity extends AppCompatActivity {
    private static final int PERMISSION_CODE = 101;

    private boolean locationPermGranted = false;
    private boolean backgroundPermGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_location);

        locationPermGranted = PermissionUtils.checkLocationPermissions(this);
        if(locationPermGranted)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                backgroundPermGranted = PermissionUtils.checkBackgroundLocationPermissions(this);
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