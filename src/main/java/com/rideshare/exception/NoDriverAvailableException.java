package com.rideshare.exception;

/**
 * Thrown when no available driver can be found for a ride request.
 */
public class NoDriverAvailableException extends RuntimeException {
    public NoDriverAvailableException(String message) {
        super(message);
    }
}
