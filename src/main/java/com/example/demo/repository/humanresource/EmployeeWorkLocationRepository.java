package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.EmployeeWorkLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeWorkLocationRepository extends JpaRepository<EmployeeWorkLocation, Long> {
    /**
     * Tối ưu cho Upsert: Tìm tất cả employeeWorkLocations tồn tại trong 1 câu query.
     */
    List<EmployeeWorkLocation> findByEmployeeWorkLocationCodeIn(Collection<String> employeeWorkLocationCodes);

    /**
     * Dùng cho Upsert: Tìm 1 employeeWorkLocation bằng employeeWorkLocationCode
     */
    Optional<EmployeeWorkLocation> findByEmployeeWorkLocationId(Long employeeWorkLocationId);

    Long countByEmployeeWorkLocationIdIn(Collection<Long> employeeWorkLocationIds);
}
