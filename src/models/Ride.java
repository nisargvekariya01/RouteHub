package models;

import exceptions.InvalidRideStateException;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Model representing a ride booking.
 * 
 * BUILDER PATTERN:
 * The static `Builder` inner class avoids the "telescoping constructor" anti-pattern. 
 * Since a Ride requires multiple distinct parameters to be initialized securely (ID, Passenger, 
 * Pickup, Dropoff), the Builder provides a highly readable, fluent API for object creation. 
 * It perfectly ensures the object is fully validated and constructed before it's used.
 * 
 * STATE MACHINE CONCEPT: 
 * (Previously explained: mathematically guarantees valid lifecycle transitions).
 */
public class Ride {
    private final String id;
    private final Passenger passenger;
    private Driver driver; 
    private final Location pickupLocation;
    private final Location dropoffLocation;
    private RideStatus status;
    private double fare;
    private final LocalDateTime time;

    // Made private to enforce Builder pattern usage
    private Ride(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID().toString();
        this.passenger = builder.passenger;
        this.pickupLocation = builder.pickupLocation;
        this.dropoffLocation = builder.dropoffLocation;
        this.status = RideStatus.PENDING;
        this.fare = 0.0;
        this.time = LocalDateTime.now();
    }

    // Nested Builder Class
    public static class Builder {
        private String id;
        private Passenger passenger;
        private Location pickupLocation;
        private Location dropoffLocation;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder passenger(Passenger passenger) {
            this.passenger = passenger;
            return this;
        }

        public Builder pickupLocation(Location pickupLocation) {
            this.pickupLocation = pickupLocation;
            return this;
        }

        public Builder dropoffLocation(Location dropoffLocation) {
            this.dropoffLocation = dropoffLocation;
            return this;
        }

        public Ride build() {
            if (this.passenger == null || this.pickupLocation == null || this.dropoffLocation == null) {
                throw new IllegalArgumentException("Passenger, Pickup, and Dropoff locations are critically required.");
            }
            return new Ride(this);
        }
    }

    public String getId() { return id; }
    public Passenger getPassenger() { return passenger; }
    public Driver getDriver() { return driver; }
    public Location getPickupLocation() { return pickupLocation; }
    public Location getDropoffLocation() { return dropoffLocation; }
    public RideStatus getStatus() { return status; }
    public double getFare() { return fare; }
    public LocalDateTime getTime() { return time; }

    public void acceptRide(Driver driver) {
        if (this.status != RideStatus.PENDING) {
            throw new InvalidRideStateException("Ride must be PENDING to be accepted.");
        }
        this.driver = driver;
        this.status = RideStatus.ACCEPTED;
    }

    public void startRide(String driverId) {
        if (this.status != RideStatus.ACCEPTED) {
            throw new InvalidRideStateException("Ride must be ACCEPTED to start.");
        }
        if (this.driver == null || !this.driver.getId().equals(driverId)) {
            throw new InvalidRideStateException("Unauthorized: Only the assigned driver can start this ride.");
        }
        this.status = RideStatus.STARTED;
    }

    public void completeRide(double finalFare) {
        if (this.status != RideStatus.STARTED) {
            throw new InvalidRideStateException("Only a STARTED ride can be completed.");
        }
        this.status = RideStatus.COMPLETED;
        this.fare = finalFare;
    }

    public void cancelRide() {
        if (this.status == RideStatus.COMPLETED || this.status == RideStatus.CANCELLED) {
            throw new InvalidRideStateException("Cannot cancel a ride that is already completed or cancelled.");
        }
        this.status = RideStatus.CANCELLED;
    }
}
