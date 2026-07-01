package services;

import models.Driver;
import models.Location;
import models.Passenger;
import models.Ride;
import repositories.RideRepository;
import strategies.DriverMatchingStrategy;
import strategies.FareStrategy;

import java.util.List;
import java.util.UUID;

/**
 * Service for orchestrating ride-related business operations.
 * 
 * EXPLAINING DEPENDENCY INVERSION PRINCIPLE (DIP):
 * DIP states that high-level modules (like RideService) should not depend on low-level modules.
 * Instead, both should depend on abstractions.
 * 
 * Here, RideService depends exclusively on the `DriverMatchingStrategy` and `FareStrategy` abstractions.
 * It knows *what* to do (find a match, calculate a fare) but has no idea *how* it's done.
 * This makes the system incredibly resilient: we can inject any implementation without breaking the service layer.
 */
public class RideService {
    private final RideRepository rideRepository;
    
    private final DriverMatchingStrategy driverMatchingStrategy;
    private final FareStrategy fareStrategy;

    public RideService(RideRepository rideRepository, DriverMatchingStrategy driverMatchingStrategy, FareStrategy fareStrategy) {
        this.rideRepository = rideRepository;
        this.driverMatchingStrategy = driverMatchingStrategy;
        this.fareStrategy = fareStrategy;
    }

    public Ride requestRide(Passenger passenger, Location pickup, Location dropoff) {
        if (passenger == null || pickup == null || dropoff == null) {
            throw new IllegalArgumentException("Invalid ride request parameters.");
        }

        String rideId = UUID.randomUUID().toString();
        Ride ride = new Ride(rideId, passenger, pickup, dropoff);
        
        Driver matchedDriver = driverMatchingStrategy.findMatch(passenger, pickup);
        ride.acceptRide(matchedDriver);
        
        // This transitions the driver to ON_TRIP, making them unavailable for other matches
        matchedDriver.startRide(); 
        
        rideRepository.save(ride);
        return ride;
    }

    /**
     * Authorizes and transitions a ride to the STARTED state.
     * The Ride's internal state machine verifies that the provided driver ID matches the assigned driver.
     */
    public Ride startRide(String rideId, String driverId) {
        Ride ride = rideRepository.findById(rideId)
            .orElseThrow(() -> new IllegalArgumentException("Ride not found."));
            
        ride.startRide(driverId); // State machine handles authorization and transition
        
        rideRepository.update(ride);
        return ride;
    }

    /**
     * Completes the ride, calculates the final fare using the injected FareStrategy,
     * and correctly resets the driver's availability state.
     */
    public Ride completeRide(String rideId, boolean isPeakHour) {
        Ride ride = rideRepository.findById(rideId)
            .orElseThrow(() -> new IllegalArgumentException("Ride not found."));
            
        double finalFare = fareStrategy.calculateFare(ride, isPeakHour);
        
        // This validates that the ride is STARTED and updates it to COMPLETED
        ride.completeRide(finalFare);
        
        // This transitions the driver from ON_TRIP back to ONLINE
        ride.getDriver().finishRide();
        
        rideRepository.update(ride);
        return ride;
    }

    public List<Ride> viewAllRides() {
        return rideRepository.findAll();
    }
}
