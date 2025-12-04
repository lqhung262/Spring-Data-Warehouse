package com.example.demo.service.humanresource;

import com.example.demo.dto.BulkOperationResult;
import com.example.demo.dto.humanresource.MaritalStatus.MaritalStatusRequest;
import com.example.demo.dto.humanresource.MaritalStatus.MaritalStatusResponse;
import com.example.demo.entity.humanresource.MaritalStatus;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.MaritalStatusMapper;
import com.example.demo.repository.humanresource.MaritalStatusRepository;
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
public class MaritalStatusService {
    final MaritalStatusRepository maritalStatusRepository;
    final MaritalStatusMapper maritalStatusMapper;
    final EmployeeRepository employeeRepository;
    final EntityManager entityManager;

    @Value("${entities.humanresource.maritalstatus}")
    private String entityName;


    public MaritalStatusResponse createMaritalStatus(MaritalStatusRequest request) {
        // Check source_id uniqueness for create
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            maritalStatusRepository.findBySourceId(request.getSourceId()).ifPresent(b -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        MaritalStatus maritalStatus = maritalStatusMapper.toMaritalStatus(request);

        return maritalStatusMapper.toMaritalStatusResponse(maritalStatusRepository.save(maritalStatus));
    }


    /**
     * Bulk Upsert với Final Batch Logic, Partial Success Pattern:
     * - Safe Batch: saveAll() - requests không có unique conflicts
     * - Final Batch: save + flush từng request - requests có potential conflicts
     * - Trả về detailed result với success/failure breakdown
     */
    public BulkOperationResult<MaritalStatusResponse> bulkUpsertMaritalStatuses(
            List<MaritalStatusRequest> requests) {

        // 1. Define unique field configurations (MaritalStatus has 2 unique fields)
        UniqueFieldConfig<MaritalStatusRequest> sourceIdConfig =
                new UniqueFieldConfig<>("source_id", MaritalStatusRequest::getSourceId);
        UniqueFieldConfig<MaritalStatusRequest> codeConfig =
                new UniqueFieldConfig<>("marital_status_code", MaritalStatusRequest::getMaritalStatusCode);
        UniqueFieldConfig<MaritalStatusRequest> nameConfig =
                new UniqueFieldConfig<>("name", MaritalStatusRequest::getName);

        // 2. Define entity fetchers for each unique field
        Map<String, Function<Set<String>, List<MaritalStatus>>> entityFetchers = new HashMap<>();
        entityFetchers.put("source_id", maritalStatusRepository::findBySourceIdIn);
        entityFetchers.put("marital_status_code", maritalStatusRepository::findByMaritalStatusCodeIn);
        entityFetchers.put("name", maritalStatusRepository::findByNameIn);

        // 2.5. Define entity field extractors (to extract values from returned entities)
        Map<String, Function<MaritalStatus, String>> entityFieldExtractors = new HashMap<>();
        entityFieldExtractors.put("source_id", MaritalStatus::getSourceId);
        entityFieldExtractors.put("marital_status_code", MaritalStatus::getMaritalStatusCode);
        entityFieldExtractors.put("name", MaritalStatus::getName);

        // 3. Setup unique fields using helper
        UniqueFieldsSetupHelper.UniqueFieldsSetup<MaritalStatusRequest> setup =
                UniqueFieldsSetupHelper.buildUniqueFieldsSetup(
                        requests,
                        entityFetchers,
                        entityFieldExtractors,
                        sourceIdConfig,
                        codeConfig,
                        nameConfig
                );

        // 4.  Build bulk upsert config
        BulkUpsertConfig<MaritalStatusRequest, MaritalStatus, MaritalStatusResponse> config =
                BulkUpsertConfig.<MaritalStatusRequest, MaritalStatus, MaritalStatusResponse>builder()
                        .uniqueFieldExtractors(setup.getUniqueFieldExtractors())
                        .existingValuesMaps(setup.getExistingValuesMaps())
                        .entityToResponseMapper(maritalStatusMapper::toMaritalStatusResponse)
                        .requestToEntityMapper(maritalStatusMapper::toMaritalStatus)
                        .entityUpdater(maritalStatusMapper::updateMaritalStatus)
                        .existingEntityFinder(this::findExistingEntityForUpsert)
                        .repositorySaver(maritalStatusRepository::saveAll)
                        .repositorySaveAndFlusher(maritalStatusRepository::saveAndFlush)
                        .entityManagerClearer(entityManager::clear)
                        .build();

        // 5. Execute bulk upsert
        BulkUpsertProcessor<MaritalStatusRequest, MaritalStatus, MaritalStatusResponse> processor =
                new BulkUpsertProcessor<>(config);

        return processor.execute(requests);
    }

    /**
     * Helper: Find existing entity for upsert
     * <p>
     * STRICT LOGIC: Only match by sourceId (primary identifier)
     * - If sourceId matches → UPDATE existing entity
     * - If sourceId doesn't match → CREATE new entity (may fail if code/name duplicate)
     * <p>
     * This ensures proper behavior:
     * - Request with existing sourceId → update
     * - Request with new sourceId but existing code/name → create attempt → DB constraint error
     */
    private MaritalStatus findExistingEntityForUpsert(MaritalStatusRequest request) {
        // ONLY match by sourceId (canonical identifier)
        if (request.getSourceId() != null && !request.getSourceId().trim().isEmpty()) {
            Optional<MaritalStatus> bySourceId = maritalStatusRepository.findBySourceId(request.getSourceId());
            if (bySourceId.isPresent()) {
                return bySourceId.get();
            }
        }

        // Do NOT fallback to code or name matching
        // If code/name exists but sourceId doesn't match, let it CREATE and fail with constraint error
        return null;
    }

    // ========================= BULK DELETE  ========================

    public BulkOperationResult<Long> bulkDeleteMaritalStatuses(List<Long> ids) {

        // Build config
        BulkDeleteConfig<MaritalStatus> config = BulkDeleteConfig.<MaritalStatus>builder()
                .entityFinder(id -> maritalStatusRepository.findById(id).orElse(null))
                .foreignKeyConstraintsChecker(this::checkForeignKeyConstraints)
                .repositoryDeleter(maritalStatusRepository::deleteById)
                .entityName(entityName)
                .build();

        // Execute với processor
        BulkDeleteProcessor<MaritalStatus> processor = new BulkDeleteProcessor<>(config);

        return processor.execute(ids);
    }

    public List<MaritalStatusResponse> getMaritalStatuses(Pageable pageable) {
        Page<MaritalStatus> page = maritalStatusRepository.findAll(pageable);
        return page.getContent()
                .stream().map(maritalStatusMapper::toMaritalStatusResponse).toList();
    }

    public MaritalStatusResponse getMaritalStatus(Long id) {
        return maritalStatusMapper.toMaritalStatusResponse(maritalStatusRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public MaritalStatusResponse updateMaritalStatus(Long id, MaritalStatusRequest request) {
        MaritalStatus maritalStatus = maritalStatusRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        // Check source_id uniqueness for update
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            maritalStatusRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getMaritalStatusId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }

        maritalStatusMapper.updateMaritalStatus(maritalStatus, request);

        return maritalStatusMapper.toMaritalStatusResponse(maritalStatusRepository.save(maritalStatus));
    }

    public void deleteMaritalStatus(Long id) {
        checkForeignKeyConstraints(id);

        maritalStatusRepository.deleteById(id);
    }

    private void checkForeignKeyConstraints(Long id) {
        if (!maritalStatusRepository.existsById(id)) {
            throw new NotFoundException(entityName);
        }

        // Check references (RESTRICT strategy)
        long refCount = employeeRepository.countByMaritalStatus_MaritalStatusId(id);
        if (refCount > 0) {
            throw new CannotDeleteException(
                    "MaritalStatus", id, "Employee", refCount
            );
        }
    }
}
