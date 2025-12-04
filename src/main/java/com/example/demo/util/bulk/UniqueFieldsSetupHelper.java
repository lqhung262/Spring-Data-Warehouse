package com.example.demo.util.bulk;

import com.example.demo.util.BulkOperationUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Helper class to set up unique fields configuration for bulk operations
 * ELIMINATES CODE DUPLICATION and SonarQube warnings
 */
@Slf4j
public class UniqueFieldsSetupHelper {

    private UniqueFieldsSetupHelper() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Build complete unique fields configuration from field configs
     *
     * @param requests              List of requests
     * @param entityFetchers        Map of (fieldName -> fetcher function)
     * @param entityFieldExtractors Map of (fieldName -> entity value extractor function)
     * @param fieldConfigs          Varargs of UniqueFieldConfig
     * @return UniqueFieldsSetup with extractors and existing values maps
     */
    @SafeVarargs
    public static <TRequest, TEntity> UniqueFieldsSetup<TRequest> buildUniqueFieldsSetup(
            List<TRequest> requests,
            Map<String, Function<Set<String>, List<TEntity>>> entityFetchers,
            Map<String, Function<TEntity, String>> entityFieldExtractors,
            UniqueFieldConfig<TRequest>... fieldConfigs) {

        // 1. Build extractors map (for requests)
        Map<String, Function<TRequest, String>> uniqueFieldExtractors = new LinkedHashMap<>();
        for (UniqueFieldConfig<TRequest> config : fieldConfigs) {
            uniqueFieldExtractors.put(config.getFieldName(), config.getValueExtractor());
        }

        // 2.  Extract unique values from requests
        Map<String, Set<String>> requestValuesMaps = new HashMap<>();
        for (UniqueFieldConfig<TRequest> config : fieldConfigs) {
            Set<String> values = BulkOperationUtils.extractUniqueValues(
                    requests, config.getValueExtractor());
            requestValuesMaps.put(config.getFieldName(), values);
        }

        // 3.  Fetch existing values from DB
        Map<String, Set<String>> existingValuesMaps = new HashMap<>();

        for (UniqueFieldConfig<TRequest> config : fieldConfigs) {
            String fieldName = config.getFieldName();
            Set<String> requestValues = requestValuesMaps.get(fieldName);

            // Extract existing values with proper field extractor
            Set<String> existingValues = extractExistingValues(
                    fieldName,
                    requestValues,
                    entityFetchers,
                    entityFieldExtractors
            );

            existingValuesMaps.put(fieldName, existingValues);
        }

        return new UniqueFieldsSetup<>(uniqueFieldExtractors, existingValuesMaps);
    }

    /**
     * EXTRACTED METHOD: Extract existing values for a single field
     * <p>
     * PROPER LOGIC:
     * 1. Fetch entities from DB using the fetcher (e.g., findByCodeIn(["MS2", "MS3"]))
     * 2. Extract the field values from returned entities using the correct extractor
     * 3. Return the Set of values that actually exist in DB
     * <p>
     * Example:
     * - fieldName = "marital_status_code"
     * - requestValues = {"MS2", "MS3"}
     * - fetcher = findByMaritalStatusCodeIn({"MS2", "MS3"}) returns [entity with code="MS2"]
     * - entityFieldExtractor = MaritalStatus::getMaritalStatusCode
     * - Extract "MS2" from entity
     * - Return {"MS2"} (only MS2 exists, MS3 doesn't)
     */
    private static <TEntity> Set<String> extractExistingValues(
            String fieldName,
            Set<String> requestValues,
            Map<String, Function<Set<String>, List<TEntity>>> entityFetchers,
            Map<String, Function<TEntity, String>> entityFieldExtractors) {

        // Case 1: No request values
        if (requestValues.isEmpty()) {
            return Collections.emptySet();
        }

        // Case 2: No fetcher configured
        Function<Set<String>, List<TEntity>> fetcher = entityFetchers.get(fieldName);
        if (fetcher == null) {
            log.warn("No fetcher found for field: {}", fieldName);
            return Collections.emptySet();
        }

        // Case 3: No entity field extractor
        Function<TEntity, String> entityExtractor = entityFieldExtractors.get(fieldName);
        if (entityExtractor == null) {
            log.warn("No entity field extractor found for field: {}", fieldName);
            return Collections.emptySet();
        }

        // Case 4: Fetch entities and extract field values
        List<TEntity> existingEntities = fetcher.apply(requestValues);

        if (existingEntities.isEmpty()) {
            log.debug("No existing entities found for field '{}'", fieldName);
            return Collections.emptySet();
        }

        // Extract the actual field values from entities
        Set<String> existingValues = existingEntities.stream()
                .map(entityExtractor)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        log.debug("Found {} existing values for field '{}': {}",
                existingValues.size(), fieldName, existingValues);

        return existingValues;
    }

    /**
     * Result class containing setup data
     */
    @Getter
    public static class UniqueFieldsSetup<TRequest> {
        private final Map<String, Function<TRequest, String>> uniqueFieldExtractors;
        private final Map<String, Set<String>> existingValuesMaps;

        public UniqueFieldsSetup(
                Map<String, Function<TRequest, String>> uniqueFieldExtractors,
                Map<String, Set<String>> existingValuesMaps) {
            this.uniqueFieldExtractors = uniqueFieldExtractors;
            this.existingValuesMaps = existingValuesMaps;
        }

    }
}