package com.dku.blindnavigation.navigation.location.gps;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.dku.blindnavigation.navigation.location.LocationUtils;

import java.io.IOException;
import java.util.List;

public class LocationNameProvider {
    public static final int EVENT_TYPE = 300;
    private final Handler handler;

    public LocationNameProvider(Handler handler) {
        this.handler = handler;
    }

    public void getDepartureInfo(Context context, double latitude, double longitude) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            try {
                String departureName = LocationUtils.getLocationNameByCoord(context, latitude, longitude);
                sendDepartureNameToHandler(departureName);

            } catch (IOException ignored) {}
        }
        else LocationUtils.getLocationNameByCoord(context, latitude, longitude, new DepartureListener());
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public class DepartureListener implements Geocoder.GeocodeListener {
        @Override
        public void onGeocode(@NonNull List<Address> addresses) {
            if(addresses.isEmpty()) throw new RuntimeException();

            String addressLine = null;
            for (Address location : addresses) {
                addressLine = location.getAddressLine(0);
                if(addressLine != null) break;
            }
            if(addressLine == null) sendErrorToHandler();
            sendDepartureNameToHandler(addressLine);
        }

        @Override
        public void onError(@Nullable String errorMessage) {
            Geocoder.GeocodeListener.super.onError(errorMessage);
            sendErrorToHandler();
        }
    }

    private void sendErrorToHandler() {
        Bundle bundle = generateBundleByLocation(false);
        Message message = new Message();
        message.setData(bundle);
        handler.sendMessage(message);
    }

    private void sendDepartureNameToHandler(String name) {
        Bundle bundle = generateBundleByLocation(true);
        bundle.putString("departureName", name);

        Message message = new Message();
        message.setData(bundle);
        handler.sendMessage(message);
    }

    @NonNull
    private static Bundle generateBundleByLocation(boolean status) {
        Bundle bundle = new Bundle();
        bundle.putInt("eventType", EVENT_TYPE);
        bundle.putBoolean("status", status);
        return bundle;
    }
}
