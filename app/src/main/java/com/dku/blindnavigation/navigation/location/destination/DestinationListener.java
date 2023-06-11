package com.dku.blindnavigation.navigation.location.destination;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;

import com.dku.blindnavigation.navigation.dto.Poi;

import java.util.ArrayList;
import java.util.List;

public class DestinationListener implements DestinationCallbackListener {
    public static final int EVENT_TYPE = 100;
    private final Handler handler;

    public DestinationListener(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void onFailureGetDestination() {
        sendErrorToHandler();
    }

    @Override
    public void onSuccessGetDestination(List<Poi> pois) {
        sendDestinationsToHandler(pois);
    }

    private void sendErrorToHandler() {
        Bundle bundle = generateBundle(false);
        sendBundleToHandler(bundle);
    }

    private void sendDestinationsToHandler(List<Poi> pois) {
        Bundle bundle = generateBundle(true);
        bundle.putParcelableArrayList("pois", (ArrayList<? extends Parcelable>) pois);
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