package de.booster.models;

import java.util.UUID;

public class PlayerBooster {
    
    private final UUID playerUUID;
    private final BoosterType type;
    private final int amount;
    private final long endTime;

    public PlayerBooster(UUID playerUUID, BoosterType type, int amount, long endTime) {
        this.playerUUID = playerUUID;
        this.type = type;
        this.amount = amount;
        this.endTime = endTime;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public BoosterType getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }

    public long getEndTime() {
        return endTime;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > endTime;
    }
}

