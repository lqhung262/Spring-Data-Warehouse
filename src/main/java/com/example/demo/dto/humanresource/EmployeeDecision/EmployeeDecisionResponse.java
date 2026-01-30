package com.example.demo.dto.humanresource.EmployeeDecision;

import com.example.demo.dto.common.RefDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

/**
 * Response DTO for EmployeeDecision.
 * Uses RefDto for related entities to provide clean, structured data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeDecisionResponse {
    
    Long id;
    String decisionNo;
    LocalDateTime decisionDate;
    Boolean isActive;
    LocalDateTime effectiveAt;
    
    // Related entities as RefDto
    RefDto department;
    RefDto employeeType;
    RefDto jobPosition;
    RefDto jobTitle;
    RefDto jobRank;
    RefDto decisionType;
    RefDto costCategoryLevel1;
    RefDto costCategoryLevel2;
}
