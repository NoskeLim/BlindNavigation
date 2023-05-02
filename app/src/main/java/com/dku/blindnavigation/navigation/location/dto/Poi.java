package com.dku.blindnavigation.navigation.location.dto;

public class Poi {
    private String name;
    private String upperAddrName;
    private String middleAddrName;
    private String lowerAddrName;
    private double frontLon;
    private double frontLat;

    public Poi() {
    }

    public Poi(double frontLon, double frontLat) {
        this.frontLon = frontLon;
        this.frontLat = frontLat;
    }

    public Poi(String name, double frontLon, double frontLat) {
        this.name = name;
        this.frontLon = frontLon;
        this.frontLat = frontLat;
    }

    public Poi(String name, String upperAddrName, String middleAddrName, String lowerAddrName, double frontLon, double frontLat) {
        this.name = name;
        this.upperAddrName = upperAddrName;
        this.middleAddrName = middleAddrName;
        this.lowerAddrName = lowerAddrName;
        this.frontLon = frontLon;
        this.frontLat = frontLat;
    }

    public String getName() {
        return name;
    }

    public String getUpperAddrName() {
        return upperAddrName;
    }

    public String getMiddleAddrName() {
        return middleAddrName;
    }

    public String getLowerAddrName() {
        return lowerAddrName;
    }

    public double getFrontLon() {
        return frontLon;
    }

    public double getFrontLat() {
        return frontLat;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getUpperAddrName() + " " +
                getMiddleAddrName() + " " +
                getLowerAddrName() + " " +
                getName();
    }
}
