package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.WorkLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkLocationRepository extends JpaRepository<WorkLocation, Long> {
}
