package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.ProvinceCity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProvinceCityRepository extends JpaRepository<ProvinceCity, Long> {
    Optional<ProvinceCity> findBySourceId(String sourceId);

    /**
     * Tối ưu cho Bulk Upsert: Tìm tất cả provinceCities theo sourceId
     */
    List<ProvinceCity> findBySourceIdIn(Collection<String> sourceIds);

    /**
     * Tối ưu cho Bulk Delete: Tìm tất cả provinceCities theo ID
     */
    List<ProvinceCity> findByProvinceCityIdIn(Collection<Long> provinceCityIds);
}
