package com.rideshare.exception;

/**
 * Thrown when an illegal state transition is attempted on a Trip.
 */
public class InvalidTripStateTransitionException extends RuntimeException {
    public InvalidTripStateTransitionException(String message) {
        super(message);
    }
}
