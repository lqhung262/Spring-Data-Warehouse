package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.Nationality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface NationalityRepository extends JpaRepository<Nationality, Long> {
//    /**
//     * Tối ưu cho Upsert: Tìm tất cả nationality tồn tại trong 1 câu query.
//     */
//    List<Nationality> findByNationalityCodeIn(Collection<String> nationalityCodes);
//
//    /**
//     * Dùng cho Upsert: Tìm 1 nationality bằng nationalityCode
//     */
//    Optional<Nationality> findByNationalityCode(String nationalityCode);
//
//    Long countByNationalityIdIn(Collection<Long> nationalityIds);
}
