package repositories;

import models.Ride;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Repository for managing Rides.
 * 
 * SINGLETON PATTERN:
 * Guarantees a single in-memory database instance for Rides globally across the system.
 */
public class RideRepository {
    private static RideRepository instance;
    private final Map<String, Ride> database;

    private RideRepository() {
        this.database = new HashMap<>();
    }

    public static synchronized RideRepository getInstance() {
        if (instance == null) {
            instance = new RideRepository();
        }
        return instance;
    }

    public void save(Ride ride) {
        database.put(ride.getId(), ride);
    }

    public Optional<Ride> findById(String id) {
        return Optional.ofNullable(database.get(id));
    }

    public void update(Ride ride) {
        if (database.containsKey(ride.getId())) {
            database.put(ride.getId(), ride);
        }
    }

    public List<Ride> findAll() {
        return new ArrayList<>(database.values());
    }
}
