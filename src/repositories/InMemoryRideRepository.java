package repositories;

import models.Ride;

/**
 * Concrete implementation of the RideRepository.
 * 
 * SINGLETON PATTERN: Guarantees a single in-memory database globally.
 */
public class InMemoryRideRepository extends InMemoryCrudRepository<Ride, String> implements RideRepository {
    private static InMemoryRideRepository instance;

    private InMemoryRideRepository() {
        super();
    }

    public static synchronized InMemoryRideRepository getInstance() {
        if (instance == null) {
            instance = new InMemoryRideRepository();
        }
        return instance;
    }

    @Override
    protected String getEntityId(Ride entity) {
        return entity.getId();
    }
}
