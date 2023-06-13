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
    private static final String[] bluetoothPermissionsBeforeVersionCodeS = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN
    };

    @RequiresApi(api = Build.VERSION_CODES.S)
    private static final String[] bluetoothPermissionsAfterVersionCodeS = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT
    };

    private static void requestPermissions(Activity activity, List<String> permissions) {
        ActivityCompat.requestPermissions(activity,
                permissions.toArray(new String[0]),
                PERMISSION_CODE);
    }

    public static boolean checkLocationPermissions(Activity activity) {
        List<String> deniedPermissions = getDeniedPermissions(locationPermissions, activity);
        if(deniedPermissions.isEmpty()) return true;
        requestPermissions(activity, deniedPermissions);
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

    public static boolean checkBluetoothPermissions(Activity activity) {
        List<String> deniedPermissions =
                getDeniedPermissions(Build.VERSION.SDK_INT < Build.VERSION_CODES.S ?
                        bluetoothPermissionsBeforeVersionCodeS : bluetoothPermissionsAfterVersionCodeS, activity);
        if(deniedPermissions.isEmpty()) return true;
        requestPermissions(activity, deniedPermissions);
        return false;
    }

    private static List<String> getDeniedPermissions(String[] bluetoothPermissions, Activity activity) {
        List<String> deniedPermissions = new ArrayList<>();
        for (String permission : bluetoothPermissions) {
            if(ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
                deniedPermissions.add(permission);
            }
        }
        return deniedPermissions;
    }
}
