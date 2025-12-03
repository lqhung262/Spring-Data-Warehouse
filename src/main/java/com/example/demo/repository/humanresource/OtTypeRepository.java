package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.OtType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface OtTypeRepository extends JpaRepository<OtType, Long> {
    Optional<OtType> findBySourceId(String sourceId);

    Optional<OtType> findByOtTypeCode(String code);

    Optional<OtType> findByName(String name);

    // Batch queries for bulk upsert
    List<OtType> findBySourceIdIn(Collection<String> sourceIds);

    List<OtType> findByNameIn(Collection<String> names);

    List<OtType> findByOtTypeCodeIn(Collection<String> codes);
}
