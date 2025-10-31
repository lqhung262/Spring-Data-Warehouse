package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.BloodGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BloodGroupRepository extends JpaRepository<BloodGroup, Long> {
}
