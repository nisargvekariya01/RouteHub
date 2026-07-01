package com.rideshare.service;

import com.rideshare.exception.NoDriverAvailableException;
import com.rideshare.model.Driver;
import com.rideshare.model.Rider;
import com.rideshare.model.Trip;
import com.rideshare.model.Location;
import com.rideshare.pattern.strategy.PricingStrategy;
import com.rideshare.pattern.strategy.BaseFarePricing;
import com.rideshare.pattern.strategy.SurgePricing;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Singleton Pattern: Central coordinator for assigning rides.
 * 
 * WHY SINGLETON: There should only be one matching engine making decisions across the system 
 * to prevent conflicting state and coordinate the shared pool of drivers effectively.
 * Double-checked locking ensures thread safety and lazy initialization.
 */
public class RideDispatcher {
    
    private static volatile RideDispatcher instance;
    private final GridManager gridManager;
    
    // Metrics
    private final AtomicInteger completedTrips = new AtomicInteger(0);
    private final AtomicInteger totalRequests = new AtomicInteger(0);
    private final AtomicReference<Double> totalRevenue = new AtomicReference<>(0.0);

    private RideDispatcher() {
        this.gridManager = new GridManager();
    }

    public static RideDispatcher getInstance() {
        if (instance == null) {
            synchronized (RideDispatcher.class) {
                if (instance == null) {
                    instance = new RideDispatcher();
                }
            }
        }
        return instance;
    }

    public GridManager getGridManager() {
        return gridManager;
    }
    
    public int getCompletedTrips() {
        return completedTrips.get();
    }
    
    public void incrementCompletedTrips() {
        completedTrips.incrementAndGet();
    }
    
    public void addRevenue(double amount) {
        totalRevenue.updateAndGet(v -> v + amount);
    }
    
    public double getTotalRevenue() {
        return totalRevenue.get();
    }

    /**
     * Matches a rider to an available driver, handles surge pricing and concurrency safely.
     */
    public Trip requestRide(Rider rider, Location destination) {
        totalRequests.incrementAndGet();
        Trip trip = new Trip(UUID.randomUUID().toString().substring(0, 8), rider, rider.getCurrentLocation(), destination);
        
        List<Driver> candidates = gridManager.getNearbyDrivers(rider.getCurrentLocation());
        
        // PriorityQueue to rank drivers based on distance (closer is better) and rating (higher is better)
        PriorityQueue<Driver> pq = new PriorityQueue<>(
            Comparator.comparingDouble((Driver d) -> d.getCurrentLocation().distanceTo(rider.getCurrentLocation()))
                      .thenComparing((Driver d) -> -d.getRating())
        );
        
        int totalDriversInCells = candidates.size();
        int availableCount = 0;
        
        for (Driver candidate : candidates) {
            if (candidate.isAvailable()) {
                availableCount++;
                pq.add(candidate);
            }
        }

        // Surge Pricing Logic
        PricingStrategy pricing = new BaseFarePricing();
        if (totalDriversInCells > 0 && availableCount < totalDriversInCells / 2) {
            System.out.println("[System] High demand detected near Rider " + rider.getName() + " -> Applying Surge Pricing");
            pricing = new SurgePricing(2.0); // 2x surge
        }
        
        Driver matchedDriver = null;
        while (!pq.isEmpty()) {
            Driver d = pq.poll();
            // Thread-safe acquire to prevent race conditions during concurrent matching
            if (d.tryAcquire()) {
                matchedDriver = d;
                break;
            }
        }

        if (matchedDriver == null) {
            trip.cancel();
            throw new NoDriverAvailableException("No drivers available near " + rider.getName());
        }

        trip.setDriver(matchedDriver);
        trip.accept(); // Transition to ACCEPTED
        
        double fare = pricing.calculateFare(trip);
        trip.setFare(fare);
        
        return trip;
    }
}
