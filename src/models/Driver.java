package models;

/**
 * Model representing a driver in the system.
 * Exists to encapsulate driver-specific properties, such as their assigned vehicle, 
 * current availability status, and rating.
 */
public class Driver extends User {
    private final Vehicle vehicle;
    private DriverStatus status;
    private double rating;

    public Driver(String id, String name, String phoneNumber, Vehicle vehicle) {
        super(id, name, phoneNumber);
        this.vehicle = vehicle;
        this.status = DriverStatus.OFFLINE;
        this.rating = 5.0; // Default rating
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public DriverStatus getStatus() {
        return status;
    }

    public double getRating() {
        return rating;
    }

    public void setStatus(DriverStatus status) {
        this.status = status;
    }

    /**
     * Meaningful method to securely update the driver's rating.
     */
    public void updateRating(double newRating) {
        if (newRating >= 1.0 && newRating <= 5.0) {
            this.rating = newRating;
        }
    }

    /**
     * Meaningful method to easily check if driver can take a ride.
     */
    public boolean isAvailable() {
        return this.status == DriverStatus.AVAILABLE;
    }
}
