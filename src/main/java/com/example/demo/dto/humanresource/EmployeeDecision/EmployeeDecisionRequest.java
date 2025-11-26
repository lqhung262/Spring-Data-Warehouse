package com.example.demo.dto.humanresource.EmployeeDecision;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeDecisionRequest {
    @NotBlank
    private String decisionNo;

    private LocalDateTime decisionDate;

    @NotNull
    private Long departmentId;

    private Long employeeTypeId;

    @NotNull
    private Long jobPositionId;

    @NotNull
    private Long jobTitleId;

    @NotNull
    private Long jobRankId;

    private Long costCategoryLevel1;

    private Long costCategoryLevel2;

    private Long decisionTypeId;

    private Boolean isActive = Boolean.TRUE;

    @NotNull
    private LocalDateTime effectiveAt;

}
