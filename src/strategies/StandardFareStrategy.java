package strategies;

import models.Ride;
import models.VehicleType;
import utils.DistanceCalculator;

/**
 * Standard implementation of the FareStrategy.
 */
public class StandardFareStrategy implements FareStrategy {

    private static final double BASE_FARE = 5.0;
    private static final double PER_KM_RATE = 2.0;
    private static final double PEAK_MULTIPLIER = 1.5;

    @Override
    public double calculateFare(Ride ride, boolean isPeakHour) {
        // 1. Distance charge
        double distance = DistanceCalculator.calculateDistance(ride.getPickupLocation(), ride.getDropoffLocation());
        
        // 2. Vehicle multiplier
        double vehicleMultiplier = getVehicleMultiplier(ride.getDriver().getVehicle().getType());
        
        // 3. Peak multiplier
        double peakMultiplier = isPeakHour ? PEAK_MULTIPLIER : 1.0;

        // Final formula
        return (BASE_FARE + (distance * PER_KM_RATE)) * vehicleMultiplier * peakMultiplier;
    }

    private double getVehicleMultiplier(VehicleType type) {
        switch (type) {
            case PREMIUM: return 1.5;
            case SUV: return 2.0;
            case ECONOMY: 
            default: return 1.0;
        }
    }
}
