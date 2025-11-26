package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.OldProvinceCity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface OldProvinceCityRepository extends JpaRepository<OldProvinceCity, Long> {
    Optional<OldProvinceCity> findBySourceId(String sourceId);

    // Count methods for cascade delete checks
    long countByProvinceCity_ProvinceCityId(Long provinceCityId);

//    /**
//     * Tối ưu cho Upsert: Tìm tất cả oldProvinceCity tồn tại trong 1 câu query.
//     */
////    List<OldProvinceCity> findByOldProvinceCityCodeIn(Collection<String> oldProvinceCityCodes);
//
//    /**
//     * Dùng cho Upsert: Tìm 1 oldProvinceCity bằng oldProvinceCityCode
//     */
//    Optional<OldProvinceCity> findByOldProvinceCityId(Long oldProvinceCityId);
//
//    Long countByOldProvinceCityIdIn(Collection<Long> oldProvinceCityIds);
}
