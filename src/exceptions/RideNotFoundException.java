package exceptions;

public class RideNotFoundException extends RideShareException {
    public RideNotFoundException(String message) {
        super(message);
    }
}
