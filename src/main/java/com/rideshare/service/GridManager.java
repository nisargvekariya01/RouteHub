package com.rideshare.service;

import com.rideshare.model.Driver;
import com.rideshare.model.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Manages spatial indexing of drivers using a grid system.
 * Allows quick O(1) cell lookup to find nearby drivers instead of scanning the whole city.
 */
public class GridManager {
    // Defines a ~1km grid cell size approximately
    private static final double GRID_CELL_SIZE = 0.01;
    
    // Thread-safe map of cell ID -> List of Drivers
    private final Map<String, List<Driver>> grid = new ConcurrentHashMap<>();

    private String getCellId(Location location) {
        int x = (int) (location.getLatitude() / GRID_CELL_SIZE);
        int y = (int) (location.getLongitude() / GRID_CELL_SIZE);
        return x + "," + y;
    }
    
    private List<String> getNeighboringCellIds(Location location) {
        int x = (int) (location.getLatitude() / GRID_CELL_SIZE);
        int y = (int) (location.getLongitude() / GRID_CELL_SIZE);
        List<String> neighbors = new ArrayList<>();
        // 3x3 grid around the location
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                neighbors.add((x + i) + "," + (y + j));
            }
        }
        return neighbors;
    }

    public void addDriver(Driver driver) {
        String cellId = getCellId(driver.getCurrentLocation());
        grid.computeIfAbsent(cellId, k -> new CopyOnWriteArrayList<>()).add(driver);
    }
    
    public void updateDriverLocation(Driver driver, Location oldLocation, Location newLocation) {
        String oldCell = getCellId(oldLocation);
        String newCell = getCellId(newLocation);
        if (!oldCell.equals(newCell)) {
            List<Driver> oldList = grid.get(oldCell);
            if (oldList != null) {
                oldList.remove(driver);
            }
            grid.computeIfAbsent(newCell, k -> new CopyOnWriteArrayList<>()).add(driver);
        }
        driver.setCurrentLocation(newLocation);
    }

    public List<Driver> getNearbyDrivers(Location location) {
        List<Driver> candidates = new ArrayList<>();
        for (String cellId : getNeighboringCellIds(location)) {
            List<Driver> driversInCell = grid.get(cellId);
            if (driversInCell != null) {
                candidates.addAll(driversInCell);
            }
        }
        return candidates;
    }
    
    public void clear() {
        grid.clear();
    }
}
