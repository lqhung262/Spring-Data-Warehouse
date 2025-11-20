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

//    /**
//     * Tối ưu cho Upsert: Tìm tất cả languages tồn tại trong 1 câu query.
//     */
//    List<Language> findByLanguageCodeIn(Collection<String> languageCodes);
//
//    /**
//     * Dùng cho Upsert: Tìm 1 language bằng languageCode
//     */
//    Optional<Language> findByLanguageCode(String languageCode);
//
//    Long countByLanguageIdIn(Collection<Long> languageIds);
}
