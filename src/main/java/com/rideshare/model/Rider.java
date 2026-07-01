package com.rideshare.model;

import com.rideshare.pattern.observer.TripObserver;

public class Rider implements TripObserver {
    private final String id;
    private final String name;
    private Location currentLocation;
    private double rating;

    public Rider(String id, String name, Location currentLocation, double rating) {
        this.id = id;
        this.name = name;
        this.currentLocation = currentLocation;
        this.rating = rating;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public Location getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(Location currentLocation) { this.currentLocation = currentLocation; }
    public double getRating() { return rating; }

    @Override
    public void onTripStatusChanged(Trip trip) {
        System.out.println("[Rider App] " + name + ": Trip " + trip.getId() + " status is now " + trip.getStatus());
    }
}
