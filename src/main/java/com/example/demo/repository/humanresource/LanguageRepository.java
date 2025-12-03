package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Long> {
    Optional<Language> findBySourceId(String sourceId);

    Optional<Language> findByLanguageCode(String code);

    Optional<Language> findByName(String name);

    // Batch queries for bulk upsert
    List<Language> findBySourceIdIn(Collection<String> sourceIds);

    List<Language> findByNameIn(Collection<String> names);

    List<Language> findByLanguageCodeIn(Collection<String> codes);
}
