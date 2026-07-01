package services;

import models.Driver;
import models.Location;
import models.Passenger;
import models.Ride;
import repositories.RideRepository;
import strategies.MatchingStrategy;
import strategies.PricingStrategy;

import java.util.List;
import java.util.UUID;

/**
 * Service for orchestrating ride-related business operations.
 * 
 * SOLID Principles Applied:
 * - Open/Closed Principle (OCP): Algorithms for pricing and matching are injected. 
 * - Dependency Inversion Principle (DIP): Relies entirely on strategy interfaces and repository abstractions.
 */
public class RideService {
    private final RideRepository rideRepository;
    private final MatchingStrategy matchingStrategy;
    private final PricingStrategy pricingStrategy;

    public RideService(RideRepository rideRepository, MatchingStrategy matchingStrategy, PricingStrategy pricingStrategy) {
        this.rideRepository = rideRepository;
        this.matchingStrategy = matchingStrategy;
        this.pricingStrategy = pricingStrategy;
    }

    /**
     * Core business logic for a Passenger to request a new ride.
     * 
     * Now attempts to match and assign a driver using the provided MatchingStrategy.
     */
    public Ride requestRide(Passenger passenger, Location pickup, Location dropoff) {
        if (passenger == null || pickup == null || dropoff == null) {
            throw new IllegalArgumentException("Invalid ride request parameters.");
        }

        String rideId = UUID.randomUUID().toString();
        Ride ride = new Ride(rideId, passenger, pickup, dropoff);
        
        // Find the nearest driver using our strategy.
        // If no drivers are available, it will throw a RideShareException("No drivers available.")
        Driver matchedDriver = matchingStrategy.findMatch(passenger, pickup);
        
        // Assign nearest driver and mark the ride as ACCEPTED
        ride.acceptRide(matchedDriver);
        
        // Mark driver as ON_TRIP so they aren't matched again while servicing this ride
        matchedDriver.startRide(); 
        
        rideRepository.save(ride);
        return ride;
    }

    /**
     * Retrieves all ongoing and past rides.
     */
    public List<Ride> viewAllRides() {
        return rideRepository.findAll();
    }
}
