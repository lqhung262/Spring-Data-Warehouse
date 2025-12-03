package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.Ward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface WardRepository extends JpaRepository<Ward, Long> {
    Optional<Ward> findBySourceId(String sourceId);

    Optional<Ward> findByName(String name);

    // Batch queries for bulk upsert
    List<Ward> findBySourceIdIn(Collection<String> sourceIds);

    List<Ward> findByNameIn(Collection<String> names);


    // For cascade delete checks - count references from Employee and OldWard
    long countByProvinceCity_ProvinceCityId(Long provinceCityId);

    /**
     * Batch count: Đếm ward cho nhiều province_city_ids cùng lúc
     */
    @Query("SELECT w. provinceCity.provinceCityId, COUNT(w) FROM Ward w " +
            "WHERE w.provinceCity.provinceCityId IN :provinceCityIds " +
            "GROUP BY w.provinceCity. provinceCityId")
    List<Object[]> countByProvinceCityIdIn(@Param("provinceCityIds") List<Long> provinceCityIds);

}
