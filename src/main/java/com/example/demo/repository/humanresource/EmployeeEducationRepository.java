package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.EmployeeEducation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeEducationRepository extends JpaRepository<EmployeeEducation, Long> {
    /**
     * Tối ưu cho Upsert: Tìm tất cả employeeEducations tồn tại trong 1 câu query.
     */
    List<EmployeeEducation> findByEmployeeEducationCodeIn(Collection<String> employeeEducationCodes);

    /**
     * Dùng cho Upsert: Tìm 1 employeeEducation bằng employeeEducationCode
     */
    Optional<EmployeeEducation> findByEmployeeEducationId(Long employeeEducationId);

    Long countByEmployeeEducationIdIn(Collection<Long> employeeEducationIds);
}
