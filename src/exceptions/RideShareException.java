package exceptions;

/**
 * Base custom exception for the application.
 * Responsibility: Provides a specific exception type for anticipated errors within the domain logic 
 * (e.g., DriverNotAvailableException, InvalidLocationException).
 */
public class RideShareException extends RuntimeException {
    public RideShareException(String message) {
        super(message);
    }
}
