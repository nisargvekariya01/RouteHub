package utils;

import models.Driver;
import models.Location;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Spatial Indexing system that divides the map into discrete mathematical buckets (sectors).
 * Allows for O(1) location tracking and localized driver searching, bypassing O(N) global scans.
 * Uses a grid-based approach rather than strict radius mathematical filters.
 */
public class SpatialGrid {

    // Singleton Instance
    private static SpatialGrid instance;

    // A sector is defined by roughly 2km x 2km blocks (0.02 degrees of Latitude/Longitude)
    private static final double SECTOR_SIZE_DEG = 0.02;

    // Sector ID (e.g. "401_745") -> Set of Drivers currently in that sector
    private final Map<String, Set<Driver>> grid = new ConcurrentHashMap<>();

    // Keep track of where a driver currently is, so we can remove them from the old sector quickly
    private final Map<String, String> driverSectorMap = new ConcurrentHashMap<>();

    private SpatialGrid() {}

    public static synchronized SpatialGrid getInstance() {
        if (instance == null) {
            instance = new SpatialGrid();
        }
        return instance;
    }

    /**
     * Converts a raw GPS location into a discrete Sector ID.
     */
    private String getSectorId(double lat, double lon) {
        int latBucket = (int) Math.floor(lat / SECTOR_SIZE_DEG);
        int lonBucket = (int) Math.floor(lon / SECTOR_SIZE_DEG);
        return latBucket + "_" + lonBucket;
    }

    /**
     * Updates the driver's position in the spatial index in O(1) time.
     * Moves them from their old bucket to the new bucket if they crossed a sector boundary.
     */
    public void updateDriverLocation(Driver driver, Location newLocation) {
        if (driver == null || newLocation == null) return;

        String newSectorId = getSectorId(newLocation.getLatitude(), newLocation.getLongitude());
        String oldSectorId = driverSectorMap.get(driver.getId());

        // If the driver moved to a new bucket (or is a brand new driver)
        if (oldSectorId == null || !oldSectorId.equals(newSectorId)) {
            // Remove from old bucket
            if (oldSectorId != null && grid.containsKey(oldSectorId)) {
                grid.get(oldSectorId).remove(driver);
            }

            // Add to new bucket
            grid.putIfAbsent(newSectorId, ConcurrentHashMap.newKeySet());
            grid.get(newSectorId).add(driver);
            driverSectorMap.put(driver.getId(), newSectorId);
        }

        // Actually update the domain model's coordinates
        driver.setCurrentLocation(newLocation);
    }

    /**
     * Fetches all drivers in the exact grid bucket of the target, AND the 8 immediately 
     * adjacent buckets (3x3 grid search). This completely eliminates the need to calculate 
     * mathematical radius distances and guarantees we only evaluate local drivers.
     */
    public List<Driver> getDriversInAdjacentSectors(Location target) {
        List<Driver> nearbyDrivers = new ArrayList<>();
        
        int centerLatBucket = (int) Math.floor(target.getLatitude() / SECTOR_SIZE_DEG);
        int centerLonBucket = (int) Math.floor(target.getLongitude() / SECTOR_SIZE_DEG);

        // Scan the 3x3 grid centered on the target
        for (int dLat = -1; dLat <= 1; dLat++) {
            for (int dLon = -1; dLon <= 1; dLon++) {
                String sectorId = (centerLatBucket + dLat) + "_" + (centerLonBucket + dLon);
                if (grid.containsKey(sectorId)) {
                    nearbyDrivers.addAll(grid.get(sectorId));
                }
            }
        }
        
        return nearbyDrivers;
    }

    /**
     * Clear grid (useful for testing/resets)
     */
    public void clear() {
        grid.clear();
        driverSectorMap.clear();
    }
}
