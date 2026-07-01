package com.rideshare.pattern.state;

import com.rideshare.exception.InvalidTripStateTransitionException;
import com.rideshare.model.Trip;

public class CancelledState implements TripState {
    @Override
    public void request(Trip trip) { throw new InvalidTripStateTransitionException("Trip cancelled."); }
    @Override
    public void accept(Trip trip) { throw new InvalidTripStateTransitionException("Trip cancelled."); }
    @Override
    public void start(Trip trip) { throw new InvalidTripStateTransitionException("Trip cancelled."); }
    @Override
    public void complete(Trip trip) { throw new InvalidTripStateTransitionException("Trip cancelled."); }
    @Override
    public void cancel(Trip trip) { throw new InvalidTripStateTransitionException("Trip already cancelled."); }
    @Override
    public String getName() { return "CANCELLED"; }
}
