package com.rideshare.pattern.observer;

import com.rideshare.model.Trip;

/**
 * Observer Pattern interface.
 * 
 * WHY OBSERVER PATTERN: It establishes a one-to-many dependency between a Trip
 * and interested parties (Riders, Drivers). When a Trip changes state, we don't
 * want the Trip class to have hardcoded knowledge of how to update Riders or Drivers.
 * This pattern decouples the subject (Trip) from its observers.
 */
public interface TripObserver {
    void onTripStatusChanged(Trip trip);
}
