package com.rideshare.pattern.state;

import com.rideshare.exception.InvalidTripStateTransitionException;
import com.rideshare.model.Trip;

public class CompletedState implements TripState {
    @Override
    public void request(Trip trip) { throw new InvalidTripStateTransitionException("Trip already completed."); }
    @Override
    public void accept(Trip trip) { throw new InvalidTripStateTransitionException("Trip already completed."); }
    @Override
    public void start(Trip trip) { throw new InvalidTripStateTransitionException("Trip already completed."); }
    @Override
    public void complete(Trip trip) { throw new InvalidTripStateTransitionException("Trip already completed."); }
    @Override
    public void cancel(Trip trip) { throw new InvalidTripStateTransitionException("Cannot cancel a completed trip."); }
    @Override
    public String getName() { return "COMPLETED"; }
}
