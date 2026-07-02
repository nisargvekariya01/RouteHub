package strategies.matching;

import exceptions.DriverNotFoundException;
import models.Driver;
import models.Location;
import models.Ride;
import models.enums.DriverStatus;
import repositories.DriverRepository;
import utils.SpatialGrid;
import java.util.List;

/**
 * Driver Matching Strategy that uses a real road network Navigation Strategy (Dijkstra)
 * to find the absolute closest online driver by driving distance.
 * 
 * DESIGN PATTERN (Strategy):
 * Encapsulates the specific algorithm for picking a driver.
 */
public class NearestDriverStrategy implements DriverMatchingStrategy {

    private final DriverRepository driverRepository;
    private final NavigationStrategy navigationStrategy;

    public NearestDriverStrategy(DriverRepository driverRepository, NavigationStrategy navigationStrategy) {
        this.driverRepository = driverRepository;
        this.navigationStrategy = navigationStrategy;
    }

    @Override
    public Driver findMatchingDriver(Ride ride) {
        Location pickupLocation = ride.getPickupLocation();
        
        Driver nearestDriver = null;
        double minTime = Double.MAX_VALUE;

        // NEW SPATIAL OPTIMIZATION: Fetch drivers ONLY from the local 3x3 grid sector!
        List<Driver> localDrivers = SpatialGrid.getInstance().getDriversInAdjacentSectors(pickupLocation);
        System.out.println("[Spatial Index] Found " + localDrivers.size() + " drivers in adjacent sectors to evaluate.");

        for (Driver driver : localDrivers) {
            if (driver.getStatus() == DriverStatus.ONLINE && driver.getCurrentLocation() != null) {
                // Use Dijkstra to calculate actual driving time
                double travelTime = navigationStrategy.getShortestPathTravelTime(driver.getCurrentLocation(), pickupLocation);
                
                if (travelTime != -1 && travelTime < minTime) {
                    minTime = travelTime;
                    nearestDriver = driver;
                }
            }
        }

        if (nearestDriver == null) {
            throw new DriverNotFoundException("No online drivers could reach the pickup location via valid roads.");
        }

        return nearestDriver;
    }
}
