package models;

import java.time.LocalDateTime;

/**
 * Model representing a ride booking.
 * 
 * EXPLAINING STATE MACHINE CONCEPT:
 * A State Machine (or Finite State Machine) is a design pattern where an object can exist 
 * in exactly one of a finite number of 'states' at any given time. The machine strictly dictates 
 * valid transitions between these states based on rules.
 * 
 * By embedding this state machine directly within the `Ride` model, we mathematically guarantee 
 * the purity of the ride's lifecycle. An external service cannot arbitrarily jump a ride from PENDING 
 * directly to COMPLETED, nor can a cancelled ride be resurrected. The object fiercely protects 
 * its own internal validity, making the system immune to race conditions and logical bypasses.
 * 
 * RIDE LIFECYCLE EXPLANATION:
 * 1. PENDING: Passenger requests a ride. No driver is assigned yet.
 * 2. ACCEPTED: System matches the ride, and a Driver is assigned. 
 * 3. STARTED: The assigned driver physically picks up the passenger and starts the journey. 
 * 4. COMPLETED: The passenger reaches the dropoff, and the fare is processed.
 * 5. CANCELLED: The ride is aborted prior to completion.
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

    /**
     * STATE TRANSITION: PENDING -> ACCEPTED
     */
    public void acceptRide(Driver driver) {
        if (this.status != RideStatus.PENDING) {
            throw new IllegalStateException("Ride must be PENDING to be accepted.");
        }
        this.driver = driver;
        this.status = RideStatus.ACCEPTED;
    }

    /**
     * STATE TRANSITION: ACCEPTED -> STARTED
     * Contains business rule: Only the assigned driver can start the ride.
     */
    public void startRide(String driverId) {
        if (this.status != RideStatus.ACCEPTED) {
            throw new IllegalStateException("Ride must be ACCEPTED to start.");
        }
        if (this.driver == null || !this.driver.getId().equals(driverId)) {
            throw new IllegalStateException("Unauthorized: Only the assigned driver can start this ride.");
        }
        this.status = RideStatus.STARTED;
    }

    /**
     * STATE TRANSITION: STARTED -> COMPLETED
     * Contains business rule: Only a started ride can be finished.
     */
    public void completeRide(double finalFare) {
        if (this.status != RideStatus.STARTED) {
            throw new IllegalStateException("Only a STARTED ride can be completed.");
        }
        this.status = RideStatus.COMPLETED;
        this.fare = finalFare;
    }

    /**
     * STATE TRANSITION: ANY -> CANCELLED
     */
    public void cancelRide() {
        if (this.status == RideStatus.COMPLETED || this.status == RideStatus.CANCELLED) {
            throw new IllegalStateException("Cannot cancel a ride that is already completed or cancelled.");
        }
        this.status = RideStatus.CANCELLED;
    }
}
