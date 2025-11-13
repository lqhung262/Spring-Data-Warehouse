package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.BloodGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface BloodGroupRepository extends JpaRepository<BloodGroup, Long> {
    /**
     * Tối ưu cho Upsert: Tìm tất cả bloodGroups tồn tại trong 1 câu query.
     */
    List<BloodGroup> findByBloodGroupCodeIn(Collection<String> bloodGroupCodes);

    /**
     * Dùng cho Upsert: Tìm 1 bloodGroup bằng bloodGroupCode
     */
    Optional<BloodGroup> findByBloodGroupCode(String bloodGroupCode);

    Long countByBloodGroupIdIn(Collection<Long> bloodGroupIds);
}
