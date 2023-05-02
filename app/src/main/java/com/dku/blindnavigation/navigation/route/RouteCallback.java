package com.dku.blindnavigation.navigation.route;

import androidx.annotation.NonNull;

import com.dku.blindnavigation.navigation.route.dto.Coordinate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RouteCallback implements Callback {
    private final List<RouteCallbackListener> listeners = new ArrayList<>();

    public void addListener(RouteCallbackListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void onFailure(@NonNull Call call, @NonNull IOException e) {
        for (RouteCallbackListener listener : listeners)
            listener.onFailureRoute();
    }

    @Override
    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
        ArrayList<Coordinate> coordinates = new ArrayList<>();

        ObjectMapper mapper = new ObjectMapper();
        String responseString = Objects.requireNonNull(response.body()).string();
        JsonNode featuresNode = mapper.readTree(responseString).path("features");

        for (JsonNode featureNode  : featuresNode) {
            JsonNode geometryNode = featureNode.path("geometry");
            String type = geometryNode.path("type").asText();

            JsonNode coordinatesNode = geometryNode.path("coordinates");
            addCoordinatesToList(coordinates, type, coordinatesNode);
        }

        for (RouteCallbackListener listener : listeners)
            listener.onSuccessRoute(coordinates);
    }

    private static void addCoordinatesToList(ArrayList<Coordinate> coordinates, String type, JsonNode coordinatesNode) {
        if(type.equals("Point")) {
            coordinates.add(new Coordinate(coordinatesNode.get(0).asDouble(), coordinatesNode.get(1).asDouble()));
        }
        else if (type.equals("LineString")) {
            for (JsonNode coordinateNode : coordinatesNode)
                coordinates.add(new Coordinate(coordinateNode.get(0).asDouble(), coordinateNode.get(1).asDouble()));
        }
    }
}
