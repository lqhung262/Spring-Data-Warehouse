package com.example.demo.dto.humanresource.EmployeeDecision;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeDecisionResponse {
    Long employeeDecisionId;
    Long employeeId;
    String sourceId;
    LocalDateTime decisionDate;
    Long departmentId;
    Long employeeTypeId;
    Long jobPositionId;
    Long jobTitleId;
    Long jobRankId;
    Long costCategoryLevel1;
    Long costCategoryLevel2;
    Long decisionTypeId;
    Boolean isActive = Boolean.TRUE;
    LocalDateTime effectiveAt;
}
