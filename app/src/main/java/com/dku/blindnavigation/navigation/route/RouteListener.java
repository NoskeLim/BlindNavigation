package com.dku.blindnavigation.navigation.route;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import com.dku.blindnavigation.navigation.route.dto.Coordinate;

import java.util.ArrayList;
import java.util.List;

public class RouteListener implements RouteCallbackListener{
    private final Handler handler;

    public RouteListener(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void onFailureRoute() {
        sendErrorToHandler();
    }

    @Override
    public void onSuccessRoute(List<Coordinate> coordinates) {
        if(coordinates.isEmpty()) {
            sendErrorToHandler();
            return;
        }
        sendRouteToHandler(coordinates);
    }

    private void sendErrorToHandler() {
        Bundle bundle = generateBundle(false);
        sendBundleToHandler(bundle);
    }

    private void sendRouteToHandler(List<Coordinate> coordinates) {
        Bundle bundle = generateBundle(true);
        bundle.putParcelableArrayList("route", (ArrayList<? extends Parcelable>) coordinates);
        sendBundleToHandler(bundle);
    }

    private void sendBundleToHandler(Bundle bundle) {
        Message message = new Message();
        message.setData(bundle);
        handler.sendMessage(message);
    }

    public Bundle generateBundle(boolean status) {
        Bundle bundle = new Bundle();
        bundle.putInt("eventType", 3);
        bundle.putBoolean("status", status);
        return bundle;
    }
}
