package utils;

import models.Location;
import models.User;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for spatial calculations.
 * Responsibility: Provides helper methods, such as calculating the distance between two Locations.
 * Extracted here to keep business services focused and clean without relying on Google Maps API.
 */
public class DistanceCalculator {

    /**
     * Calculates the Euclidean distance between two locations.
     * 
     * FORMULA EXPLANATION:
     * Euclidean Distance is the "ordinary" straight-line distance between two points in Euclidean space.
     * Formula: d = sqrt((x2 - x1)^2 + (y2 - y1)^2)
     * Here, x is latitude and y is longitude. 
     * Note: This is a simplified 2D Cartesian plane calculation and ignores the Earth's curvature.
     * 
     * @param loc1 First location
     * @param loc2 Second location
     * @return Distance in abstract units
     */
    public static double calculateDistance(Location loc1, Location loc2) {
        if (loc1 == null || loc2 == null) {
            throw new IllegalArgumentException("Locations cannot be null");
        }
        
        double latDiff = loc2.getLatitude() - loc1.getLatitude();
        double lonDiff = loc2.getLongitude() - loc1.getLongitude();
        
        return Math.sqrt(Math.pow(latDiff, 2) + Math.pow(lonDiff, 2));
    }

    /**
     * Location comparison to check if two locations are exceptionally close.
     * 
     * FORMULA EXPLANATION:
     * This simply delegates to calculateDistance and checks if the distance is below a small threshold (tolerance).
     * This handles floating-point inaccuracies better than exact mathematical equality (==).
     * 
     * @param loc1 First location
     * @param loc2 Second location
     * @param tolerance The maximum distance to be considered "equal"
     * @return True if distance <= tolerance
     */
    public static boolean isSameLocation(Location loc1, Location loc2, double tolerance) {
        return calculateDistance(loc1, loc2) <= tolerance;
    }

    /**
     * Nearby search to filter a list of locations to only those within a certain radius.
     * 
     * FORMULA EXPLANATION:
     * We iterate through the list and apply the Euclidean distance formula to each item relative 
     * to the center point. If the calculated distance is less than or equal to the radius, it is included.
     * 
     * @param center The central reference location
     * @param candidates List of locations to check
     * @param radius Maximum allowable distance
     * @return List of locations within the radius
     */
    public static List<Location> findNearbyLocations(Location center, List<Location> candidates, double radius) {
        if (center == null || candidates == null) {
            throw new IllegalArgumentException("Center and candidates cannot be null");
        }
        
        return candidates.stream()
                .filter(candidate -> calculateDistance(center, candidate) <= radius)
                .collect(Collectors.toList());
    }

    /**
     * Nearby search specifically for Users (Drivers or Passengers).
     * 
     * FORMULA EXPLANATION:
     * Similar to the location search, we extract the user's location, apply the Euclidean formula 
     * against the center point, and check if it falls within the given radius threshold.
     * 
     * @param center The central reference location
     * @param users List of users (Drivers or Passengers) to check
     * @param radius Maximum allowable distance
     * @param <T> Type of User
     * @return List of users within the radius
     */
    public static <T extends User> List<T> findNearbyUsers(Location center, List<T> users, double radius) {
        if (center == null || users == null) {
            throw new IllegalArgumentException("Center and users cannot be null");
        }
        
        return users.stream()
                .filter(user -> user.getCurrentLocation() != null)
                .filter(user -> calculateDistance(center, user.getCurrentLocation()) <= radius)
                .collect(Collectors.toList());
    }
}
