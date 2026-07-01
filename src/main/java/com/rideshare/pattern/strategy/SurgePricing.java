package com.rideshare.pattern.strategy;

import com.rideshare.model.Trip;

public class SurgePricing implements PricingStrategy {
    private final double surgeMultiplier;
    private final BaseFarePricing baseFarePricing = new BaseFarePricing();

    public SurgePricing(double surgeMultiplier) {
        this.surgeMultiplier = surgeMultiplier;
    }

    @Override
    public double calculateFare(Trip trip) {
        return baseFarePricing.calculateFare(trip) * surgeMultiplier;
    }
}
