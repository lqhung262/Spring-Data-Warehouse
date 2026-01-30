package com.example.demo.mapper.common;

import com.example.demo.dto.common.RefDto;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Utility class for common mapping operations.
 * 
 * Provides helper methods to reduce boilerplate in mappers:
 * - Creating RefDto from entity relationships
 * - Setting entity references from IDs
 */
public final class MapperUtils {
    
    private MapperUtils() {
        // Utility class - no instantiation
    }
    
    /**
     * Creates a RefDto from an entity's id and name.
     * Returns null if the id is null.
     * 
     * @param id The entity id
     * @param name The entity name/display value
     * @return RefDto or null
     */
    public static RefDto toRef(Long id, String name) {
        return RefDto.of(id, name);
    }
    
    /**
     * Sets an entity reference if the ID is not null.
     * 
     * Usage example:
     * setRefIfPresent(request.getGenderId(), Gender::new, Gender::setGenderId, employee::setGender);
     * 
     * @param id The ID from the request
     * @param entitySupplier Supplier to create new entity instance
     * @param idSetter Consumer to set the ID on the entity
     * @param targetSetter Consumer to set the entity on the target object
     * @param <T> The entity type
     */
    public static <T> void setRefIfPresent(Long id, 
                                           Supplier<T> entitySupplier, 
                                           java.util.function.BiConsumer<T, Long> idSetter,
                                           Consumer<T> targetSetter) {
        if (id != null) {
            T entity = entitySupplier.get();
            idSetter.accept(entity, id);
            targetSetter.accept(entity);
        }
    }
}
