package com.example.demo.mapper.common;

import org.mapstruct.MapperConfig;
import org.mapstruct.MappingInheritanceStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * Shared MapStruct configuration for all mappers.
 * 
 * Centralizes common settings:
 * - Null value handling strategy: IGNORE (don't overwrite with null)
 * - Component model: Spring (for dependency injection)
 * - Unmapped target policy: IGNORE (reduce boilerplate @Mapping(ignore=true))
 * 
 * Usage in mappers:
 * @Mapper(config = CommonMapperConfig.class)
 */
@MapperConfig(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    mappingInheritanceStrategy = MappingInheritanceStrategy.AUTO_INHERIT_FROM_CONFIG
)
public interface CommonMapperConfig {
    // This interface serves as a configuration container
    // All settings are defined via annotations
}
