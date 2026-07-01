package repositories;

import models.User;

/**
 * Repository interface specific to User entities.
 * Extends the standard CrudRepository contract.
 */
public interface UserRepository extends CrudRepository<User, String> {
    // Domain-specific methods (like findByPhoneNumber) can be added here in the future
}
