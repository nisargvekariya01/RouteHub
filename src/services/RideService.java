package services;

import models.Driver;
import models.Location;
import models.Passenger;
import models.Ride;
import repositories.RideRepository;
import strategies.DriverMatchingStrategy;
import strategies.PricingStrategy;

import java.util.List;
import java.util.UUID;

/**
 * Service for orchestrating ride-related business operations.
 * 
 * EXPLAINING DEPENDENCY INVERSION PRINCIPLE (DIP):
 * DIP states that high-level modules (like RideService) should not depend on low-level modules 
 * (like NearestDriverStrategy). Instead, both should depend on abstractions.
 * 
 * Here, RideService depends exclusively on the `DriverMatchingStrategy` abstraction interface.
 * It knows *what* to do (find a match) but has no idea *how* it's done (nearest distance, highest rating, etc.).
 * This makes the system incredibly resilient: we can inject any implementation without breaking the service layer.
 */
public class RideService {
    private final RideRepository rideRepository;
    
    // Depending purely on Abstraction, satisfying the Dependency Inversion Principle
    private final DriverMatchingStrategy driverMatchingStrategy;
    private final PricingStrategy pricingStrategy;

    public RideService(RideRepository rideRepository, DriverMatchingStrategy driverMatchingStrategy, PricingStrategy pricingStrategy) {
        this.rideRepository = rideRepository;
        this.driverMatchingStrategy = driverMatchingStrategy;
        this.pricingStrategy = pricingStrategy;
    }

    /**
     * Core business logic for a Passenger to request a new ride.
     */
    public Ride requestRide(Passenger passenger, Location pickup, Location dropoff) {
        if (passenger == null || pickup == null || dropoff == null) {
            throw new IllegalArgumentException("Invalid ride request parameters.");
        }

        String rideId = UUID.randomUUID().toString();
        Ride ride = new Ride(rideId, passenger, pickup, dropoff);
        
        // Find match using injected abstraction
        Driver matchedDriver = driverMatchingStrategy.findMatch(passenger, pickup);
        ride.acceptRide(matchedDriver);
        matchedDriver.startRide(); 
        
        rideRepository.save(ride);
        return ride;
    }

    public List<Ride> viewAllRides() {
        return rideRepository.findAll();
    }
}
