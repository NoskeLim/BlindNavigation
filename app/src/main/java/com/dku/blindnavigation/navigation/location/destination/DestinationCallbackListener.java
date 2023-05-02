package com.dku.blindnavigation.navigation.location.destination;

import com.dku.blindnavigation.navigation.location.dto.Poi;
import java.util.List;

public interface DestinationCallbackListener {
    void onFailureGetDestination();
    void onSuccessGetDestination(List<Poi> pois);
}
