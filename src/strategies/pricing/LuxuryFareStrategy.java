package strategies.pricing;

import models.Ride;
import models.enums.VehicleType;

/**
 * Luxury implementation of the FareStrategy with higher baseline rates.
 */
public class LuxuryFareStrategy implements FareStrategy {

    private static final double BASE_FARE = 20.0;
    private static final double PER_KM_RATE = 5.0;
    private static final double PEAK_MULTIPLIER = 2.0;

    @Override
    public double calculateFare(Ride ride, boolean isPeakHour) {
        // 1. Distance charge (using actual road distance from Ride object)
        double distance = ride.getDistance();
        
        // 2. Vehicle multiplier (Premium vehicles cost even more in luxury strategy)
        VehicleType vType = VehicleType.PREMIUM; // Default for luxury
        if (ride.getDriver() != null && ride.getDriver().getVehicle() != null) {
            vType = ride.getDriver().getVehicle().getType();
        }
        double vehicleMultiplier = getVehicleMultiplier(vType);
        
        // 3. Peak multiplier
        double peakMultiplier = isPeakHour ? PEAK_MULTIPLIER : 1.0;

        // Final formula
        return (BASE_FARE + (distance * PER_KM_RATE)) * vehicleMultiplier * peakMultiplier;
    }

    private double getVehicleMultiplier(VehicleType type) {
        switch (type) {
            case PREMIUM: return 2.0;
            case SUV: return 2.5;
            case ECONOMY: 
            default: return 1.0;
        }
    }
}
