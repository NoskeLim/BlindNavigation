package com.dku.blindnavigation.navigation.location;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.dku.blindnavigation.navigation.location.destination.DestinationHttpClient;
import com.dku.blindnavigation.navigation.location.dto.Poi;
import com.dku.blindnavigation.navigation.route.RouteHttpClient;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import okhttp3.Callback;

public class LocationUtils {
    public static String getLocationNameByCoord(Context context, double lat, double lng) throws IOException {
        Geocoder geocoder = new Geocoder(context, Locale.KOREA);
        List<Address> locations = geocoder.getFromLocation(lat, lng, 1);
        if(locations.isEmpty()) throw new RuntimeException();

        String addressLine = null;
        for (Address location : locations) {
            addressLine = location.getAddressLine(0);
            if(addressLine != null) break;
        }
        return addressLine;
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static void getLocationNameByCoord(Context context, double lat, double lng, Geocoder.GeocodeListener listener) {
        Geocoder geocoder = new Geocoder(context, Locale.KOREA);
        geocoder.getFromLocation(lat, lng, 1, listener);
    }

    public static void getLocationInfoByName(@NotNull String name, Context context, Callback callback) {
        DestinationHttpClient httpClient = DestinationHttpClient.getInstance();
        httpClient.requestDestination(name, context, callback);
    }

    public static void getRoute(Poi startLocation, Poi endLocation, Context context, Callback callback) {
        RouteHttpClient httpClient = RouteHttpClient.getInstance();
        httpClient.requestRoute(startLocation, endLocation, context, callback);
    }
}
