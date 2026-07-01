package repositories;

import models.Ride;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Repository class for managing Ride data.
 * 
 * WHY SEPARATE STORAGE FROM BUSINESS LOGIC?
 * By introducing a repository, we centralize all data access code. 
 * The RideService shouldn't be polluted with data structure manipulation 
 * (like Map.put or list filtering). This isolation allows developers to optimize 
 * queries, introduce caching, or change databases entirely without modifying 
 * a single line of the RideService business rules.
 */
public class RideRepository {
    private final Map<String, Ride> dataStore = new HashMap<>();

    public void save(Ride ride) {
        dataStore.put(ride.getId(), ride);
    }

    public void update(Ride ride) {
        if (dataStore.containsKey(ride.getId())) {
            dataStore.put(ride.getId(), ride);
        } else {
            throw new IllegalArgumentException("Ride does not exist for update.");
        }
    }

    public void delete(String id) {
        dataStore.remove(id);
    }

    public Optional<Ride> findById(String id) {
        return Optional.ofNullable(dataStore.get(id));
    }

    public List<Ride> findAll() {
        return new ArrayList<>(dataStore.values());
    }

    public List<Ride> search(Predicate<Ride> criteria) {
        return dataStore.values().stream()
                .filter(criteria)
                .collect(Collectors.toList());
    }
}
