package exceptions;

/**
 * Base exception class for all RideShareEngine business exceptions.
 * 
 * EXPLAINING EXCEPTION HIERARCHY:
 * Exception hierarchy allows us to categorize and safely catch errors at different levels of granularity.
 * By having a base `RideShareException` extending `RuntimeException`, all specific domain exceptions 
 * (like `DriverNotFoundException` or `InvalidRideStateException`) inherit from it. 
 * 
 * A caller can choose to specifically catch a `DriverNotFoundException` if they want to run 
 * distinct recovery logic for missing drivers, OR they can catch the parent `RideShareException` to generically 
 * handle any business-rule violation without catching fatal systemic errors (like NullPointerExceptions 
 * or OutOfMemoryErrors), which would dangerously happen if we just broadly caught `Exception`.
 */
public class RideShareException extends RuntimeException {
    public RideShareException(String message) {
        super(message);
    }
}
