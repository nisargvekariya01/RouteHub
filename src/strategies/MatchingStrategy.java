package strategies;

import models.Driver;
import models.Location;
import models.Passenger;

/**
 * Strategy interface for matching riders with drivers.
 * Responsibility: Defines a contract for driver matching algorithms.
 */
public interface MatchingStrategy {
    Driver findMatch(Passenger passenger, Location pickupLocation);
}
