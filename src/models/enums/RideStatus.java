package models.enums;

/**
 * Enum representing the current state of a ride.
 * Exists to ensure type safety and restrict ride states to valid predefined values,
 * preventing invalid state transitions.
 */
public enum RideStatus {
    PENDING,
    ACCEPTED,
    STARTED,
    COMPLETED,
    CANCELLED
}
