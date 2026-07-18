package models;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import models.enums.*;

/**
 * Model representing a passenger requesting rides.
 */
public class Passenger extends User {
    
    // History Tracking
    private final List<Ride> rideHistory;

    public Passenger(String id, String name, String phoneNumber) {
        super(id, name, phoneNumber);
        this.rideHistory = new ArrayList<>();
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
        System.out.println("====== PASSENGER HISTORY ======");
        System.out.println("Passenger: " + getName() + " [" + getId() + "]");
    }
}
