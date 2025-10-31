package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.EmployeeWorkLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeWorkLocationRepository extends JpaRepository<EmployeeWorkLocation, Long> {
}
