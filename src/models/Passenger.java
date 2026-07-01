package models;

/**
 * Model representing a passenger requesting rides.
 * Exists to encapsulate passenger-specific data, such as their rating 
 * and specific preferences, distinct from drivers.
 */
public class Passenger extends User {
    private double rating;

    public Passenger(String id, String name, String phoneNumber) {
        super(id, name, phoneNumber);
        this.rating = 5.0; // Default starting rating
    }

    public double getRating() {
        return rating;
    }

    /**
     * Meaningful method to update the passenger's rating ensuring validity.
     */
    public void updateRating(double newRating) {
        if (newRating >= 1.0 && newRating <= 5.0) {
            this.rating = newRating;
        }
    }
}
