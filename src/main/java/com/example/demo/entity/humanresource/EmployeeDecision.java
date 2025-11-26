package com.example.demo.entity.humanresource;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;

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


    @Column(name = "decision_date")
    private LocalDateTime decisionDate;

    @Column(name = "is_active")
    private Boolean isActive = Boolean.TRUE;

    @NotNull
    @Column(name = "effective_at", nullable = false)
    private LocalDateTime effectiveAt;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_type_id")
    private EmployeeType employeeType;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_position_id", nullable = false)
    private JobPosition jobPosition;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_title_id", nullable = false)
    private JobTitle jobTitle;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_rank_id", nullable = false)
    private JobRank jobRank;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "decision_type_id")
    private DecisionType decisionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cost_category_level_1")
    private ExpenseType costCategoryLevel1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cost_category_level_2")
    private ExpenseType costCategoryLevel2;

    @Size(max = 100)
    @NotNull
    @Column(name = "decision_no", nullable = false, length = 100)
    private String decisionNo;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeeDecision that = (EmployeeDecision) o;
        return employeeDecisionId != null && Objects.equals(employeeDecisionId, that.employeeDecisionId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(employeeDecisionId);
    }

    @Override
    public String toString() {
        return "EmployeeDecision(" + employeeDecisionId + ")";
    }
}
