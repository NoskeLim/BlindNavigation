package com.dku.blindnavigation.navigation.location.departure;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.dku.blindnavigation.navigation.location.dto.Poi;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

public class DepartureCoordProvider {
    private final Handler handler;
    private final Activity activity;
    private final FusedLocationProviderClient fusedLocationClient;

    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            fusedLocationClient.removeLocationUpdates(this);
            Location curLocation = locationResult.getLastLocation();
            if(curLocation == null) sendErrorToHandler();
            else sendDepartureCoordToHandler(curLocation);
        }
    };



    public DepartureCoordProvider(Activity activity, Handler handler) {
        this.handler = handler;
        this.activity = activity;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);

    }

    public void startRequestLocation() {
        LocationRequest locationRequest = new LocationRequest
                .Builder(Priority.PRIORITY_HIGH_ACCURACY, 20 * 1000)
                .build();

        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
    }

    public void stopRequestLocation() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private void sendErrorToHandler() {
        Bundle bundle = generateBundleByLocation(false);
        Message message = new Message();
        message.setData(bundle);
        handler.sendMessage(message);
    }

    private void sendDepartureCoordToHandler(Location curLocation) {
        Bundle bundle = generateBundleByLocation(true);
        bundle.putParcelable("departureCoord", new Poi(curLocation.getLatitude(), curLocation.getLongitude()));

        Message message = new Message();
        message.setData(bundle);
        handler.sendMessage(message);
    }

    @NonNull
    private static Bundle generateBundleByLocation(boolean status) {
        Bundle bundle = new Bundle();
        bundle.putInt("eventType", 1);
        bundle.putBoolean("status", status);
        return bundle;
    }
}
