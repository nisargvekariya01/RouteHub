package repositories;

import models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Repository for managing Users.
 * 
 * SINGLETON PATTERN:
 * We use the Singleton pattern here to ensure that only ONE instance of the in-memory database 
 * (the HashMap) exists across the entire application lifecycle. This guarantees a single source 
 * of truth and prevents data fragmentation where different services might accidentally read/write 
 * from different isolated instances.
 */
public class UserRepository {
    private static UserRepository instance;
    private final Map<String, User> database;

    // Private constructor blocks instantiation via 'new' keyword
    private UserRepository() {
        this.database = new HashMap<>();
    }

    // Static access method with synchronization for thread safety
    public static synchronized UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    public void save(User user) {
        database.put(user.getId(), user);
    }

    public Optional<User> findById(String id) {
        return Optional.ofNullable(database.get(id));
    }

    public void update(User user) {
        if (database.containsKey(user.getId())) {
            database.put(user.getId(), user);
        }
    }

    public List<User> findAll() {
        return new ArrayList<>(database.values());
    }
}
