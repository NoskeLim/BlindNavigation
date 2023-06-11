package com.dku.blindnavigation.navigation.route;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;

import com.dku.blindnavigation.navigation.dto.Poi;

import java.util.ArrayList;
import java.util.List;

public class RouteListener implements RouteCallbackListener{
    public static final int EVENT_TYPE = 400;
    private final Handler handler;

    public RouteListener(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void onFailureRoute() {
        sendErrorToHandler();
    }

    @Override
    public void onSuccessRoute(List<Poi> pois) {
        if(pois.isEmpty()) {
            sendErrorToHandler();
            return;
        }
        sendRouteToHandler(pois);
    }

    private void sendErrorToHandler() {
        Bundle bundle = generateBundle(false);
        sendBundleToHandler(bundle);
    }

    private void sendRouteToHandler(List<Poi> pois) {
        Bundle bundle = generateBundle(true);
        bundle.putParcelableArrayList("route", (ArrayList<? extends Parcelable>) pois);
        sendBundleToHandler(bundle);
    }

    private void sendBundleToHandler(Bundle bundle) {
        Message message = new Message();
        message.setData(bundle);
        handler.sendMessage(message);
    }

    public Bundle generateBundle(boolean status) {
        Bundle bundle = new Bundle();
        bundle.putInt("eventType", EVENT_TYPE);
        bundle.putBoolean("status", status);
        return bundle;
    }
}
