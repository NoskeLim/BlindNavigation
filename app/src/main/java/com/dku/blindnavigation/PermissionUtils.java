package com.dku.blindnavigation;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionUtils {
    private static final int PERMISSION_CODE = 101;
    private static final String[] locationPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    public static boolean checkLocationPermissions(Activity activity) {
        List<String> deniedPermissions = new ArrayList<>();
        for (String permission : locationPermissions) {
            if(ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
                deniedPermissions.add(permission);
            }
        }

        if(deniedPermissions.isEmpty()) return true;

        String[] deniedPermissionArray = new String[deniedPermissions.size()];
        ActivityCompat.requestPermissions(activity, deniedPermissions.toArray(deniedPermissionArray), PERMISSION_CODE);
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static boolean checkBackgroundLocationPermissions(Activity activity) {
        String backgroundPermission = Manifest.permission.ACCESS_BACKGROUND_LOCATION;
        if(ActivityCompat.checkSelfPermission(activity, backgroundPermission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.shouldShowRequestPermissionRationale(activity, backgroundPermission);
            ActivityCompat.requestPermissions(activity, new String[] {backgroundPermission}, PERMISSION_CODE);
            return false;
        }
        return true;
    }
}
