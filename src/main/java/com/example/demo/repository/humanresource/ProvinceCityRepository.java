package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.ProvinceCity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProvinceCityRepository extends JpaRepository<ProvinceCity, Long> {
    // Single queries
    Optional<ProvinceCity> findBySourceId(String sourceId);

    Optional<ProvinceCity> findByName(String name);

    // Batch queries for bulk upsert
    List<ProvinceCity> findBySourceIdIn(Collection<String> sourceIds);

    List<ProvinceCity> findByNameIn(Collection<String> names);

    // Batch queries for bulk delete
    List<ProvinceCity> findByProvinceCityIdIn(Collection<Long> provinceCityIds);

    long countByProvinceCityIdIn(Collection<Long> provinceCityIds);
}
