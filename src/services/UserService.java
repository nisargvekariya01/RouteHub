package services;

import models.Passenger;
import models.User;
import repositories.UserRepository;

import java.util.List;
import java.util.UUID;

/**
 * Service handling user (passenger) related business logic.
 * 
 * SOLID Principles Applied:
 * - SRP: This class focuses strictly on user-related actions.
 * - DIP: Depends on the UserRepository abstraction.
 */
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Passenger registerPassenger(String name, String phoneNumber) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Passenger name cannot be empty.");
        }
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be empty.");
        }

        String id = UUID.randomUUID().toString();
        Passenger passenger = new Passenger(id, name, phoneNumber);
        
        userRepository.save(passenger);
        return passenger;
    }
    
    /**
     * Submits a new rating for a passenger, securely recalculating their aggregate average.
     */
    public Passenger ratePassenger(String userId, int rating) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found."));
            
        if (!(user instanceof Passenger)) {
            throw new IllegalArgumentException("Only passengers can receive passenger ratings.");
        }
        
        Passenger passenger = (Passenger) user;
        passenger.addRating(rating);
        userRepository.update(passenger);
        return passenger;
    }

    public List<User> viewAllUsers() {
        return userRepository.findAll();
    }
}
