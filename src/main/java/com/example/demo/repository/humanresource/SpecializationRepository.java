package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface SpecializationRepository extends JpaRepository<Specialization, Long> {
    /**
     * Tối ưu cho Upsert: Tìm tất cả specializations tồn tại trong 1 câu query.
     */
    List<Specialization> findBySpecializationCodeIn(Collection<String> specializationCodes);

    /**
     * Dùng cho Upsert: Tìm 1 specialization bằng specializationCode
     */
    Optional<Specialization> findBySpecializationCode(String specializationCode);

    Long countBySpecializationIdIn(Collection<Long> specializationIds);
}
