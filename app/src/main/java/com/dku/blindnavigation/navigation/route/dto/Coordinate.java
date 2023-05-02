package com.dku.blindnavigation.navigation.route.dto;

import java.io.Serializable;

public class Coordinate implements Serializable {
    private final double latitude;
    private final double longitude;

    public Coordinate(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "Coordinate{" + latitude +  ", " + longitude + '}';
    }
}
