package com.rideshare.pattern.state;

import com.rideshare.model.Trip;

/**
 * State Pattern interface.
 * 
 * WHY STATE PATTERN: A trip goes through a complex lifecycle. By encapsulating
 * state-specific behavior and transition logic into distinct state classes,
 * we avoid a massive switch statement in the Trip class and ensure illegal
 * transitions throw exceptions properly.
 */
public interface TripState {
    void request(Trip trip);
    void accept(Trip trip);
    void start(Trip trip);
    void complete(Trip trip);
    void cancel(Trip trip);
    String getName();
}
