package com.rideshare.model;

/**
 * Represents a geographical location using latitude and longitude.
 * Provides a method to calculate the great-circle distance between two locations.
 */
public class Location {
    private final double latitude;
    private final double longitude;

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    /**
     * Calculates the distance to another location using the Haversine formula.
     * @param other The other location
     * @return Distance in kilometers
     */
    public double distanceTo(Location other) {
        final int R = 6371; // Radius of the earth in km
        double latDistance = Math.toRadians(other.latitude - this.latitude);
        double lonDistance = Math.toRadians(other.longitude - this.longitude);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(this.latitude)) * Math.cos(Math.toRadians(other.latitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // distance in km
    }

    @Override
    public String toString() {
        return String.format("(%.4f, %.4f)", latitude, longitude);
    }
}
