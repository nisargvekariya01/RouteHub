package services;

import exceptions.RideNotFoundException;
import exceptions.RideShareException;
import models.Driver;
import models.Location;
import models.Passenger;
import models.Ride;
import repositories.RideRepository;
import strategies.matching.DriverMatchingStrategy;
import strategies.matching.NavigationStrategy;
import strategies.pricing.FareStrategy;
import observers.NotificationService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * OBSERVER & STRATEGY PATTERNS:
 * - OBSERVER: This class acts as the Subject. It holds `notificationObservers` and broadcasts lifecycle state changes.
 * - STRATEGY: It relies entirely on injected strategies (`DriverMatchingStrategy`, `FareStrategy`, `NavigationStrategy`) via DI.
 */
public class RideService {
    private final RideRepository rideRepository;
    private final DriverMatchingStrategy matchingStrategy;
    private final FareStrategy fareStrategy;
    private final NavigationStrategy navigationStrategy;
    private final List<NotificationService> notificationObservers = new ArrayList<>();

    public RideService(RideRepository rideRepository, DriverMatchingStrategy matchingStrategy, 
                       FareStrategy fareStrategy, NavigationStrategy navigationStrategy) {
        this.rideRepository = rideRepository;
        this.matchingStrategy = matchingStrategy;
        this.fareStrategy = fareStrategy;
        this.navigationStrategy = navigationStrategy;
    }

    public void addNotificationObserver(NotificationService observer) {
        if (observer != null) {
            this.notificationObservers.add(observer);
        }
    }
    
    private void notifyObservers(Ride ride, String message) {
        for (NotificationService observer : notificationObservers) {
            observer.onRideUpdate(ride, message);
        }
    }

    /**
     * Step 1 of Ride Flow: Estimates a ride by finding the shortest road travel time via Dijkstra.
     */
    public double estimateRideTravelTime(Location pickup, Location dropoff) {
        double travelTime = navigationStrategy.getShortestPathTravelTime(pickup, dropoff);
        if (travelTime == -1) {
            throw new RideShareException("Cannot find a valid road path between these locations.");
        }
        return travelTime;
    }

    /**
     * Step 2 of Ride Flow: User confirms the ride, we match a driver and lock it in.
     */
    public Ride confirmRide(Passenger passenger, Location pickup, Location dropoff) {
        if (passenger == null || pickup == null || dropoff == null) {
            throw new IllegalArgumentException("Invalid ride request parameters.");
        }
        double travelTime = estimateRideTravelTime(pickup, dropoff);

        Ride ride = new Ride.Builder()
                .id(UUID.randomUUID().toString().substring(0, 8))
                .passenger(passenger)
                .pickupLocation(pickup)
                .dropoffLocation(dropoff)
                .travelTimeSeconds(travelTime) // Set the actual road travel time
                .build();
                
        passenger.addRideToHistory(ride);
        notifyObservers(ride, "Ride requested and is currently PENDING. Searching for drivers on the road network...");
        
        // This strategy now runs Dijkstra internally to find the closest online driver!
        Driver matchedDriver = matchingStrategy.findMatchingDriver(ride);
        
        ride.acceptRide(matchedDriver);
        matchedDriver.startRide(); 
        rideRepository.save(ride);
        
        notifyObservers(ride, "Ride ACCEPTED by driver: " + matchedDriver.getName());
        return ride;
    }

    public Ride startRide(String rideId, String driverId) {
        Ride ride = rideRepository.findById(rideId)
            .orElseThrow(() -> new RideNotFoundException("Ride with ID " + rideId + " not found."));
            
        ride.startRide(driverId);
        rideRepository.update(ride);
        
        notifyObservers(ride, "Ride has officially STARTED. Passenger is en route.");
        return ride;
    }

    public double estimateFare(double travelTimeSeconds) {
        boolean isPeakHour = false; // Could be dynamic
        // Create a dummy ride to pass into the strategy
        Ride dummy = new Ride.Builder()
                .id("dummy")
                .passenger(new Passenger("dummy", "dummy", "dummy"))
                .pickupLocation(new Location(0,0))
                .dropoffLocation(new Location(0,0))
                .travelTimeSeconds(travelTimeSeconds).build();
        return fareStrategy.calculateFare(dummy, false);
    }

    public Ride completeRide(String rideId, boolean isPeakHour) {
        Ride ride = rideRepository.findById(rideId)
            .orElseThrow(() -> new RideNotFoundException("Ride with ID " + rideId + " not found."));
            
        // Assuming fareStrategy calculates based on ride.getDistance()
        double finalFare = fareStrategy.calculateFare(ride, isPeakHour); 
        // If PeakHour strategy logic was required, it would be a separate decorator or strategy.
        // For standard compilation based on previous state:
        
        ride.completeRide(finalFare);
        ride.getDriver().addEarnings(finalFare);
        ride.getDriver().finishRide();
        rideRepository.update(ride);
        
        notifyObservers(ride, "Ride COMPLETED successfully. Final Fare charged: $" + String.format("%.2f", finalFare));
        return ride;
    }

    public List<Ride> viewAllRides() {
        return rideRepository.findAll();
    }
}
