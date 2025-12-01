package com.example.demo.util.bulk;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Configuration for bulk upsert operations
 * Reusable cho tất cả entities
 */
@Getter
@Builder
public class BulkUpsertConfig<TRequest, TEntity, TResponse> {
    /**
     * Map<fieldName, extractor function> để extract unique fields từ request
     */
    private final Map<String, Function<TRequest, String>> uniqueFieldExtractors;

    /**
     * Map<fieldName, existing values from DB> để check conflicts
     */
    private final Map<String, Set<String>> existingValuesMaps;

    /**
     * Function để map entity sang response DTO
     */
    private final Function<TEntity, TResponse> entityToResponseMapper;

    /**
     * Function để map request sang new entity (for CREATE)
     */
    private final Function<TRequest, TEntity> requestToEntityMapper;

    /**
     * BiConsumer để update existing entity từ request (for UPDATE)
     */
    private final EntityUpdater<TRequest, TEntity> entityUpdater;

    /**
     * Function để find existing entity by request (check by unique fields)
     */
    private final Function<TRequest, TEntity> existingEntityFinder;

    /**
     * Repository saveAll function (batch operation)
     * Input: List<TEntity>, Output: Iterable<TEntity>
     */
    private final RepositorySaveAll<TEntity> repositorySaver;

    /**
     * Repository saveAndFlush function (individual save)
     * Input: TEntity, Output: TEntity
     */
    private final RepositorySave<TEntity> repositorySaveAndFlusher;

    /**
     * Entity manager clear function (for memory management)
     */
    private final Runnable entityManagerClearer;

    /**
     * Functional interface for entity update
     */
    @FunctionalInterface
    public interface EntityUpdater<TRequest, TEntity> {
        void update(TEntity entity, TRequest request);
    }

    /**
     * Functional interface for repository saveAll (batch)
     */
    @FunctionalInterface
    public interface RepositorySaveAll<TEntity> {
        Iterable<TEntity> saveAll(Iterable<TEntity> entities);
    }

    /**
     * Functional interface for repository save (individual)
     */
    @FunctionalInterface
    public interface RepositorySave<TEntity> {
        TEntity save(TEntity entity);
    }
}