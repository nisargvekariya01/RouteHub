package com.rideshare.pattern.state;

import com.rideshare.exception.InvalidTripStateTransitionException;
import com.rideshare.model.Trip;

public class AcceptedState implements TripState {
    @Override
    public void request(Trip trip) { throw new InvalidTripStateTransitionException("Trip already accepted."); }

    @Override
    public void accept(Trip trip) { throw new InvalidTripStateTransitionException("Trip already accepted."); }

    @Override
    public void start(Trip trip) { trip.setState(new OngoingState()); }

    @Override
    public void complete(Trip trip) { throw new InvalidTripStateTransitionException("Cannot complete without starting."); }

    @Override
    public void cancel(Trip trip) { trip.setState(new CancelledState()); }

    @Override
    public String getName() { return "ACCEPTED"; }
}
