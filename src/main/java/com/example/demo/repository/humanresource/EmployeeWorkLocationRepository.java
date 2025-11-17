package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.EmployeeWorkLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeWorkLocationRepository extends JpaRepository<EmployeeWorkLocation, Long> {
    Optional<EmployeeWorkLocation> findByEmployee_IdAndWorkLocationId(Long employeeId, Long workLocationId);

    List<EmployeeWorkLocation> findByEmployee_Id(Long employeeId);
}
