package com.dku.blindnavigation.navigation.direction;

public enum DirectionType {
    UP, RIGHT_UP, RIGHT, RIGHT_DOWN, DOWN, LEFT_DOWN, LEFT, LEFT_UP;

    public static DirectionType getDirectionType(double degree) {
        if(degree >= 22.5 && degree <= 67.5) return RIGHT_UP;
        else if(degree > 67.5 && degree < 112.5) return RIGHT;
        else if(degree >= 112.5 && degree <= 157.5) return RIGHT_DOWN;
        else if(degree > 157.5 && degree < 202.5) return DOWN;
        else if(degree >= 202.5 && degree <= 247.5) return LEFT_DOWN;
        else if(degree > 247.5 && degree < 292.5) return LEFT;
        else if(degree >= 292.5 && degree < 337.5) return LEFT_UP;
        else return UP;
    }
}
