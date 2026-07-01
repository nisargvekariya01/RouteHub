package com.rideshare.pattern.strategy;

import com.rideshare.model.Trip;

/**
 * Strategy Pattern Interface for fare calculation.
 * 
 * WHY STRATEGY PATTERN: The algorithm for calculating fare can change depending 
 * on conditions (Surge, Night time, Standard). Instead of writing complex conditional 
 * blocks inside the Trip or Dispatcher class, we inject the right pricing algorithm at runtime.
 * This conforms to the Open/Closed Principle.
 */
public interface PricingStrategy {
    double calculateFare(Trip trip);
}
