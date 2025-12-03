package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.Nationality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface NationalityRepository extends JpaRepository<Nationality, Long> {
    Optional<Nationality> findBySourceId(String sourceId);

    Optional<Nationality> findByNationalityCode(String code);

    Optional<Nationality> findByName(String name);

    // Batch queries for bulk upsert
    List<Nationality> findBySourceIdIn(Collection<String> sourceIds);

    List<Nationality> findByNameIn(Collection<String> names);

    List<Nationality> findByNationalityCodeIn(Collection<String> codes);
}
