package repositories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Abstract Base Class for In-Memory Repositories.
 * 
 * CODE DUPLICATION FIX (DRY Principle):
 * Previously, every repository duplicated the exact same HashMap logic. By extracting this 
 * into a generic base class, we remove all duplication. Concrete repositories simply extend 
 * this class and provide their specific Entity and ID types.
 */
public abstract class InMemoryCrudRepository<T, ID> implements CrudRepository<T, ID> {
    protected final Map<ID, T> database = new HashMap<>();

    // Abstract method to extract ID from the entity, required for generic saving/updating
    protected abstract ID getEntityId(T entity);

    @Override
    public void save(T entity) {
        database.put(getEntityId(entity), entity);
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(database.get(id));
    }

    @Override
    public void update(T entity) {
        ID id = getEntityId(entity);
        if (database.containsKey(id)) {
            database.put(id, entity);
        }
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(database.values());
    }
}
