package services;

import exceptions.DuplicateUserException;
import exceptions.RideShareException;
import models.Passenger;
import models.User;
import repositories.UserRepository;

import java.util.List;
import java.util.UUID;

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

        // Enforce uniqueness with a custom domain exception
        boolean exists = userRepository.findAll().stream()
                .anyMatch(u -> u.getPhoneNumber().equals(phoneNumber));
        if (exists) {
            throw new DuplicateUserException("Passenger with phone number " + phoneNumber + " already exists.");
        }

        String id = UUID.randomUUID().toString();
        Passenger passenger = new Passenger(id, name, phoneNumber);
        
        userRepository.save(passenger);
        return passenger;
    }
    
    public Passenger ratePassenger(String userId, int rating) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RideShareException("User with ID " + userId + " not found."));
            
        if (!(user instanceof Passenger)) {
            throw new RideShareException("Only passengers can receive passenger ratings.");
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
