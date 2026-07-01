package models;

/**
 * Model representing a driver in the system.
 * 
 * WHY STATE MANAGEMENT IS IMPORTANT:
 * State management guarantees that an entity cannot perform invalid actions based on its current context.
 * For example, a driver who is already 'ON_TRIP' cannot accept a new ride or suddenly go 'OFFLINE'.
 * Enforcing these rules strictly inside the domain model using Enums prevents race conditions, 
 * logical errors, and double-booking, ensuring the entire system remains in a valid and consistent state.
 */
public class Driver extends User {
    private Vehicle vehicle;
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

    public void setVehicle(Vehicle vehicle) {
        if (vehicle != null) {
            this.vehicle = vehicle;
        }
    }

    public void updateRating(double newRating) {
        if (newRating >= 1.0 && newRating <= 5.0) {
            this.rating = newRating;
        }
    }

    public boolean isAvailable() {
        return this.status == DriverStatus.ONLINE;
    }

    // --- STATE MANAGEMENT ---

    public void goOnline() {
        if (this.status == DriverStatus.OFFLINE) {
            this.status = DriverStatus.ONLINE;
        } else {
            throw new IllegalStateException("Driver can only go online from an offline state.");
        }
    }

    public void goOffline() {
        if (this.status == DriverStatus.ONLINE) {
            this.status = DriverStatus.OFFLINE;
        } else if (this.status == DriverStatus.ON_TRIP) {
            throw new IllegalStateException("Driver cannot go offline while on a trip.");
        }
    }

    public void startRide() {
        if (this.status == DriverStatus.ONLINE) {
            this.status = DriverStatus.ON_TRIP;
        } else {
            throw new IllegalStateException("Driver must be online and not on a trip to start a ride.");
        }
    }

    public void finishRide() {
        if (this.status == DriverStatus.ON_TRIP) {
            this.status = DriverStatus.ONLINE; // Transition back to online after dropping off passenger
        } else {
            throw new IllegalStateException("Driver must be on a trip to finish it.");
        }
    }
}
