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
     * @param requests             List of requests
     * @param fieldConfigs         Varargs of UniqueFieldConfig
     * @param entityFetchers       Map of (fieldName -> fetcher function)
     * @param entityValueExtractor Function to extract value from entity (for any unique field)
     * @return UniqueFieldsSetup with extractors and existing values maps
     */
    @SafeVarargs
    public static <TRequest, TEntity> UniqueFieldsSetup<TRequest> buildUniqueFieldsSetup(
            List<TRequest> requests,
            Map<String, Function<Set<String>, List<TEntity>>> entityFetchers,
            Function<TEntity, String> entityValueExtractor,
            UniqueFieldConfig<TRequest>... fieldConfigs) {

        // 1. Build extractors map
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

            // Extract existing values or use empty set
            Set<String> existingValues = extractExistingValues(
                    fieldName,
                    requestValues,
                    entityFetchers,
                    entityValueExtractor
            );

            existingValuesMaps.put(fieldName, existingValues);
        }

        return new UniqueFieldsSetup<>(uniqueFieldExtractors, existingValuesMaps);
    }

    /**
     * EXTRACTED METHOD: Extract existing values for a single field
     * Hàm tìm các giá trị đã tồn tại trong cơ sở dữ liệu cho một trường duy nhất
     */
    private static <TEntity> Set<String> extractExistingValues(
            String fieldName,
            Set<String> requestValues,
            Map<String, Function<Set<String>, List<TEntity>>> entityFetchers,
            Function<TEntity, String> entityValueExtractor) {

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

        // Case 3: Fetch and extract values
        List<TEntity> existingEntities = fetcher.apply(requestValues);
        return existingEntities.stream()
                .map(entityValueExtractor)
                .collect(Collectors.toSet());
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