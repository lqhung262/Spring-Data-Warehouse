package com.example.demo.service.humanresource;

import com.example.demo.dto.BulkOperationResult;
import com.example.demo.dto.humanresource.Language.LanguageRequest;
import com.example.demo.dto.humanresource.Language.LanguageResponse;
import com.example.demo.entity.humanresource.Language;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.LanguageMapper;
import com.example.demo.repository.humanresource.LanguageRepository;
import com.example.demo.repository.humanresource.EmployeeRepository;
import com.example.demo.exception.CannotDeleteException;
import com.example.demo.util.bulk.*;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LanguageService {
    final LanguageRepository languageRepository;
    final LanguageMapper languageMapper;
    final EmployeeRepository employeeRepository;
    final EntityManager entityManager;

    @Value("${entities.humanresource.language}")
    private String entityName;

    public LanguageResponse createLanguage(LanguageRequest request) {
        // Check source_id uniqueness for create
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            languageRepository.findBySourceId(request.getSourceId()).ifPresent(b -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        Language language = languageMapper.toLanguage(request);

        return languageMapper.toLanguageResponse(languageRepository.save(language));
    }

    /**
     * Bulk Upsert với Final Batch Logic, Partial Success Pattern:
     * - Safe Batch: saveAll() - requests không có unique conflicts
     * - Final Batch: save + flush từng request - requests có potential conflicts
     * - Trả về detailed result với success/failure breakdown
     */
    public BulkOperationResult<LanguageResponse> bulkUpsertLanguages(
            List<LanguageRequest> requests) {

        // 1. Define unique field configurations (Language has 2 unique fields)
        UniqueFieldConfig<LanguageRequest> sourceIdConfig =
                new UniqueFieldConfig<>("source_id", LanguageRequest::getSourceId);
        UniqueFieldConfig<LanguageRequest> codeConfig =
                new UniqueFieldConfig<>("language_code", LanguageRequest::getLanguageCode);
        UniqueFieldConfig<LanguageRequest> nameConfig =
                new UniqueFieldConfig<>("name", LanguageRequest::getName);

        // 2. Define entity fetchers for each unique field
        Map<String, Function<Set<String>, List<Language>>> entityFetchers = new HashMap<>();
        entityFetchers.put("source_id", languageRepository::findBySourceIdIn);
        entityFetchers.put("language_code", languageRepository::findByLanguageCodeIn);
        entityFetchers.put("name", languageRepository::findByNameIn);

        // 2.5. Define entity field extractors
        Map<String, Function<Language, String>> entityFieldExtractors = new HashMap<>();
        entityFieldExtractors.put("source_id", Language::getSourceId);
        entityFieldExtractors.put("language_code", Language::getLanguageCode);
        entityFieldExtractors.put("name", Language::getName);

        // 3. Setup unique fields using helper
        UniqueFieldsSetupHelper.UniqueFieldsSetup<LanguageRequest> setup =
                UniqueFieldsSetupHelper.buildUniqueFieldsSetup(
                        requests,
                        entityFetchers,
                        entityFieldExtractors,
                        sourceIdConfig,
                        codeConfig,
                        nameConfig
                );

        // 4.  Build bulk upsert config
        BulkUpsertConfig<LanguageRequest, Language, LanguageResponse> config =
                BulkUpsertConfig.<LanguageRequest, Language, LanguageResponse>builder()
                        .uniqueFieldExtractors(setup.getUniqueFieldExtractors())
                        .existingValuesMaps(setup.getExistingValuesMaps())
                        .entityToResponseMapper(languageMapper::toLanguageResponse)
                        .requestToEntityMapper(languageMapper::toLanguage)
                        .entityUpdater(languageMapper::updateLanguage)
                        .existingEntityFinder(this::findExistingEntityForUpsert)
                        .repositorySaver(languageRepository::saveAll)
                        .repositorySaveAndFlusher(languageRepository::saveAndFlush)
                        .entityManagerClearer(entityManager::clear)
                        .build();

        // 5. Execute bulk upsert
        BulkUpsertProcessor<LanguageRequest, Language, LanguageResponse> processor =
                new BulkUpsertProcessor<>(config);

        return processor.execute(requests);
    }

    /**
     * Helper: Find existing entity
     */
    private Language findExistingEntityForUpsert(LanguageRequest request) {
        if (request.getSourceId() != null && !request.getSourceId().trim().isEmpty()) {
            Optional<Language> bySourceId = languageRepository.findBySourceId(request.getSourceId());
            if (bySourceId.isPresent()) {
                return bySourceId.get();
            }
        }

        return null;
    }

    // ========================= BULK DELETE  ========================

    public BulkOperationResult<Long> bulkDeleteLanguages(List<Long> ids) {

        // Build config
        BulkDeleteConfig<Language> config = BulkDeleteConfig.<Language>builder()
                .entityFinder(id -> languageRepository.findById(id).orElse(null))
                .foreignKeyConstraintsChecker(this::checkForeignKeyConstraints)
                .repositoryDeleter(languageRepository::deleteById)
                .entityName(entityName)
                .build();

        // Execute với processor
        BulkDeleteProcessor<Language> processor = new BulkDeleteProcessor<>(config);

        return processor.execute(ids);
    }


    public List<LanguageResponse> getLanguages(Pageable pageable) {
        Page<Language> page = languageRepository.findAll(pageable);
        return page.getContent()
                .stream().map(languageMapper::toLanguageResponse).toList();
    }

    public LanguageResponse getLanguage(Long id) {
        return languageMapper.toLanguageResponse(languageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public LanguageResponse updateLanguage(Long id, LanguageRequest request) {
        Language language = languageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        // Check source_id uniqueness for update
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            languageRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getLanguageId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }

        languageMapper.updateLanguage(language, request);

        return languageMapper.toLanguageResponse(languageRepository.save(language));
    }

    public void deleteLanguage(Long id) {
        checkForeignKeyConstraints(id);

        languageRepository.deleteById(id);
    }

    private void checkForeignKeyConstraints(Long id) {
        if (!languageRepository.existsById(id)) {
            throw new NotFoundException(entityName);
        }

        // Check references (RESTRICT strategy) - Language has 3 FK references
        long lang1Count = employeeRepository.countByLanguage1_LanguageId(id);
        long lang2Count = employeeRepository.countByLanguage2_LanguageId(id);
        long lang3Count = employeeRepository.countByLanguage3_LanguageId(id);
        long totalCount = lang1Count + lang2Count + lang3Count;

        if (totalCount > 0) {
            throw new CannotDeleteException(
                    "Language", id, "Employee", totalCount
            );
        }
    }
}
