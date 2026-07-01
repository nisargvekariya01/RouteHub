package exceptions;

public class InvalidRideStateException extends RideShareException {
    public InvalidRideStateException(String message) {
        super(message);
    }
}
