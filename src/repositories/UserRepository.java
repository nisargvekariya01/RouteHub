package repositories;

import models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Repository class for managing User (Passenger) data.
 * 
 * WHY SEPARATE STORAGE FROM BUSINESS LOGIC?
 * The Repository Pattern acts as an in-memory domain object collection.
 * It separates the business logic (Services) from data access logic.
 * This ensures that if we decide to switch from an in-memory Map to an actual database 
 * (like SQL or NoSQL) in the future, we only need to change the repository implementation.
 * The services remain completely untouched, adhering to the Single Responsibility Principle 
 * and Open/Closed Principle. It also significantly improves testability.
 */
public class UserRepository {
    // Using a Map for O(1) lookup by ID
    private final Map<String, User> dataStore = new HashMap<>();

    public void save(User user) {
        dataStore.put(user.getId(), user);
    }

    public void update(User user) {
        if (dataStore.containsKey(user.getId())) {
            dataStore.put(user.getId(), user);
        } else {
            throw new IllegalArgumentException("User does not exist for update.");
        }
    }

    public void delete(String id) {
        dataStore.remove(id);
    }

    public Optional<User> findById(String id) {
        return Optional.ofNullable(dataStore.get(id));
    }

    public List<User> findAll() {
        return new ArrayList<>(dataStore.values());
    }

    public List<User> search(Predicate<User> criteria) {
        return dataStore.values().stream()
                .filter(criteria)
                .collect(Collectors.toList());
    }
}
