package com.dku.blindnavigation.navigation.location;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.dku.blindnavigation.navigation.location.destination.DestinationHttpClient;
import com.dku.blindnavigation.navigation.dto.Poi;
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

    public static double getDistance(double lat1, double lon1, double lat2, double lon2){
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))* Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1))*Math.cos(deg2rad(lat2))*Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60*1.1515*1609.344;

        return Double.isNaN(dist) ? 0 : dist; //단위 meter
    }

    private static double deg2rad(double deg){
        return (deg * Math.PI/180.0);
    }
    //radian(라디안)을 10진수로 변환
    private static double rad2deg(double rad){
        return (rad * 180 / Math.PI);
    }
}
