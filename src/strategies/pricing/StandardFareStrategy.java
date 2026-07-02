package strategies.pricing;

import models.Ride;
import models.enums.VehicleType;

/**
 * Standard implementation of the FareStrategy.
 */
public class StandardFareStrategy implements FareStrategy {

    private static final double BASE_FARE = 5.0;
    private static final double PER_SECOND_RATE = 0.05;
    private static final double PEAK_MULTIPLIER = 1.5;

    @Override
    public double calculateFare(Ride ride, boolean isPeakHour) {
        // 1. Time charge
        double travelTimeSeconds = ride.getTravelTimeSeconds();
        
        // 2. Vehicle multiplier (default to ECONOMY if driver not assigned yet)
        VehicleType vType = VehicleType.ECONOMY;
        if (ride.getDriver() != null && ride.getDriver().getVehicle() != null) {
            vType = ride.getDriver().getVehicle().getType();
        }
        double vehicleMultiplier = getVehicleMultiplier(vType);
        
        // 3. Peak multiplier
        double peakMultiplier = isPeakHour ? PEAK_MULTIPLIER : 1.0;

        // Final formula
        return (BASE_FARE + (travelTimeSeconds * PER_SECOND_RATE)) * vehicleMultiplier * peakMultiplier;
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
