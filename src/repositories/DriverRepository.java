package repositories;

import models.Driver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Repository class for managing Driver data.
 * 
 * WHY SEPARATE STORAGE FROM BUSINESS LOGIC?
 * Repositories decouple the persistence layer from the domain layer (services).
 * The business logic only knows how to request and pass domain models (Drivers).
 * It doesn't care whether the drivers are stored in memory, a relational database, 
 * or a third-party API. This isolation allows developers to easily mock repositories 
 * for unit testing without relying on complex database setups.
 */
public class DriverRepository {
    private final Map<String, Driver> dataStore = new HashMap<>();

    public void save(Driver driver) {
        dataStore.put(driver.getId(), driver);
    }

    public void update(Driver driver) {
        if (dataStore.containsKey(driver.getId())) {
            dataStore.put(driver.getId(), driver);
        } else {
            throw new IllegalArgumentException("Driver does not exist for update.");
        }
    }

    public void delete(String id) {
        dataStore.remove(id);
    }

    public Optional<Driver> findById(String id) {
        return Optional.ofNullable(dataStore.get(id));
    }

    public List<Driver> findAll() {
        return new ArrayList<>(dataStore.values());
    }

    public List<Driver> search(Predicate<Driver> criteria) {
        return dataStore.values().stream()
                .filter(criteria)
                .collect(Collectors.toList());
    }
}
