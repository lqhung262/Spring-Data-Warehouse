package com.example.demo.entity.humanresource;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "employee_decision")
@Data
public class EmployeeDecision {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_decision_id")
    private Long employeeDecisionId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @NotNull
    @Column(name = "decision_no", nullable = false, unique = true, length = 100)
    private String decisionNo;

    @Column(name = "decision_date")
    private LocalDateTime decisionDate;

    @NotNull
    @Column(name = "department_id", nullable = false)
    private Long departmentId;

    @Column(name = "employee_type_id")
    private Long employeeTypeId;

    @NotNull
    @Column(name = "job_position_id", nullable = false)
    private Long jobPositionId;

    @NotNull
    @Column(name = "job_title_id", nullable = false)
    private Long jobTitleId;

    @NotNull
    @Column(name = "job_rank_id", nullable = false)
    private Long jobRankId;

    @Column(name = "cost_category_level_1")
    private Long costCategoryLevel1;

    @Column(name = "cost_category_level_2")
    private Long costCategoryLevel2;

    @Column(name = "decision_type_id")
    private Long decisionTypeId;

    @Column(name = "is_active")
    private Boolean isActive = Boolean.TRUE;

    @NotNull
    @Column(name = "effective_at", nullable = false)
    private LocalDateTime effectiveAt;
}
