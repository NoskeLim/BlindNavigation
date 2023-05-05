package com.dku.blindnavigation.navigation.direction;


import com.dku.blindnavigation.navigation.location.dto.Poi;

public class DirectionCalculator {
    public static DirectionType getNextDirection(Poi beforeLoc, Poi curLoc, Poi nextLoc) {
        double[] beforeVector = new double[] {
                curLoc.getFrontLon() - beforeLoc.getFrontLon(),
                curLoc.getFrontLat() - beforeLoc.getFrontLat()
        };

        double[] nextVector = new double[] {
                nextLoc.getFrontLon() - curLoc.getFrontLon(),
                nextLoc.getFrontLat() - curLoc.getFrontLat()
        };

        return DirectionType.getDirectionType(getDegreeBetweenVector(beforeVector, nextVector));
    }

    public static DirectionType getFirstDirection(Poi curLoc, Poi nextLoc, double phoneDegree) {
        double[] beforeVector = new double[] {90 - curLoc.getFrontLon(), 0};
        double[] nextVector = new double[] {
                nextLoc.getFrontLon() - curLoc.getFrontLon(),
                nextLoc.getFrontLat() - curLoc.getFrontLat()
        };
        double vectorBetweenDegree = getDegreeBetweenVector(beforeVector, nextVector);
        return DirectionType.getDirectionType((360 - phoneDegree + vectorBetweenDegree) % 360);
    }

    private static double getDegreeBetweenVector(double[] vector1, double[] vector2) {
        double radian = Math.atan2(vector1[0] * vector2[1] - vector2[0] * vector1[1],
                vector1[0] * vector2[0] + vector1[1] * vector2[1]);
        return ((radian * 180 / Math.PI) + 360) % 360;
    }
}
