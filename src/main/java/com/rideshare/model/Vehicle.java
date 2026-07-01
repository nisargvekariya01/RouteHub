package com.rideshare.model;

/**
 * Abstract base class for all vehicle types.
 */
public abstract class Vehicle {
    private final String id;
    private final int capacity;
    private final double baseFareMultiplier;

    protected Vehicle(String id, int capacity, double baseFareMultiplier) {
        this.id = id;
        this.capacity = capacity;
        this.baseFareMultiplier = baseFareMultiplier;
    }

    public String getId() { return id; }
    public int getCapacity() { return capacity; }
    public double getBaseFareMultiplier() { return baseFareMultiplier; }
    
    public abstract String getType();

    @Override
    public String toString() {
        return getType() + "-" + id;
    }
}
