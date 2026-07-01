package com.rideshare.pattern.state;

import com.rideshare.exception.InvalidTripStateTransitionException;
import com.rideshare.model.Trip;

public class RequestedState implements TripState {
    @Override
    public void request(Trip trip) {
        throw new InvalidTripStateTransitionException("Trip is already requested.");
    }

    @Override
    public void accept(Trip trip) {
        trip.setState(new AcceptedState());
    }

    @Override
    public void start(Trip trip) {
        throw new InvalidTripStateTransitionException("Cannot start a trip that hasn't been accepted.");
    }

    @Override
    public void complete(Trip trip) {
        throw new InvalidTripStateTransitionException("Cannot complete a requested trip.");
    }

    @Override
    public void cancel(Trip trip) {
        trip.setState(new CancelledState());
    }
    
    @Override
    public String getName() { return "REQUESTED"; }
}
