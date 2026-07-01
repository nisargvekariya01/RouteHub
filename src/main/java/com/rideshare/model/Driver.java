package com.rideshare.model;

import com.rideshare.pattern.observer.TripObserver;
import java.util.concurrent.atomic.AtomicBoolean;

public class Driver implements TripObserver {
    private final String id;
    private final String name;
    private final Vehicle vehicle;
    private Location currentLocation;
    private double rating;
    
    // AtomicBoolean used to prevent race conditions during driver matching (double booking).
    private final AtomicBoolean isAvailable = new AtomicBoolean(true);

    public Driver(String id, String name, Vehicle vehicle, Location currentLocation, double rating) {
        this.id = id;
        this.name = name;
        this.vehicle = vehicle;
        this.currentLocation = currentLocation;
        this.rating = rating;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public Vehicle getVehicle() { return vehicle; }
    public Location getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(Location currentLocation) { this.currentLocation = currentLocation; }
    public double getRating() { return rating; }
    
    public boolean isAvailable() { return isAvailable.get(); }
    
    /**
     * Thread-safe acquire.
     * What would break without lock/atomic: Two concurrent ride requests might read isAvailable=true
     * at the same time and both assign the same driver to different trips.
     */
    public boolean tryAcquire() {
        return isAvailable.compareAndSet(true, false);
    }
    
    public void release() {
        isAvailable.set(true);
    }

    @Override
    public void onTripStatusChanged(Trip trip) {
        System.out.println("[Driver App] " + name + ": Trip " + trip.getId() + " status is now " + trip.getStatus());
        if ("COMPLETED".equals(trip.getStatus()) || "CANCELLED".equals(trip.getStatus())) {
            release();
            if ("COMPLETED".equals(trip.getStatus()) && trip.getDestination() != null) {
                this.currentLocation = trip.getDestination();
            }
        }
    }
}
