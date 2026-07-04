package repositories;

import models.Driver;

/**
 * Concrete implementation of the DriverRepository.
 * 
 * SINGLETON PATTERN: Guarantees a single in-memory database globally.
 */
public class InMemoryDriverRepository extends InMemoryCrudRepository<Driver, String> implements DriverRepository {
    private static InMemoryDriverRepository instance;

    private InMemoryDriverRepository() {
        super();
    }

    public static synchronized InMemoryDriverRepository getInstance() {
        if (instance == null) {
            instance = new InMemoryDriverRepository();
        } 
        return instance;
    }

    @Override
    protected String getEntityId(Driver entity) {
        return entity.getId();
    }
}
