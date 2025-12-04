package com.example.demo.service.humanresource;

import com.example.demo.dto.BulkOperationResult;
import com.example.demo.dto.humanresource.EducationLevel.EducationLevelRequest;
import com.example.demo.dto.humanresource.EducationLevel.EducationLevelResponse;
import com.example.demo.entity.humanresource.EducationLevel;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.EducationLevelMapper;
import com.example.demo.repository.humanresource.EducationLevelRepository;
import com.example.demo.repository.humanresource.EmployeeEducationRepository;
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
public class EducationLevelService {
    final EducationLevelRepository educationLevelRepository;
    final EducationLevelMapper educationLevelMapper;
    final EmployeeEducationRepository employeeEducationRepository;
    final EntityManager entityManager;

    @Value("${entities.humanresource.educationlevel}")
    private String entityName;

    public EducationLevelResponse createEducationLevel(EducationLevelRequest request) {
        // Check source_id uniqueness for create
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            educationLevelRepository.findBySourceId(request.getSourceId()).ifPresent(b -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        EducationLevel educationLevel = educationLevelMapper.toEducationLevel(request);

        return educationLevelMapper.toEducationLevelResponse(educationLevelRepository.save(educationLevel));
    }


    /**
     * Bulk Upsert với Final Batch Logic, Partial Success Pattern:
     * - Safe Batch: saveAll() - requests không có unique conflicts
     * - Final Batch: save + flush từng request - requests có potential conflicts
     * - Trả về detailed result với success/failure breakdown
     */
    public BulkOperationResult<EducationLevelResponse> bulkUpsertEducationLevels(
            List<EducationLevelRequest> requests) {

        // 1. Define unique field configurations (EducationLevel has 2 unique fields)
        UniqueFieldConfig<EducationLevelRequest> sourceIdConfig =
                new UniqueFieldConfig<>("source_id", EducationLevelRequest::getSourceId);
        UniqueFieldConfig<EducationLevelRequest> codeConfig =
                new UniqueFieldConfig<>("education_level_code", EducationLevelRequest::getEducationLevelCode);
        UniqueFieldConfig<EducationLevelRequest> nameConfig =
                new UniqueFieldConfig<>("name", EducationLevelRequest::getName);

        // 2. Define entity fetchers for each unique field
        Map<String, Function<Set<String>, List<EducationLevel>>> entityFetchers = new HashMap<>();
        entityFetchers.put("source_id", educationLevelRepository::findBySourceIdIn);
        entityFetchers.put("education_level_code", educationLevelRepository::findByEducationLevelCodeIn);
        entityFetchers.put("name", educationLevelRepository::findByNameIn);

        // 2.5. Define entity field extractors
        Map<String, Function<EducationLevel, String>> entityFieldExtractors = new HashMap<>();
        entityFieldExtractors.put("source_id", EducationLevel::getSourceId);
        entityFieldExtractors.put("education_level_code", EducationLevel::getEducationLevelCode);
        entityFieldExtractors.put("name", EducationLevel::getName);

        // 3. Setup unique fields using helper
        UniqueFieldsSetupHelper.UniqueFieldsSetup<EducationLevelRequest> setup =
                UniqueFieldsSetupHelper.buildUniqueFieldsSetup(
                        requests,
                        entityFetchers,
                        entityFieldExtractors,
                        sourceIdConfig,
                        codeConfig,
                        nameConfig
                );

        // 4.  Build bulk upsert config
        BulkUpsertConfig<EducationLevelRequest, EducationLevel, EducationLevelResponse> config =
                BulkUpsertConfig.<EducationLevelRequest, EducationLevel, EducationLevelResponse>builder()
                        .uniqueFieldExtractors(setup.getUniqueFieldExtractors())
                        .existingValuesMaps(setup.getExistingValuesMaps())
                        .entityToResponseMapper(educationLevelMapper::toEducationLevelResponse)
                        .requestToEntityMapper(educationLevelMapper::toEducationLevel)
                        .entityUpdater(educationLevelMapper::updateEducationLevel)
                        .existingEntityFinder(this::findExistingEntityForUpsert)
                        .repositorySaver(educationLevelRepository::saveAll)
                        .repositorySaveAndFlusher(educationLevelRepository::saveAndFlush)
                        .entityManagerClearer(entityManager::clear)
                        .build();

        // 5. Execute bulk upsert
        BulkUpsertProcessor<EducationLevelRequest, EducationLevel, EducationLevelResponse> processor =
                new BulkUpsertProcessor<>(config);

        return processor.execute(requests);
    }

    /**
     * Helper: Find existing entity
     */
    private EducationLevel findExistingEntityForUpsert(EducationLevelRequest request) {
        if (request.getSourceId() != null && !request.getSourceId().trim().isEmpty()) {
            Optional<EducationLevel> bySourceId = educationLevelRepository.findBySourceId(request.getSourceId());
            if (bySourceId.isPresent()) {
                return bySourceId.get();
            }
        }

        return null;
    }

    // ========================= BULK DELETE  ========================

    public BulkOperationResult<Long> bulkDeleteEducationLevels(List<Long> ids) {

        // Build config
        BulkDeleteConfig<EducationLevel> config = BulkDeleteConfig.<EducationLevel>builder()
                .entityFinder(id -> educationLevelRepository.findById(id).orElse(null))
                .foreignKeyConstraintsChecker(this::checkForeignKeyConstraints)
                .repositoryDeleter(educationLevelRepository::deleteById)
                .entityName(entityName)
                .build();

        // Execute với processor
        BulkDeleteProcessor<EducationLevel> processor = new BulkDeleteProcessor<>(config);

        return processor.execute(ids);
    }


    public List<EducationLevelResponse> getEducationLevels(Pageable pageable) {
        Page<EducationLevel> page = educationLevelRepository.findAll(pageable);
        return page.getContent()
                .stream().map(educationLevelMapper::toEducationLevelResponse).toList();
    }

    public EducationLevelResponse getEducationLevel(Long id) {
        return educationLevelMapper.toEducationLevelResponse(educationLevelRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public EducationLevelResponse updateEducationLevel(Long id, EducationLevelRequest request) {
        EducationLevel educationLevel = educationLevelRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        // Check source_id uniqueness for update
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            educationLevelRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getEducationLevelId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }

        educationLevelMapper.updateEducationLevel(educationLevel, request);

        return educationLevelMapper.toEducationLevelResponse(educationLevelRepository.save(educationLevel));
    }

    public void deleteEducationLevel(Long id) {
        checkForeignKeyConstraints(id);

        educationLevelRepository.deleteById(id);
    }

    private void checkForeignKeyConstraints(Long id) {
        if (!educationLevelRepository.existsById(id)) {
            throw new NotFoundException(entityName);
        }

        // Check references (RESTRICT strategy)
        long refCount = employeeEducationRepository.countByEducationLevel_EducationLevelId(id);
        if (refCount > 0) {
            throw new CannotDeleteException(
                    "EducationLevel", id, "EmployeeEducation", refCount
            );
        }
    }
}
