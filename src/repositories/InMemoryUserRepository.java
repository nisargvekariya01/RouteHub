package repositories;

import models.User;

/**
 * Concrete implementation of the UserRepository.
 * 
 * SINGLETON PATTERN: Guarantees a single in-memory database globally.
 */
public class InMemoryUserRepository extends InMemoryCrudRepository<User, String> implements UserRepository {
    private static InMemoryUserRepository instance;

    private InMemoryUserRepository() {
        super();
    }

    public static synchronized InMemoryUserRepository getInstance() {
        if (instance == null) {
            instance = new InMemoryUserRepository();
        }
        return instance;
    }

    @Override
    protected String getEntityId(User entity) {
        return entity.getId();
    }
}
