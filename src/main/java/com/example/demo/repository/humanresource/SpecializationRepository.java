package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface SpecializationRepository extends JpaRepository<Specialization, Long> {
    Optional<Specialization> findBySourceId(String sourceId);

    Optional<Specialization> findBySpecializationCode(String code);

    Optional<Specialization> findByName(String name);

    // Batch queries for bulk upsert
    List<Specialization> findBySourceIdIn(Collection<String> sourceIds);

    List<Specialization> findByNameIn(Collection<String> names);

    List<Specialization> findBySpecializationCodeIn(Collection<String> codes);
}
