package com.example.demo.entity.humanresource;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Objects;

@Entity
@Table(name = "employee_work_location")
@Data
public class EmployeeWorkLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_work_location_id")
    private Long employeeWorkLocationId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @NotNull
    @Column(name = "work_location_id", nullable = false)
    private Long workLocationId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeeWorkLocation that = (EmployeeWorkLocation) o;
        return employeeWorkLocationId != null && Objects.equals(employeeWorkLocationId, that.employeeWorkLocationId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(employeeWorkLocationId);
    }
}
