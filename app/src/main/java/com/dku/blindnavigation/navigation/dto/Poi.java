package com.dku.blindnavigation.navigation.dto;

import android.os.Parcel;
import android.os.Parcelable;

public class Poi implements Parcelable {
    private String name;
    private String upperAddrName;
    private String middleAddrName;
    private String lowerAddrName;
    private double frontLon;
    private double frontLat;

    public Poi() {
    }

    public Poi(double frontLat, double frontLon) {
        this.frontLat = frontLat;
        this.frontLon = frontLon;
    }

    public Poi(String name, double frontLat, double frontLon) {
        this.name = name;
        this.frontLat = frontLat;
        this.frontLon = frontLon;
    }

    public Poi(String name, String upperAddrName, String middleAddrName, String lowerAddrName, double frontLon, double frontLat) {
        this.name = name;
        this.upperAddrName = upperAddrName;
        this.middleAddrName = middleAddrName;
        this.lowerAddrName = lowerAddrName;
        this.frontLon = frontLon;
        this.frontLat = frontLat;
    }

    protected Poi(Parcel in) {
        name = in.readString();
        upperAddrName = in.readString();
        middleAddrName = in.readString();
        lowerAddrName = in.readString();
        frontLon = in.readDouble();
        frontLat = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(upperAddrName);
        dest.writeString(middleAddrName);
        dest.writeString(lowerAddrName);
        dest.writeDouble(frontLon);
        dest.writeDouble(frontLat);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Poi> CREATOR = new Creator<Poi>() {
        @Override
        public Poi createFromParcel(Parcel in) {
            return new Poi(in);
        }

        @Override
        public Poi[] newArray(int size) {
            return new Poi[size];
        }
    };

    public String getName() {
        return name;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Poi poi = (Poi) o;

        if (Double.compare(poi.frontLon, frontLon) != 0) return false;
        return Double.compare(poi.frontLat, frontLat) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(frontLon);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(frontLat);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
