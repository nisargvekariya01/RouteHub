package repositories;

import models.Driver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Repository for managing Drivers.
 * 
 * SINGLETON PATTERN:
 * Guarantees a single in-memory database instance for Drivers globally across the system.
 */
public class DriverRepository {
    private static DriverRepository instance;
    private final Map<String, Driver> database;

    private DriverRepository() {
        this.database = new HashMap<>();
    }

    public static synchronized DriverRepository getInstance() {
        if (instance == null) {
            instance = new DriverRepository();
        }
        return instance;
    }

    public void save(Driver driver) {
        database.put(driver.getId(), driver);
    }

    public Optional<Driver> findById(String id) {
        return Optional.ofNullable(database.get(id));
    }

    public void update(Driver driver) {
        if (database.containsKey(driver.getId())) {
            database.put(driver.getId(), driver);
        }
    }

    public List<Driver> findAll() {
        return new ArrayList<>(database.values());
    }
}
