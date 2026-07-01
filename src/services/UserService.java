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
 * - Single Responsibility Principle (SRP): This class focuses strictly on user-related actions.
 * - Dependency Inversion Principle (DIP): Depends on the UserRepository abstraction, 
 *   allowing for flexible testing and independent storage updates.
 */
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Registers a new passenger in the system.
     * Applies business rules such as validation and ID generation before persistence.
     * 
     * @param name Name of the passenger
     * @param phoneNumber Contact number
     * @return The newly registered passenger
     */
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
     * Retrieves all registered users from the platform.
     * 
     * @return A list of all users.
     */
    public List<User> viewAllUsers() {
        return userRepository.findAll();
    }
}
