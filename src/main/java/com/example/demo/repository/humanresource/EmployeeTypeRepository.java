package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.EmployeeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeTypeRepository extends JpaRepository<EmployeeType, Long> {
    /**
     * Tối ưu cho Upsert: Tìm tất cả employeeTypes tồn tại trong 1 câu query.
     */
    List<EmployeeType> findByEmployeeTypeCodeIn(Collection<String> employeeTypeCodes);

    /**
     * Dùng cho Upsert: Tìm 1 employeeType bằng employeeTypeCode
     */
    Optional<EmployeeType> findByEmployeeTypeCode(String employeeTypeCode);

    Long countByEmployeeTypeIdIn(Collection<Long> employeeTypeIds);

}
