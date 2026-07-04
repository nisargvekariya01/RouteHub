package repositories;

import java.util.List;
import java.util.Optional;

/**
 * Generic CRUD Repository interface defining the standard operations for any domain entity.
 * 
 * A repository is responsible for storing and retrieving data.
 * 
 * SOLID PRINCIPLES:
 * 1. Dependency Inversion Principle (DIP): High-level modules (Services) will now depend 
 *    on this abstraction rather than concrete implementations (HashMaps).
 * 2. Interface Segregation Principle (ISP): Consumers only see the generic CRUD contract.
 * 
 * <T> is called a generic type parameter, allowing this interface to be used with any entity type.
 */
public interface CrudRepository<T, ID> {
    void save(T entity);
    Optional<T> findById(ID id);
    void update(T entity);
    List<T> findAll();
}
