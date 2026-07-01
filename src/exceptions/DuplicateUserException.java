package exceptions;

public class DuplicateUserException extends RideShareException {
    public DuplicateUserException(String message) {
        super(message);
    }
}
