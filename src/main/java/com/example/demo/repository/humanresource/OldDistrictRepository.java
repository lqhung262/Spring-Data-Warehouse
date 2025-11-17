package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.OldDistrict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface OldDistrictRepository extends JpaRepository<OldDistrict, Long> {
//    /**
//     * Tối ưu cho Upsert: Tìm tất cả oldDistricts tồn tại trong 1 câu query.
//     */
////    List<OldDistrict> findByOldDistrictCodeIn(Collection<String> oldDistrictCodes);
//
//    /**
//     * Dùng cho Upsert: Tìm 1 oldDistrict bằng oldDistrictCode
//     */
//    Optional<OldDistrict> findByOldDistrictId(Long oldDistrictId);
//
//    Long countByOldDistrictIdIn(Collection<Long> oldDistrictIds);
}
