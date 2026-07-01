package services;

import models.Driver;
import models.Location;
import models.Passenger;
import models.Ride;
import repositories.RideRepository;
import strategies.DriverMatchingStrategy;
import strategies.FareStrategy;
import services.notifications.NotificationService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service for orchestrating ride-related business operations.
 * Acts as the 'Subject' (or Observable) in the Observer Pattern.
 */
public class RideService {
    private final RideRepository rideRepository;
    
    private final DriverMatchingStrategy driverMatchingStrategy;
    private final FareStrategy fareStrategy;
    
    // OBSERVER PATTERN: List of observers listening to ride lifecycle events
    private final List<NotificationService> notificationObservers = new ArrayList<>();

    public RideService(RideRepository rideRepository, DriverMatchingStrategy driverMatchingStrategy, FareStrategy fareStrategy) {
        this.rideRepository = rideRepository;
        this.driverMatchingStrategy = driverMatchingStrategy;
        this.fareStrategy = fareStrategy;
    }

    /**
     * OBSERVER PATTERN: Dynamically register new notification channels.
     */
    public void addNotificationObserver(NotificationService observer) {
        if (observer != null) {
            this.notificationObservers.add(observer);
        }
    }
    
    /**
     * OBSERVER PATTERN: Broadcast updates to all registered channels.
     */
    private void notifyObservers(Ride ride, String message) {
        for (NotificationService observer : notificationObservers) {
            observer.onRideUpdate(ride, message);
        }
    }

    public Ride requestRide(Passenger passenger, Location pickup, Location dropoff) {
        if (passenger == null || pickup == null || dropoff == null) {
            throw new IllegalArgumentException("Invalid ride request parameters.");
        }

        String rideId = UUID.randomUUID().toString();
        Ride ride = new Ride(rideId, passenger, pickup, dropoff);
        passenger.addRideToHistory(ride);
        
        notifyObservers(ride, "Ride requested and is currently PENDING. Searching for drivers...");
        
        Driver matchedDriver = driverMatchingStrategy.findMatch(passenger, pickup);
        ride.acceptRide(matchedDriver);
        matchedDriver.startRide(); 
        rideRepository.save(ride);
        
        notifyObservers(ride, "Ride ACCEPTED by driver: " + matchedDriver.getName());
        return ride;
    }

    public Ride startRide(String rideId, String driverId) {
        Ride ride = rideRepository.findById(rideId)
            .orElseThrow(() -> new IllegalArgumentException("Ride not found."));
            
        ride.startRide(driverId);
        rideRepository.update(ride);
        
        notifyObservers(ride, "Ride has officially STARTED. Passenger is en route.");
        return ride;
    }

    public Ride completeRide(String rideId, boolean isPeakHour) {
        Ride ride = rideRepository.findById(rideId)
            .orElseThrow(() -> new IllegalArgumentException("Ride not found."));
            
        double finalFare = fareStrategy.calculateFare(ride, isPeakHour);
        
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
