package utils;

import models.Location;

/**
 * Utility class providing highly accurate real-world distance calculations
 * using the Haversine formula to account for the Earth's curvature.
 */
public class AccurateDistanceCalculator {

    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * Calculates the true geographical distance between two coordinates.
     * @param loc1 Starting location
     * @param loc2 Destination location
     * @return Distance in kilometers
     */
    public static double calculateDistance(Location loc1, Location loc2) {
        return calculateDistance(loc1.getLatitude(), loc1.getLongitude(), loc2.getLatitude(), loc2.getLongitude());
    }

    /**
     * Calculates the true geographical distance between two coordinate pairs.
     * @return Distance in kilometers
     */
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
                   
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }
}
