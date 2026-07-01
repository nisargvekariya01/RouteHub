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
 * Concrete implementation of DriverMatchingStrategy finding the geographically nearest driver.
 */
public class NearestDriverStrategy implements DriverMatchingStrategy {

    private final DriverRepository driverRepository;

    public NearestDriverStrategy(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    @Override
    public Driver findMatch(Passenger passenger, Location pickupLocation) {
        List<Driver> allDrivers = driverRepository.findAll();

        List<Driver> onlineDrivers = allDrivers.stream()
                .filter(Driver::isAvailable)
                .filter(driver -> driver.getCurrentLocation() != null)
                .collect(Collectors.toList());

        if (onlineDrivers.isEmpty()) {
            throw new RideShareException("No drivers available.");
        }

        return Collections.min(onlineDrivers, new Comparator<Driver>() {
            @Override
            public int compare(Driver d1, Driver d2) {
                double dist1 = DistanceCalculator.calculateDistance(pickupLocation, d1.getCurrentLocation());
                double dist2 = DistanceCalculator.calculateDistance(pickupLocation, d2.getCurrentLocation());
                return Double.compare(dist1, dist2);
            }
        });
    }
}
