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
    public static Poi getDepartureCoord(Context context) {
        GpsTracker gpsTracker = new GpsTracker(context);
        Poi departure = new Poi(gpsTracker.getLongitude(), gpsTracker.getLatitude());

        if(departure.getFrontLat() == 0.0 || departure.getFrontLon() == 0.0) return null;
        gpsTracker.stopUsingGPS();
        return departure;
    }

    public static String getDepartureName(Context context, double lng, double lat) throws IOException {
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
    public static void getDepartureName(Context context, double lng, double lat, Geocoder.GeocodeListener listener) {
        Geocoder geocoder = new Geocoder(context);
        geocoder.getFromLocation(lat, lng, 1, listener);
    }

    public static void getDestinationInfo(@NotNull String name, Context context, Callback callback) {
        DestinationHttpClient httpClient = DestinationHttpClient.getInstance();
        httpClient.requestDestination(name, context, callback);
    }

    public static void getRoute(Poi startLocation, Poi endLocation, Context context, Callback callback) {
        RouteHttpClient httpClient = RouteHttpClient.getInstance();
        httpClient.requestRoute(startLocation, endLocation, context, callback);
    }
}
