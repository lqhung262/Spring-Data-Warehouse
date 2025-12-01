package com.example.demo.util.bulk;

import lombok.Builder;
import lombok.Getter;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Configuration for bulk delete operations
 * Reusable cho tất cả entities
 */
@Getter
@Builder
public class BulkDeleteConfig<TEntity> {
    /**
     * Function để find entity by ID
     */
    private final Function<Long, TEntity> entityFinder;

    /**
     * Function để check FK constraints (throw exception nếu có references)
     */
    private final Consumer<Long> foreignKeyConstraintsChecker;

    /**
     * Repository delete function
     */
    private final Consumer<Long> repositoryDeleter;

    /**
     * Entity name for error messages
     */
    private final String entityName;
}