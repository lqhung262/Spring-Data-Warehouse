package com.example.demo.dto.common;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

/**
 * Base class for simple catalog/lookup table response DTOs.
 * Contains code and name fields - the essential business information.
 * 
 * Use this for entities like Bank, Department, School, etc.
 * Excludes internal fields like sourceId, sourceSystemId, audit fields.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class BaseCatalogResponse extends BaseResponse {
    
    String code;
    String name;
}
