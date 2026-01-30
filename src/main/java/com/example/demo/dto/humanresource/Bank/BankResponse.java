package com.example.demo.dto.humanresource.Bank;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * Response DTO for Bank.
 * 
 * NOTE: Excludes internal fields (sourceId, sourceSystemId, audit fields)
 * that are not relevant for API consumers.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BankResponse {
    
    Long id;
    String code;
    String name;
    String shortName;
}
