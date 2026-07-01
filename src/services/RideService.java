package services;

import exceptions.RideNotFoundException;
import models.Driver;
import models.Location;
import models.Passenger;
import models.Ride;
import repositories.RideRepository;
import strategies.matching.DriverMatchingStrategy;
import strategies.pricing.FareStrategy;
import observers.NotificationService;

import java.util.ArrayList;
import java.util.List;

/**
 * OBSERVER & STRATEGY PATTERNS:
 * - OBSERVER: This class acts as the Subject. It holds `notificationObservers` and broadcasts lifecycle state changes.
 * - STRATEGY: It relies entirely on injected strategies (`DriverMatchingStrategy`, `FareStrategy`) via DI to decouple algorithms.
 */
public class RideService {
    private final RideRepository rideRepository;
    private final DriverMatchingStrategy driverMatchingStrategy;
    private final FareStrategy fareStrategy;
    
    private final List<NotificationService> notificationObservers = new ArrayList<>();

    public RideService(RideRepository rideRepository, DriverMatchingStrategy driverMatchingStrategy, FareStrategy fareStrategy) {
        this.rideRepository = rideRepository;
        this.driverMatchingStrategy = driverMatchingStrategy;
        this.fareStrategy = fareStrategy;
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

    public Ride requestRide(Passenger passenger, Location pickup, Location dropoff) {
        if (passenger == null || pickup == null || dropoff == null) {
            throw new IllegalArgumentException("Invalid ride request parameters.");
        }

        // BUILDER PATTERN in usage: Creating the Ride cleanly
        Ride ride = new Ride.Builder()
                .passenger(passenger)
                .pickupLocation(pickup)
                .dropoffLocation(dropoff)
                .build();
                
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
            .orElseThrow(() -> new RideNotFoundException("Ride with ID " + rideId + " not found."));
            
        ride.startRide(driverId);
        rideRepository.update(ride);
        
        notifyObservers(ride, "Ride has officially STARTED. Passenger is en route.");
        return ride;
    }

    public Ride completeRide(String rideId, boolean isPeakHour) {
        Ride ride = rideRepository.findById(rideId)
            .orElseThrow(() -> new RideNotFoundException("Ride with ID " + rideId + " not found."));
            
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
