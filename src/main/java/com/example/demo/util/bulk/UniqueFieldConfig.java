package com.example.demo.util.bulk;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Function;

/**
 * Configuration for a single unique field
 * Reusable config object
 */
@Getter
@AllArgsConstructor
public class UniqueFieldConfig<TRequest> {
    /**
     * Field name trong DB (e.g., "source_id", "name", "code")
     */
    private final String fieldName;

    /**
     * Function để extract value từ request
     */
    private final Function<TRequest, String> valueExtractor;
}