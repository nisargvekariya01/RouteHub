package models;

import models.enums.*;

/**
 * Model representing a driver in the system.
 * 
 * EXPLAINING DATA AGGREGATION:
 * Aggregation is the act of summarizing many individual data points into a single metric.
 * Instead of storing a massive historical List of every rating a driver has ever received 
 * (which would waste memory and require O(N) recalculations), we aggregate the average iteratively.
 * By maintaining just two variables (`averageRating` and `totalRatings`), we can instantly compute 
 * the new moving average using a cumulative formula in O(1) constant time.
 * 
 * WHY STATE MANAGEMENT IS IMPORTANT:
 * State management guarantees that an entity cannot perform invalid actions based on its current context.
 * Enforcing rules strictly inside the domain model using Enums prevents race conditions and double-booking, 
 * ensuring the entire system remains in a valid and consistent state.
 */
public class Driver extends User {
    private Vehicle vehicle;
    private DriverStatus status;
    private double averageRating;
    private int totalRatings;
    
    // History & Performance Tracking
    private int tripsCompleted;
    private double totalEarnings;

    public Driver(String id, String name, String phoneNumber, Vehicle vehicle) {
        super(id, name, phoneNumber);
        this.vehicle = vehicle;
        this.status = DriverStatus.OFFLINE;
        this.averageRating = 5.0; // Default rating
        this.totalRatings = 0;
        this.tripsCompleted = 0;
        this.totalEarnings = 0.0;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public DriverStatus getStatus() {
        return status;
    }

    public double getRating() {
        return averageRating;
    }

    public void setVehicle(Vehicle vehicle) {
        if (vehicle != null) {
            this.vehicle = vehicle;
        }
    }

    public void addRating(int newRating) {
        if (newRating < 1 || newRating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }
        this.averageRating = ((this.averageRating * this.totalRatings) + newRating) / (this.totalRatings + 1);
        this.totalRatings++;
    }

    public boolean isAvailable() {
        return this.status == DriverStatus.ONLINE;
    }

    // --- HISTORY & EARNINGS ---

    public int getTripsCompleted() {
        return tripsCompleted;
    }

    public double getTotalEarnings() {
        return totalEarnings;
    }

    public void addEarnings(double amount) {
        if (amount > 0) {
            this.totalEarnings += amount;
            this.tripsCompleted++;
        }
    }

    /**
     * Helper method to display driver history and stats to the terminal.
     */
    public void displayHistory() {
        System.out.println("--- Driver Stats for " + getName() + " ---");
        System.out.println("Trips Completed: " + tripsCompleted);
        System.out.println("Total Earnings: $" + String.format("%.2f", totalEarnings));
        System.out.println("Current Rating: " + String.format("%.2f", averageRating) + " stars (from " + totalRatings + " reviews)");
    }

    // --- STATE MANAGEMENT ---

    public void goOnline() {
        if (this.status == DriverStatus.OFFLINE) {
            this.status = DriverStatus.ONLINE;
        } else {
            throw new IllegalStateException("Driver can only go online from an offline state.");
        }
    }

    public void goOffline() {
        if (this.status == DriverStatus.ONLINE) {
            this.status = DriverStatus.OFFLINE;
        } else if (this.status == DriverStatus.ON_TRIP) {
            throw new IllegalStateException("Driver cannot go offline while on a trip.");
        }
    }

    public void startRide() {
        if (this.status == DriverStatus.ONLINE) {
            this.status = DriverStatus.ON_TRIP;
        } else {
            throw new IllegalStateException("Driver must be online and not on a trip to start a ride.");
        }
    }

    public void finishRide() {
        if (this.status == DriverStatus.ON_TRIP) {
            this.status = DriverStatus.ONLINE;
        } else {
            throw new IllegalStateException("Driver must be on a trip to finish it.");
        }
    }
}
