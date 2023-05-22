package com.dku.blindnavigation.navigation.route;

import com.dku.blindnavigation.navigation.dto.Poi;

import java.util.List;

public interface RouteCallbackListener {
    void onFailureRoute();
    void onSuccessRoute(List<Poi> pois);
}
