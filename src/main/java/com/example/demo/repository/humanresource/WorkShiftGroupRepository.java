package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.WorkShiftGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkShiftGroupRepository extends JpaRepository<WorkShiftGroup, Long> {
}
