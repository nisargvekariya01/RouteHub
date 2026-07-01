package models;

/**
 * Model representing a ride booking.
 * Exists to serve as the core transactional entity of the engine, 
 * linking a Passenger, a Driver, Locations, and maintaining the ride's state.
 */
public class Ride {
    private final String id;
    private final Passenger passenger;
    private Driver driver; 
    private final Location pickupLocation;
    private final Location dropoffLocation;
    private RideStatus status;
    private double fare;

    public Ride(String id, Passenger passenger, Location pickupLocation, Location dropoffLocation) {
        this.id = id;
        this.passenger = passenger;
        this.pickupLocation = pickupLocation;
        this.dropoffLocation = dropoffLocation;
        this.status = RideStatus.REQUESTED;
        this.fare = 0.0;
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

    /**
     * Meaningful method to assign a driver when the ride is accepted.
     */
    public void acceptRide(Driver driver) {
        if (this.status == RideStatus.REQUESTED) {
            this.driver = driver;
            this.status = RideStatus.ACCEPTED;
        }
    }

    /**
     * Meaningful method to transition the ride to in-progress.
     */
    public void startRide() {
        if (this.status == RideStatus.ACCEPTED) {
            this.status = RideStatus.IN_PROGRESS;
        }
    }

    /**
     * Meaningful method to finalize the ride and record the fare.
     */
    public void completeRide(double finalFare) {
        if (this.status == RideStatus.IN_PROGRESS) {
            this.status = RideStatus.COMPLETED;
            this.fare = finalFare;
        }
    }

    /**
     * Meaningful method to handle cancellations safely.
     */
    public void cancelRide() {
        if (this.status == RideStatus.REQUESTED || this.status == RideStatus.ACCEPTED) {
            this.status = RideStatus.CANCELLED;
        }
    }
}
