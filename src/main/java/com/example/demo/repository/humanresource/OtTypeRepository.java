package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.OtType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface OtTypeRepository extends JpaRepository<OtType, Long> {
    /**
     * Tối ưu cho Upsert: Tìm tất cả otTypes tồn tại trong 1 câu query.
     */
    List<OtType> findByOtTypeCodeIn(Collection<String> otTypeCodes);

    /**
     * Dùng cho Upsert: Tìm 1 otType bằng otTypeCode
     */
    Optional<OtType> findByOtTypeCode(String otTypeCode);

    Long countByOtTypeIdIn(Collection<Long> otTypeIds);
}
