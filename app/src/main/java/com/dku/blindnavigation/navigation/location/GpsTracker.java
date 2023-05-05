package com.dku.blindnavigation.navigation.location;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.core.content.ContextCompat;

public class GpsTracker extends Service implements LocationListener {

    private final Context mContext;
    Location location;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60;
    protected LocationManager locationManager;

    public GpsTracker(Context context) {
        this.mContext = context;
        initLocation();
    }


    private void initLocation() {
        try {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) return;

            if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
                getLocationFromManager(LocationManager.NETWORK_PROVIDER);
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                getLocationFromManager(LocationManager.GPS_PROVIDER);

        } catch (Exception e) {
            Log.d("GpsTracker", "" + e);
        }
    }

    public Location getLocation() {
        return location;
    }

    private void getLocationFromManager(String provider) {
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
                hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED) return;

        locationManager.requestLocationUpdates(provider, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
        location = locationManager.getLastKnownLocation(provider);
    }

    public double getLatitude() {
        if(location == null) return 0.0;
        return location.getLatitude();
    }

    public double getLongitude() {
        if(location == null) return 0.0;
        return location.getLongitude();
    }

    public double getAltitude() {
        if(location == null) return 0.0;
        return location.getAltitude();
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }


    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(GpsTracker.this);
        }
        stopSelf();
    }
}