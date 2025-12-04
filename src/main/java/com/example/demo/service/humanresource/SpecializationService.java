package com.example.demo.service.humanresource;

import com.example.demo.dto.BulkOperationResult;
import com.example.demo.dto.humanresource.Specialization.SpecializationRequest;
import com.example.demo.dto.humanresource.Specialization.SpecializationResponse;
import com.example.demo.entity.humanresource.Specialization;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.SpecializationMapper;
import com.example.demo.repository.humanresource.SpecializationRepository;
import com.example.demo.util.bulk.*;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.demo.repository.humanresource.EmployeeEducationRepository;
import com.example.demo.exception.CannotDeleteException;

import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SpecializationService {
    final SpecializationRepository specializationRepository;
    final SpecializationMapper specializationMapper;
    final EmployeeEducationRepository employeeEducationRepository;
    final EntityManager entityManager;

    @Value("${entities.humanresource.speicialization}")
    private String entityName;

    public SpecializationResponse createSpecialization(SpecializationRequest request) {
        // Check source_id uniqueness for create
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            specializationRepository.findBySourceId(request.getSourceId()).ifPresent(b -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        Specialization specialization = specializationMapper.toSpecialization(request);

        return specializationMapper.toSpecializationResponse(specializationRepository.save(specialization));
    }

    /**
     * Bulk Upsert với Final Batch Logic, Partial Success Pattern:
     * - Safe Batch: saveAll() - requests không có unique conflicts
     * - Final Batch: save + flush từng request - requests có potential conflicts
     * - Trả về detailed result với success/failure breakdown
     */
    public BulkOperationResult<SpecializationResponse> bulkUpsertSpecializations(
            List<SpecializationRequest> requests) {

        // 1. Define unique field configurations (Specialization has 2 unique fields)
        UniqueFieldConfig<SpecializationRequest> sourceIdConfig =
                new UniqueFieldConfig<>("source_id", SpecializationRequest::getSourceId);
        UniqueFieldConfig<SpecializationRequest> codeConfig =
                new UniqueFieldConfig<>("specialization_code", SpecializationRequest::getSpecializationCode);
        UniqueFieldConfig<SpecializationRequest> nameConfig =
                new UniqueFieldConfig<>("name", SpecializationRequest::getName);

        // 2. Define entity fetchers for each unique field
        Map<String, Function<Set<String>, List<Specialization>>> entityFetchers = new HashMap<>();
        entityFetchers.put("source_id", specializationRepository::findBySourceIdIn);
        entityFetchers.put("specialization_code", specializationRepository::findBySpecializationCodeIn);
        entityFetchers.put("name", specializationRepository::findByNameIn);

        // 2.5. Define entity field extractors (to extract values from returned entities)
        Map<String, Function<Specialization, String>> entityFieldExtractors = new HashMap<>();
        entityFieldExtractors.put("source_id", Specialization::getSourceId);
        entityFieldExtractors.put("specialization_code", Specialization::getSpecializationCode);
        entityFieldExtractors.put("name", Specialization::getName);

        // 3. Setup unique fields using helper
        UniqueFieldsSetupHelper.UniqueFieldsSetup<SpecializationRequest> setup =
                UniqueFieldsSetupHelper.buildUniqueFieldsSetup(
                        requests,
                        entityFetchers,
                        entityFieldExtractors,
                        sourceIdConfig,
                        codeConfig,
                        nameConfig
                );

        // 4.  Build bulk upsert config
        BulkUpsertConfig<SpecializationRequest, Specialization, SpecializationResponse> config =
                BulkUpsertConfig.<SpecializationRequest, Specialization, SpecializationResponse>builder()
                        .uniqueFieldExtractors(setup.getUniqueFieldExtractors())
                        .existingValuesMaps(setup.getExistingValuesMaps())
                        .entityToResponseMapper(specializationMapper::toSpecializationResponse)
                        .requestToEntityMapper(specializationMapper::toSpecialization)
                        .entityUpdater(specializationMapper::updateSpecialization)
                        .existingEntityFinder(this::findExistingEntityForUpsert)
                        .repositorySaver(specializationRepository::saveAll)
                        .repositorySaveAndFlusher(specializationRepository::saveAndFlush)
                        .entityManagerClearer(entityManager::clear)
                        .build();

        // 5. Execute bulk upsert
        BulkUpsertProcessor<SpecializationRequest, Specialization, SpecializationResponse> processor =
                new BulkUpsertProcessor<>(config);

        return processor.execute(requests);
    }

    /**
     * Helper: Find existing entity for upsert
     * STRICT LOGIC: Only match by sourceId (primary identifier)
     */
    private Specialization findExistingEntityForUpsert(SpecializationRequest request) {
        // ONLY match by sourceId (canonical identifier)
        if (request.getSourceId() != null && !request.getSourceId().trim().isEmpty()) {
            Optional<Specialization> bySourceId = specializationRepository.findBySourceId(request.getSourceId());
            if (bySourceId.isPresent()) {
                return bySourceId.get();
            }
        }

        // Do NOT fallback to code or name matching
        return null;
    }

    // ========================= BULK DELETE  ========================

    public BulkOperationResult<Long> bulkDeleteSpecializations(List<Long> ids) {

        // Build config
        BulkDeleteConfig<Specialization> config = BulkDeleteConfig.<Specialization>builder()
                .entityFinder(id -> specializationRepository.findById(id).orElse(null))
                .foreignKeyConstraintsChecker(this::checkForeignKeyConstraints)
                .repositoryDeleter(specializationRepository::deleteById)
                .entityName(entityName)
                .build();

        // Execute với processor
        BulkDeleteProcessor<Specialization> processor = new BulkDeleteProcessor<>(config);

        return processor.execute(ids);
    }

    public List<SpecializationResponse> getSpecializations(Pageable pageable) {
        Page<Specialization> page = specializationRepository.findAll(pageable);
        return page.getContent()
                .stream().map(specializationMapper::toSpecializationResponse).toList();
    }

    public SpecializationResponse getSpecialization(Long id) {
        return specializationMapper.toSpecializationResponse(specializationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public SpecializationResponse updateSpecialization(Long id, SpecializationRequest request) {
        Specialization specialization = specializationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        // Check source_id uniqueness for update
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            specializationRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getSpecializationId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }

        specializationMapper.updateSpecialization(specialization, request);

        return specializationMapper.toSpecializationResponse(specializationRepository.save(specialization));
    }

    public void deleteSpecialization(Long id) {
        checkForeignKeyConstraints(id);

        specializationRepository.deleteById(id);
    }

    private void checkForeignKeyConstraints(Long id) {
        if (!specializationRepository.existsById(id)) {
            throw new NotFoundException(entityName);
        }

        // Check references (RESTRICT strategy)
        long refCount = employeeEducationRepository.countBySpecialization_SpecializationId(id);
        if (refCount > 0) {
            throw new CannotDeleteException(
                    "Specialization", id, "EmployeeEducation", refCount
            );
        }
    }
}
