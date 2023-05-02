package com.dku.blindnavigation.navigation.location.destination;

import androidx.annotation.NonNull;

import com.dku.blindnavigation.navigation.location.dto.DestinationResponse;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DestinationCallback implements Callback {
    private final List<DestinationCallbackListener> listeners = new ArrayList<>();

    public void addListener(DestinationCallbackListener listener) {
        listeners.add(listener);
    }

    @Override
    public void onFailure(@NonNull Call call, @NonNull IOException e) {
        for (DestinationCallbackListener listener : listeners)
            listener.onFailureGetDestination();
    }

    @Override
    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String str = Objects.requireNonNull(response.body()).string();
        DestinationResponse locationResponse = mapper.readValue(str, DestinationResponse.class);

        for (DestinationCallbackListener listener : listeners)
            listener.onSuccessGetDestination(locationResponse.getPois());
    }
}
