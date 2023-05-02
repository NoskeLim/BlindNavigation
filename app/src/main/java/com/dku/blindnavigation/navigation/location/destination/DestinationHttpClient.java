package com.dku.blindnavigation.navigation.location.destination;

import android.content.Context;
import androidx.annotation.NonNull;

import com.dku.blindnavigation.R;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class DestinationHttpClient {
    private final OkHttpClient okClient;
    private static final DestinationHttpClient instance = new DestinationHttpClient();
    public static DestinationHttpClient getInstance() {
        return instance;
    }

    private DestinationHttpClient() {
        this.okClient = new OkHttpClient();
    }

    public void requestDestination(@NotNull String name, Context context, Callback callback) {
        Request request = generateRequest(context, name);
        okClient.newCall(request).enqueue(callback);
    }

    @NonNull
    private static Request generateRequest(Context context, String name) {
        return new Request.Builder()
                .url(generateRequestUrl(name, context))
                .addHeader("Accept", "application/json")
                .addHeader("appKey", context.getString(R.string.sk_api_key))
                .build();
    }

    @NonNull
    private static HttpUrl generateRequestUrl(@NonNull String name, Context context) {
        HttpUrl.Builder httpBuilder = Objects.requireNonNull(
                HttpUrl.parse(context.getString(R.string.sk_api_location_url))
        ).newBuilder();
        httpBuilder.setQueryParameter("version", "1");
        httpBuilder.setEncodedQueryParameter("searchKeyword", name);
        return httpBuilder.build();
    }
}
