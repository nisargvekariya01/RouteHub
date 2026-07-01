package models;

import java.time.LocalDateTime;

/**
 * Model representing a ride booking.
 * 
 * RIDE LIFECYCLE EXPLANATION:
 * 1. PENDING: The passenger requests a ride from a Pickup to a Dropoff location. 
 *             The ride object is created and persisted, but no driver is assigned yet.
 * 2. ACCEPTED: The system matches the ride, and a Driver accepts it. The Driver field is populated.
 * 3. IN_PROGRESS: The driver picks up the passenger and the journey begins.
 * 4. COMPLETED: The passenger reaches the dropoff location, and the final fare is processed.
 * 5. CANCELLED: Either the driver or passenger aborts the ride before it finishes.
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
     * Meaningful method to assign a driver when the ride is accepted.
     */
    public void acceptRide(Driver driver) {
        if (this.status == RideStatus.PENDING) {
            this.driver = driver;
            this.status = RideStatus.ACCEPTED;
        } else {
            throw new IllegalStateException("Ride must be PENDING to be accepted.");
        }
    }

    public void startRide() {
        if (this.status == RideStatus.ACCEPTED) {
            this.status = RideStatus.IN_PROGRESS;
        } else {
            throw new IllegalStateException("Ride must be ACCEPTED to start.");
        }
    }

    public void completeRide(double finalFare) {
        if (this.status == RideStatus.IN_PROGRESS) {
            this.status = RideStatus.COMPLETED;
            this.fare = finalFare;
        } else {
            throw new IllegalStateException("Ride must be IN_PROGRESS to complete.");
        }
    }

    public void cancelRide() {
        if (this.status == RideStatus.PENDING || this.status == RideStatus.ACCEPTED) {
            this.status = RideStatus.CANCELLED;
        } else {
            throw new IllegalStateException("Cannot cancel a ride that is in progress or completed.");
        }
    }
}
