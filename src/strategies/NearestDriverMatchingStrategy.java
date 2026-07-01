package strategies;

import exceptions.RideShareException;
import models.Driver;
import models.Location;
import models.Passenger;
import repositories.DriverRepository;
import utils.DistanceCalculator;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Concrete implementation of the MatchingStrategy that finds the geographically nearest driver.
 * 
 * WHY STRATEGY PATTERN IS USEFUL:
 * The Strategy pattern allows us to define a family of matching algorithms, encapsulate each one, 
 * and make them interchangeable. If we later want to implement a 'HighestRatedDriverStrategy' 
 * or a 'VIPDriverStrategy', we simply create a new class implementing this interface.
 * We can swap algorithms at runtime without modifying the RideService or core business logic, 
 * perfectly adhering to the Open/Closed Principle.
 */
public class NearestDriverMatchingStrategy implements MatchingStrategy {

    private final DriverRepository driverRepository;

    public NearestDriverMatchingStrategy(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    @Override
    public Driver findMatch(Passenger passenger, Location pickupLocation) {
        List<Driver> allDrivers = driverRepository.findAll();

        // 1. Filter only ONLINE drivers (isAvailable checks for ONLINE status, inherently ignoring OFFLINE and ON_TRIP)
        List<Driver> onlineDrivers = allDrivers.stream()
                .filter(Driver::isAvailable)
                .filter(driver -> driver.getCurrentLocation() != null) // Ensure location exists
                .collect(Collectors.toList());

        // 2. If none exists, throw exception with specific requested message
        if (onlineDrivers.isEmpty()) {
            throw new RideShareException("No drivers available.");
        }

        // 3. Find the nearest driver using Collections.min and a custom comparator
        Driver nearestDriver = Collections.min(onlineDrivers, new Comparator<Driver>() {
            @Override
            public int compare(Driver d1, Driver d2) {
                double dist1 = DistanceCalculator.calculateDistance(pickupLocation, d1.getCurrentLocation());
                double dist2 = DistanceCalculator.calculateDistance(pickupLocation, d2.getCurrentLocation());
                return Double.compare(dist1, dist2);
            }
        });

        return nearestDriver;
    }
}
