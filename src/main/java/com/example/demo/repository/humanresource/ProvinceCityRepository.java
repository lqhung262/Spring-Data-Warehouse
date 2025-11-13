package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.ProvinceCity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProvinceCityRepository extends JpaRepository<ProvinceCity, Long> {
    /**
     * Tối ưu cho Upsert: Tìm tất cả provinceCitys tồn tại trong 1 câu query.
     */
    List<ProvinceCity> findByProvinceCityCodeIn(Collection<String> provinceCityCodes);

    /**
     * Dùng cho Upsert: Tìm 1 provinceCity bằng provinceCityId
     */
    Optional<ProvinceCity> findByProvinceCityId(Long provinceCityId);

    Long countByProvinceCityIdIn(Collection<Long> provinceCityIds);
}
