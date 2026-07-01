package models;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Model representing a passenger requesting rides.
 * 
 * EXPLAINING DATA AGGREGATION:
 * Aggregation mathematically condenses a high volume of events into a summary statistic.
 * Here, we aggregate all historical ride ratings into a single `averageRating` metric. 
 * By tracking the `totalRatings` count alongside the moving average, we completely avoid 
 * storing every past rating in memory, keeping our data structures extremely lightweight.
 */
public class Passenger extends User {
    private double averageRating;
    private int totalRatings;
    
    // History Tracking
    private final List<Ride> rideHistory;

    public Passenger(String id, String name, String phoneNumber) {
        super(id, name, phoneNumber);
        this.averageRating = 5.0; // Default starting rating
        this.totalRatings = 0;
        this.rideHistory = new ArrayList<>();
    }

    public double getRating() {
        return averageRating;
    }

    public void addRating(int newRating) {
        if (newRating < 1 || newRating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }
        
        this.averageRating = ((this.averageRating * this.totalRatings) + newRating) / (this.totalRatings + 1);
        this.totalRatings++;
    }

    // --- RIDE HISTORY ---

    public void addRideToHistory(Ride ride) {
        if (ride != null) {
            this.rideHistory.add(ride);
        }
    }

    public List<Ride> getAllPreviousRides() {
        return new ArrayList<>(this.rideHistory);
    }

    public List<Ride> getCompletedRides() {
        return this.rideHistory.stream()
                .filter(r -> r.getStatus() == RideStatus.COMPLETED)
                .collect(Collectors.toList());
    }

    public List<Ride> getCancelledRides() {
        return this.rideHistory.stream()
                .filter(r -> r.getStatus() == RideStatus.CANCELLED)
                .collect(Collectors.toList());
    }

    /**
     * Helper method to print the passenger's history to the terminal.
     */
    public void displayHistory() {
        System.out.println("--- Passenger History for " + getName() + " ---");
        System.out.println("Total Requested Rides: " + rideHistory.size());
        System.out.println("Completed Rides: " + getCompletedRides().size());
        System.out.println("Cancelled Rides: " + getCancelledRides().size());
        System.out.println("Current Rating: " + String.format("%.2f", averageRating) + " stars");
    }
}
