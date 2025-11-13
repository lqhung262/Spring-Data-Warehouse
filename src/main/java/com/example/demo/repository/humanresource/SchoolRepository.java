package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface SchoolRepository extends JpaRepository<School, Long> {
    /**
     * Tối ưu cho Upsert: Tìm tất cả schools tồn tại trong 1 câu query.
     */
    List<School> findBySchoolCodeIn(Collection<String> schoolCodes);

    /**
     * Dùng cho Upsert: Tìm 1 school bằng schoolCode
     */
    Optional<School> findBySchoolCode(String schoolCode);

    Long countBySchoolIdIn(Collection<Long> schoolIds);
}
