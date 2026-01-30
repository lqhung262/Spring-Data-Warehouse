package com.example.demo.dto.common;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

/**
 * Base class for all response DTOs.
 * Contains only the internal ID for reference.
 * 
 * NOTE: Intentionally excludes internal audit fields (createdAt, updatedAt,
 * createdBy, updatedBy, isDeleted, sourceId, sourceSystemId) as these are
 * not relevant for API consumers.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class BaseResponse {
    
    Long id;
}
