package com.rideshare.model;

import com.rideshare.pattern.observer.TripObserver;
import com.rideshare.pattern.state.RequestedState;
import com.rideshare.pattern.state.TripState;

import java.util.ArrayList;
import java.util.List;

public class Trip {
    private final String id;
    private final Rider rider;
    private Driver driver;
    private final Location source;
    private final Location destination;
    private double fare;
    private final long timestamp;
    
    private TripState state;
    private final List<TripObserver> observers = new ArrayList<>();

    public Trip(String id, Rider rider, Location source, Location destination) {
        this.id = id;
        this.rider = rider;
        this.source = source;
        this.destination = destination;
        this.timestamp = System.currentTimeMillis();
        this.state = new RequestedState(); // Initial state
        addObserver(rider);
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
        addObserver(driver);
    }

    public String getId() { return id; }
    public Rider getRider() { return rider; }
    public Driver getDriver() { return driver; }
    public Location getSource() { return source; }
    public Location getDestination() { return destination; }
    public double getFare() { return fare; }
    public void setFare(double fare) { this.fare = fare; }
    public long getTimestamp() { return timestamp; }

    public String getStatus() {
        return state.getName();
    }
    
    public void setState(TripState state) {
        this.state = state;
        notifyObservers();
    }
    
    // State delegations
    public void request() { state.request(this); }
    public void accept() { state.accept(this); }
    public void start() { state.start(this); }
    public void complete() { state.complete(this); }
    public void cancel() { state.cancel(this); }

    // Observer methods
    public void addObserver(TripObserver observer) {
        observers.add(observer);
    }
    
    public void removeObserver(TripObserver observer) {
        observers.remove(observer);
    }
    
    private void notifyObservers() {
        for (TripObserver observer : observers) {
            observer.onTripStatusChanged(this);
        }
    }
}
