package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.EmployeeDecision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeDecisionRepository extends JpaRepository<EmployeeDecision, Long> {
    /**
     * Tối ưu cho Upsert: Tìm tất cả employeeDecisions tồn tại trong 1 câu query.
     */
    List<EmployeeDecision> findByEmployeeDecisionCodeIn(Collection<String> employeeDecisionCodes);

    /**
     * Dùng cho Upsert: Tìm 1 employeeDecision bằng employeeDecisionCode
     */
    Optional<EmployeeDecision> findByEmployeeDecisionId(Long employeeDecisionId);

    Long countByEmployeeDecisionIdIn(Collection<Long> employeeDecisionIds);
}
