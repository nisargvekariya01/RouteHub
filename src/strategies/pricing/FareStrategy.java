package strategies.pricing;

import models.Ride;

/**
 * Strategy interface for calculating ride fares.
 * 
 * EXPLAINING OPEN/CLOSED PRINCIPLE (OCP):
 * The Open/Closed Principle states that software entities (classes, modules, functions, etc.) 
 * should be open for extension, but closed for modification.
 * 
 * By defining the `FareStrategy` interface, we can add as many new pricing models as we want 
 * (e.g., StandardFareStrategy, LuxuryFareStrategy, HolidayFareStrategy) by creating new classes 
 * that implement this interface. We extend the system's behavior without ever needing to modify 
 * the existing `RideService` code. This eliminates the need for massive, fragile if/else blocks 
 * inside the service layer.
 */
public interface FareStrategy {
    double calculateFare(Ride ride, boolean isPeakHour);
}
