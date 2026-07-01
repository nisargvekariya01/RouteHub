package models;

import exceptions.InvalidRideStateException;
import java.time.LocalDateTime;

/**
 * Model representing a ride booking.
 * 
 * EXPLAINING STATE MACHINE CONCEPT:
 * A State Machine (or Finite State Machine) is a design pattern where an object can exist 
 * in exactly one of a finite number of 'states' at any given time. The machine strictly dictates 
 * valid transitions between these states based on rules.
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

    public Ride(String id, Passenger passenger, Location pickupLocation, Location dropoffLocation) {
        this.id = id;
        this.passenger = passenger;
        this.pickupLocation = pickupLocation;
        this.dropoffLocation = dropoffLocation;
        this.status = RideStatus.PENDING;
        this.fare = 0.0;
        this.time = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public Driver getDriver() {
        return driver;
    }

    public Location getPickupLocation() {
        return pickupLocation;
    }

    public Location getDropoffLocation() {
        return dropoffLocation;
    }

    public RideStatus getStatus() {
        return status;
    }

    public double getFare() {
        return fare;
    }

    public LocalDateTime getTime() {
        return time;
    }

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
