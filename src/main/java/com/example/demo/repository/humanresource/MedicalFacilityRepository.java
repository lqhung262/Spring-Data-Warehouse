package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.MedicalFacility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface MedicalFacilityRepository extends JpaRepository<MedicalFacility, Long> {
    /**
     * Tối ưu cho Upsert: Tìm tất cả medicalFacilitys tồn tại trong 1 câu query.
     */
    List<MedicalFacility> findByMedicalFacilityCodeIn(Collection<String> medicalFacilityCodes);

    /**
     * Dùng cho Upsert: Tìm 1 medicalFacility bằng medicalFacilityCode
     */
    Optional<MedicalFacility> findByMedicalFacilityCode(String medicalFacilityCode);

    Long countByMedicalFacilityIdIn(Collection<Long> medicalFacilityIds);
}
