package com.example.demo.entity.humanresource;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "employee_education")
@Data
public class EmployeeEducation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_education_id")
    private Long employeeEducationId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "major_id")
    private Long majorId;

    @Column(name = "specialization_id")
    private Long specializationId;

    @Column(name = "education_level_id")
    private Long educationLevelId;

    @Column(name = "school_id")
    private Long schoolId;
}
