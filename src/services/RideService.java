package services;

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
 *   We can introduce 'SurgePricing' or 'VIPMatching' without altering this file.
 * - Dependency Inversion Principle (DIP): Relies entirely on strategy interfaces 
 *   and abstract repository concepts.
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
     * Core business logic to request a new ride.
     * Coordinates creating the model, interacting with strategies (future step), 
     * and saving state to the repository.
     */
    public Ride requestRide(Passenger passenger, Location pickup, Location dropoff) {
        if (passenger == null || pickup == null || dropoff == null) {
            throw new IllegalArgumentException("Invalid ride request parameters.");
        }

        String rideId = UUID.randomUUID().toString();
        Ride ride = new Ride(rideId, passenger, pickup, dropoff);
        
        // Future Integration:
        // double estimatedFare = pricingStrategy.calculateFare(ride);
        // Driver driver = matchingStrategy.findMatch(passenger, pickup);
        
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
