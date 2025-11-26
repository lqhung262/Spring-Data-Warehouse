package com.example.demo.entity.humanresource;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "employee_education")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class EmployeeEducation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_education_id")
    private Long employeeEducationId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    @ToString.Exclude
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "major_id")
    @ToString.Exclude
    private Major major;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialization_id")
    @ToString.Exclude
    private Specialization specialization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "education_level_id")
    @ToString.Exclude
    private EducationLevel educationLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id")
    @ToString.Exclude
    private School school;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeeEducation that = (EmployeeEducation) o;
        return employeeEducationId != null && Objects.equals(employeeEducationId, that.employeeEducationId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(employeeEducationId);
    }
}
