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
    @NotNull
    private Long employeeId;

    @NotBlank
    private String sourceId;

    @NotNull
    private LocalDateTime decisionDate;

    @NotNull
    private Long departmentId;

    @NotNull
    private Long employeeTypeId;

    @NotNull
    private Long jobPositionId;

    @NotNull
    private Long jobTitleId;

    @NotNull
    private Long jobRankId;

    @NotNull
    private Long costCategoryLevel1;

    @NotNull
    private Long costCategoryLevel2;

    @NotNull
    private Long decisionTypeId;

    @NotNull
    private Boolean isActive = Boolean.TRUE;

    @NotNull
    private LocalDateTime effectiveAt;

}
