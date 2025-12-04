package com.example.demo.service.humanresource;

import com.example.demo.dto.BulkOperationResult;
import com.example.demo.dto.humanresource.LaborStatus.LaborStatusRequest;
import com.example.demo.dto.humanresource.LaborStatus.LaborStatusResponse;
import com.example.demo.entity.humanresource.LaborStatus;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.LaborStatusMapper;
import com.example.demo.repository.humanresource.LaborStatusRepository;
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
public class LaborStatusService {
    final LaborStatusRepository laborStatusRepository;
    final LaborStatusMapper laborStatusMapper;
    final EmployeeRepository employeeRepository;
    final EntityManager entityManager;

    @Value("${entities.humanresource.laborstatus}")
    private String entityName;

    public LaborStatusResponse createLaborStatus(LaborStatusRequest request) {
        // Check source_id uniqueness for create
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            laborStatusRepository.findBySourceId(request.getSourceId()).ifPresent(b -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        LaborStatus laborStatus = laborStatusMapper.toLaborStatus(request);

        return laborStatusMapper.toLaborStatusResponse(laborStatusRepository.save(laborStatus));
    }

    /**
     * Bulk Upsert với Final Batch Logic, Partial Success Pattern:
     * - Safe Batch: saveAll() - requests không có unique conflicts
     * - Final Batch: save + flush từng request - requests có potential conflicts
     * - Trả về detailed result với success/failure breakdown
     */
    public BulkOperationResult<LaborStatusResponse> bulkUpsertLaborStatuses(
            List<LaborStatusRequest> requests) {

        // 1. Define unique field configurations (LaborStatus has 2 unique fields)
        UniqueFieldConfig<LaborStatusRequest> sourceIdConfig =
                new UniqueFieldConfig<>("source_id", LaborStatusRequest::getSourceId);
        UniqueFieldConfig<LaborStatusRequest> codeConfig =
                new UniqueFieldConfig<>("labor_status_code", LaborStatusRequest::getLaborStatusCode);
        UniqueFieldConfig<LaborStatusRequest> nameConfig =
                new UniqueFieldConfig<>("name", LaborStatusRequest::getName);

        // 2. Define entity fetchers for each unique field
        Map<String, Function<Set<String>, List<LaborStatus>>> entityFetchers = new HashMap<>();
        entityFetchers.put("source_id", laborStatusRepository::findBySourceIdIn);
        entityFetchers.put("labor_status_code", laborStatusRepository::findByLaborStatusCodeIn);
        entityFetchers.put("name", laborStatusRepository::findByNameIn);

        // 2.5. Define entity field extractors
        Map<String, Function<LaborStatus, String>> entityFieldExtractors = new HashMap<>();
        entityFieldExtractors.put("source_id", LaborStatus::getSourceId);
        entityFieldExtractors.put("labor_status_code", LaborStatus::getLaborStatusCode);
        entityFieldExtractors.put("name", LaborStatus::getName);

        // 3. Setup unique fields using helper
        UniqueFieldsSetupHelper.UniqueFieldsSetup<LaborStatusRequest> setup =
                UniqueFieldsSetupHelper.buildUniqueFieldsSetup(
                        requests,
                        entityFetchers,
                        entityFieldExtractors,
                        sourceIdConfig,
                        codeConfig,
                        nameConfig
                );

        // 4.  Build bulk upsert config
        BulkUpsertConfig<LaborStatusRequest, LaborStatus, LaborStatusResponse> config =
                BulkUpsertConfig.<LaborStatusRequest, LaborStatus, LaborStatusResponse>builder()
                        .uniqueFieldExtractors(setup.getUniqueFieldExtractors())
                        .existingValuesMaps(setup.getExistingValuesMaps())
                        .entityToResponseMapper(laborStatusMapper::toLaborStatusResponse)
                        .requestToEntityMapper(laborStatusMapper::toLaborStatus)
                        .entityUpdater(laborStatusMapper::updateLaborStatus)
                        .existingEntityFinder(this::findExistingEntityForUpsert)
                        .repositorySaver(laborStatusRepository::saveAll)
                        .repositorySaveAndFlusher(laborStatusRepository::saveAndFlush)
                        .entityManagerClearer(entityManager::clear)
                        .build();

        // 5. Execute bulk upsert
        BulkUpsertProcessor<LaborStatusRequest, LaborStatus, LaborStatusResponse> processor =
                new BulkUpsertProcessor<>(config);

        return processor.execute(requests);
    }

    /**
     * Helper: Find existing entity
     */
    private LaborStatus findExistingEntityForUpsert(LaborStatusRequest request) {
        if (request.getSourceId() != null && !request.getSourceId().trim().isEmpty()) {
            Optional<LaborStatus> bySourceId = laborStatusRepository.findBySourceId(request.getSourceId());
            if (bySourceId.isPresent()) {
                return bySourceId.get();
            }
        }

        return null;
    }

    // ========================= BULK DELETE  ========================

    public BulkOperationResult<Long> bulkDeleteLaborStatuses(List<Long> ids) {

        // Build config
        BulkDeleteConfig<LaborStatus> config = BulkDeleteConfig.<LaborStatus>builder()
                .entityFinder(id -> laborStatusRepository.findById(id).orElse(null))
                .foreignKeyConstraintsChecker(this::checkReferenceBeforeDelete)
                .repositoryDeleter(laborStatusRepository::deleteById)
                .entityName(entityName)
                .build();

        // Execute với processor
        BulkDeleteProcessor<LaborStatus> processor = new BulkDeleteProcessor<>(config);

        return processor.execute(ids);
    }

    public List<LaborStatusResponse> getLaborStatuses(Pageable pageable) {
        Page<LaborStatus> page = laborStatusRepository.findAll(pageable);
        return page.getContent()
                .stream().map(laborStatusMapper::toLaborStatusResponse).toList();
    }

    public LaborStatusResponse getLaborStatus(Long id) {
        return laborStatusMapper.toLaborStatusResponse(laborStatusRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public LaborStatusResponse updateLaborStatus(Long id, LaborStatusRequest request) {
        LaborStatus laborStatus = laborStatusRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        // Check source_id uniqueness for update
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            laborStatusRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getLaborStatusId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }

        laborStatusMapper.updateLaborStatus(laborStatus, request);

        return laborStatusMapper.toLaborStatusResponse(laborStatusRepository.save(laborStatus));
    }

    public void deleteLaborStatus(Long id) {
        checkReferenceBeforeDelete(id);

        laborStatusRepository.deleteById(id);
    }

    private void checkReferenceBeforeDelete(Long id) {
        if (!laborStatusRepository.existsById(id)) {
            throw new NotFoundException(entityName);
        }

        // Check references (RESTRICT strategy)
        long refCount = employeeRepository.countByLaborStatus_LaborStatusId(id);
        if (refCount > 0) {
            throw new CannotDeleteException(
                    "LaborStatus", id, "Employee", refCount
            );
        }
    }
}
