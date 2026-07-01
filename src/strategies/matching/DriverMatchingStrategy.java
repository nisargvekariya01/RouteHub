package strategies.matching;

import models.Driver;
import models.Location;
import models.Passenger;

/**
 * Strategy interface for matching riders with drivers.
 * 
 * This represents the Abstraction. Future strategies (e.g., HighestRatedDriverStrategy) 
 * can easily be added by simply creating a new class that implements this interface, 
 * without modifying the existing service code.
 */
public interface DriverMatchingStrategy {
    Driver findMatch(Passenger passenger, Location pickupLocation);
}
