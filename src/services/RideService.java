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
     * This method initializes the ride in a PENDING state.
     * As per requirements, drivers are NOT assigned at this stage. The ride 
     * simply enters the system waiting to be matched/accepted later.
     * 
     * @param passenger The user requesting the ride
     * @param pickup The starting location
     * @param dropoff The destination location
     * @return The newly created PENDING Ride
     */
    public Ride requestRide(Passenger passenger, Location pickup, Location dropoff) {
        if (passenger == null || pickup == null || dropoff == null) {
            throw new IllegalArgumentException("Invalid ride request parameters.");
        }

        String rideId = UUID.randomUUID().toString();
        Ride ride = new Ride(rideId, passenger, pickup, dropoff);
        
        // Deliberately NOT assigning a driver here.
        // It remains PENDING in the repository until a matching engine picks it up.
        
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
