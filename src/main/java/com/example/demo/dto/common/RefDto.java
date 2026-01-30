package com.example.demo.dto.common;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * Lightweight reference DTO for related entities.
 * Used in response DTOs to represent related entities with just id and name.
 * 
 * Example: Instead of returning genderId + genderName separately,
 * return a single RefDto for gender with id and name.
 * 
 * This provides a cleaner, more structured API response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RefDto {
    
    Long id;
    String name;
    
    /**
     * Factory method to create RefDto from id and name.
     */
    public static RefDto of(Long id, String name) {
        if (id == null) return null;
        return new RefDto(id, name);
    }
}
