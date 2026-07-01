package com.rideshare.pattern.strategy;

import com.rideshare.model.Trip;

public class BaseFarePricing implements PricingStrategy {
    private static final double RATE_PER_KM = 2.0;

    @Override
    public double calculateFare(Trip trip) {
        double distance = trip.getSource().distanceTo(trip.getDestination());
        double vehicleMultiplier = trip.getDriver() != null ? trip.getDriver().getVehicle().getBaseFareMultiplier() : 1.0;
        return distance * RATE_PER_KM * vehicleMultiplier;
    }
}
