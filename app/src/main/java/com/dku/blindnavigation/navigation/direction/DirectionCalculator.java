package com.dku.blindnavigation.navigation.direction;

import com.dku.blindnavigation.navigation.dto.Poi;

public class DirectionCalculator {
    public static DirectionType getNextDirection(Poi beforeLoc, Poi curLoc, Poi nextLoc) {
        double[] beforeVector = getNormalizeVector(
                curLoc.getFrontLon() - beforeLoc.getFrontLon(), curLoc.getFrontLat() - beforeLoc.getFrontLat());


        double[] nextVector = getNormalizeVector(
                nextLoc.getFrontLon() - curLoc.getFrontLon(), nextLoc.getFrontLat() - curLoc.getFrontLat());

        return DirectionType.getDirectionType(getDegreeBetweenVector(nextVector, beforeVector));
    }

    public static DirectionType getFirstDirection(Poi curLoc, Poi nextLoc, double phoneDegree) {
        double[] beforeVector = new double[] {0, 1};
        double[] nextVector = getNormalizeVector(
                nextLoc.getFrontLon() - curLoc.getFrontLon(), nextLoc.getFrontLat() - curLoc.getFrontLat());
        double vectorBetweenDegree = getDegreeBetweenVector(nextVector, beforeVector);
        return DirectionType.getDirectionType((360 - phoneDegree + vectorBetweenDegree) % 360);
    }

    private static double getDegreeBetweenVector(double[] vector1, double[] vector2) {
        double radian = Math.atan2(vector1[0] * vector2[1] - vector2[0] * vector1[1],
                vector1[0] * vector2[0] + vector1[1] * vector2[1]);
        return ((radian * 180 / Math.PI) + 360) % 360;
    }

    private static double[] getNormalizeVector(double x, double y) {
        double vectorSize = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        return new double[] {x / vectorSize, y / vectorSize};
    }
}
