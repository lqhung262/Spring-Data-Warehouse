package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.MaritalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface MaritalStatusRepository extends JpaRepository<MaritalStatus, Long> {
    /**
     * Tối ưu cho Upsert: Tìm tất cả maritalStatus tồn tại trong 1 câu query.
     */
    List<MaritalStatus> findByMaritalStatusCodeIn(Collection<String> maritalStatusCodes);

    /**
     * Dùng cho Upsert: Tìm 1 maritalStatus bằng maritalStatusCode
     */
    Optional<MaritalStatus> findByMaritalStatusCode(String maritalStatusCode);

    Long countByMaritalStatusIdIn(Collection<Long> maritalStatusIds);
}
