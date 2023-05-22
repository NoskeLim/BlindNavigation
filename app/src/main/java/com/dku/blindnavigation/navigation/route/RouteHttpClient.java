package com.dku.blindnavigation.navigation.route;

import android.content.Context;

import androidx.annotation.NonNull;

import com.dku.blindnavigation.R;
import com.dku.blindnavigation.navigation.dto.Poi;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class RouteHttpClient {
    private final OkHttpClient okClient;
    private static final RouteHttpClient instance = new RouteHttpClient();
    public static RouteHttpClient getInstance() {
        return instance;
    }

    private RouteHttpClient() {
        this.okClient = new OkHttpClient();
    }

    public void requestRoute(Poi startLocation, Poi endLocation, Context context, Callback callback) {
        RequestBody body = generateRouteRequestBody(startLocation, endLocation);
        Request request = new Request.Builder()
                .url(context.getString(R.string.sk_api_route_url))
                .addHeader("accept", "application/json")
                .addHeader("content-type", "application/json")
                .addHeader("appKey", context.getString(R.string.sk_api_key))
                .post(body)
                .build();

        okClient.newCall(request).enqueue(callback);
    }

    @NonNull
    private RequestBody generateRouteRequestBody(Poi startLocation, Poi endLocation) {
        return new FormBody.Builder()
                .add("startX", String.valueOf(startLocation.getFrontLon()))
                .add("startY", String.valueOf(startLocation.getFrontLat()))
                .add("endX", String.valueOf(endLocation.getFrontLon()))
                .add("endY", String.valueOf(endLocation.getFrontLat()))
                .addEncoded("startName", startLocation.getName())
                .addEncoded("endName", endLocation.getName())
                .build();
    }
}
