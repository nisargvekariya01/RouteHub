package com.rideshare.pattern.state;

import com.rideshare.exception.InvalidTripStateTransitionException;
import com.rideshare.model.Trip;

public class OngoingState implements TripState {
    @Override
    public void request(Trip trip) { throw new InvalidTripStateTransitionException("Trip is ongoing."); }

    @Override
    public void accept(Trip trip) { throw new InvalidTripStateTransitionException("Trip is ongoing."); }

    @Override
    public void start(Trip trip) { throw new InvalidTripStateTransitionException("Trip already started."); }

    @Override
    public void complete(Trip trip) { trip.setState(new CompletedState()); }

    @Override
    public void cancel(Trip trip) { trip.setState(new CancelledState()); }
    
    @Override
    public String getName() { return "ONGOING"; }
}
