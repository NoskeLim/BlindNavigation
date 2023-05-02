package com.dku.blindnavigation.navigation.location;

import android.content.Context;

import com.dku.blindnavigation.navigation.location.destination.DestinationHttpClient;

import org.jetbrains.annotations.NotNull;

import okhttp3.Callback;

public class LocationUtils {
    public static void getDestinationInfo(@NotNull String name, Context context, Callback callback) {
        DestinationHttpClient httpClient = DestinationHttpClient.getInstance();
        httpClient.requestDestination(name, context, callback);
    }
}
