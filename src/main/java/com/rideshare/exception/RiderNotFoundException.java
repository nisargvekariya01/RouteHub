package com.rideshare.exception;

/**
 * Thrown when a rider cannot be found in the system.
 */
public class RiderNotFoundException extends RuntimeException {
    public RiderNotFoundException(String message) {
        super(message);
    }
}
