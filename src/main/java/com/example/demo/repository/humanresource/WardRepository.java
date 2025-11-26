package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.Ward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WardRepository extends JpaRepository<Ward, Long> {
    Optional<Ward> findBySourceId(String sourceId);

    // For cascade delete checks - count references from Employee and OldWard
    long countByProvinceCity_ProvinceCityId(Long provinceCityId);
}
