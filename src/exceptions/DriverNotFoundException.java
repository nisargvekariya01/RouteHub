package exceptions;

public class DriverNotFoundException extends RideShareException {
    public DriverNotFoundException(String message) {
        super(message);
    }
}
