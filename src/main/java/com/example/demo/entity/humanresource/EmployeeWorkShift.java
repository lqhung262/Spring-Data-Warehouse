package com.example.demo.entity.humanresource;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Objects;

@Entity
@Table(name = "employee_work_shift")
@Data
public class EmployeeWorkShift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_work_shift_id")
    private Long employeeWorkShiftId;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false, unique = true)
    private Employee employee;

    @Column(name = "attendance_code", length = 100)
    private String attendanceCode;
    
    @Column(name = "saturday_full")
    private Boolean saturdayFull = Boolean.FALSE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_shift_id")
    private WorkShift workShift;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_shift_group_id")
    private WorkShiftGroup workShiftGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_type_id")
    private AttendanceType attendanceType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ot_type_id")
    private OtType otType;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeeWorkShift that = (EmployeeWorkShift) o;
        return employeeWorkShiftId != null && Objects.equals(employeeWorkShiftId, that.employeeWorkShiftId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(employeeWorkShiftId);
    }
}
