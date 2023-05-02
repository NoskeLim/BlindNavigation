package com.dku.blindnavigation.navigation.route;

import com.dku.blindnavigation.navigation.route.dto.Coordinate;
import java.util.List;

public interface RouteCallbackListener {
    void onFailureRoute();
    void onSuccessRoute(List<Coordinate> coordinates);
}
