package com.rideshare.pattern.strategy;

import com.rideshare.model.Trip;

public class NightPricing implements PricingStrategy {
    private static final double NIGHT_MULTIPLIER = 1.25;
    private final BaseFarePricing baseFarePricing = new BaseFarePricing();

    @Override
    public double calculateFare(Trip trip) {
        return baseFarePricing.calculateFare(trip) * NIGHT_MULTIPLIER;
    }
}
