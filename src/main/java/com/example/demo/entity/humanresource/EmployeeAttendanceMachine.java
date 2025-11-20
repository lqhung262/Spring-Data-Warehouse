package com.example.demo.entity.humanresource;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Objects;

@Entity
@Table(name = "employee_attendance_machine")
@Data
public class EmployeeAttendanceMachine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_attendance_machine_id")
    private Long employeeAttendanceMachineId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @NotNull
    @Column(name = "machine_id", nullable = false)
    private Long machineId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeeAttendanceMachine that = (EmployeeAttendanceMachine) o;
        return employeeAttendanceMachineId != null && Objects.equals(employeeAttendanceMachineId, that.employeeAttendanceMachineId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(employeeAttendanceMachineId);
    }
}
