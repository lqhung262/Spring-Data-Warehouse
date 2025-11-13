package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.LaborStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface LaborStatusRepository extends JpaRepository<LaborStatus, Long> {
    /**
     * Tối ưu cho Upsert: Tìm tất cả laborStatus tồn tại trong 1 câu query.
     */
    List<LaborStatus> findByLaborStatusCodeIn(Collection<String> laborStatusCodes);

    /**
     * Dùng cho Upsert: Tìm 1 laborStatus bằng laborStatusCode
     */
    Optional<LaborStatus> findByLaborStatusCode(String laborStatusCode);

    Long countByLaborStatusIdIn(Collection<Long> laborStatusIds);
}
