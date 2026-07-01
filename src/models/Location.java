package models;

/**
 * Model representing a geographical coordinate.
 * Exists to group latitude and longitude, providing a single domain object
 * to represent points on a map for pickups and drop-offs.
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
}
