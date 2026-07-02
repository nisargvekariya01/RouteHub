package strategies.matching;

import models.Driver;
import models.Ride;

/**
 * Strategy interface for matching a requested ride to the most appropriate driver.
 */
public interface DriverMatchingStrategy {
    Driver findMatchingDriver(Ride ride);
}
