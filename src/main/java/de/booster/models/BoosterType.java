package de.booster.models;

public enum BoosterType {
    BREAK,
    DROP,
    FLY,
    MOB,
    XP;

    public static BoosterType fromString(String str) {
        try {
            return valueOf(str.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

