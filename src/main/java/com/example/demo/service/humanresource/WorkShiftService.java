package com.example.demo.service.humanresource;

import com.example.demo.dto.BulkOperationResult;
import com.example.demo.dto.humanresource.WorkShift.WorkShiftRequest;
import com.example.demo.dto.humanresource.WorkShift.WorkShiftResponse;
import com.example.demo.entity.humanresource.WorkShift;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.WorkShiftMapper;
import com.example.demo.exception.CannotDeleteException;
import com.example.demo.repository.humanresource.WorkShiftRepository;
import com.example.demo.util.bulk.*;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.demo.repository.humanresource.EmployeeWorkShiftRepository;

import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WorkShiftService {
    final WorkShiftRepository workShiftRepository;
    final WorkShiftMapper workShiftMapper;
    final EmployeeWorkShiftRepository employeeWorkShiftRepository;
    final EntityManager entityManager;

    @Value("${entities.humanresource.workshift}")
    private String entityName;

    public WorkShiftResponse createWorkShift(WorkShiftRequest request) {
        // Check source_id uniqueness for create
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            workShiftRepository.findBySourceId(request.getSourceId()).ifPresent(b -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        WorkShift workShift = workShiftMapper.toWorkShift(request);

        return workShiftMapper.toWorkShiftResponse(workShiftRepository.save(workShift));
    }

    /**
     * Bulk Upsert với Final Batch Logic, Partial Success Pattern:
     * - Safe Batch: saveAll() - requests không có unique conflicts
     * - Final Batch: save + flush từng request - requests có potential conflicts
     * - Trả về detailed result với success/failure breakdown
     */
    public BulkOperationResult<WorkShiftResponse> bulkUpsertWorkShifts(
            List<WorkShiftRequest> requests) {

        // 1. Define unique field configurations (WorkShift has 2 unique fields)
        UniqueFieldConfig<WorkShiftRequest> sourceIdConfig =
                new UniqueFieldConfig<>("source_id", WorkShiftRequest::getSourceId);
        UniqueFieldConfig<WorkShiftRequest> codeConfig =
                new UniqueFieldConfig<>("work_shift_code", WorkShiftRequest::getWorkShiftCode);
        UniqueFieldConfig<WorkShiftRequest> nameConfig =
                new UniqueFieldConfig<>("name", WorkShiftRequest::getName);

        // 2. Define entity fetchers for each unique field
        Map<String, Function<Set<String>, List<WorkShift>>> entityFetchers = new HashMap<>();
        entityFetchers.put("source_id", workShiftRepository::findBySourceIdIn);
        entityFetchers.put("work_shift_code", workShiftRepository::findByWorkShiftCodeIn);
        entityFetchers.put("name", workShiftRepository::findByNameIn);

        // 2.5. Define entity field extractors (to extract values from returned entities)
        Map<String, Function<WorkShift, String>> entityFieldExtractors = new HashMap<>();
        entityFieldExtractors.put("source_id", WorkShift::getSourceId);
        entityFieldExtractors.put("work_shift_code", WorkShift::getWorkShiftCode);
        entityFieldExtractors.put("name", WorkShift::getName);

        // 3. Setup unique fields using helper
        UniqueFieldsSetupHelper.UniqueFieldsSetup<WorkShiftRequest> setup =
                UniqueFieldsSetupHelper.buildUniqueFieldsSetup(
                        requests,
                        entityFetchers,
                        entityFieldExtractors,
                        sourceIdConfig,
                        codeConfig,
                        nameConfig
                );

        // 4.  Build bulk upsert config
        BulkUpsertConfig<WorkShiftRequest, WorkShift, WorkShiftResponse> config =
                BulkUpsertConfig.<WorkShiftRequest, WorkShift, WorkShiftResponse>builder()
                        .uniqueFieldExtractors(setup.getUniqueFieldExtractors())
                        .existingValuesMaps(setup.getExistingValuesMaps())
                        .entityToResponseMapper(workShiftMapper::toWorkShiftResponse)
                        .requestToEntityMapper(workShiftMapper::toWorkShift)
                        .entityUpdater(workShiftMapper::updateWorkShift)
                        .existingEntityFinder(this::findExistingEntityForUpsert)
                        .repositorySaver(workShiftRepository::saveAll)
                        .repositorySaveAndFlusher(workShiftRepository::saveAndFlush)
                        .entityManagerClearer(entityManager::clear)
                        .build();

        // 5. Execute bulk upsert
        BulkUpsertProcessor<WorkShiftRequest, WorkShift, WorkShiftResponse> processor =
                new BulkUpsertProcessor<>(config);

        return processor.execute(requests);
    }

    /**
     * Helper: Find existing entity for upsert
     * STRICT LOGIC: Only match by sourceId (primary identifier)
     */
    private WorkShift findExistingEntityForUpsert(WorkShiftRequest request) {
        // ONLY match by sourceId (canonical identifier)
        if (request.getSourceId() != null && !request.getSourceId().trim().isEmpty()) {
            Optional<WorkShift> bySourceId = workShiftRepository.findBySourceId(request.getSourceId());
            if (bySourceId.isPresent()) {
                return bySourceId.get();
            }
        }

        // Do NOT fallback to code or name matching
        return null;
    }

    // ========================= BULK DELETE  ========================

    public BulkOperationResult<Long> bulkDeleteWorkShifts(List<Long> ids) {

        // Build config
        BulkDeleteConfig<WorkShift> config = BulkDeleteConfig.<WorkShift>builder()
                .entityFinder(id -> workShiftRepository.findById(id).orElse(null))
                .foreignKeyConstraintsChecker(this::checkForeignKeyConstraints)
                .repositoryDeleter(workShiftRepository::deleteById)
                .entityName(entityName)
                .build();

        // Execute với processor
        BulkDeleteProcessor<WorkShift> processor = new BulkDeleteProcessor<>(config);

        return processor.execute(ids);
    }

    public List<WorkShiftResponse> getWorkShifts(Pageable pageable) {
        Page<WorkShift> page = workShiftRepository.findAll(pageable);
        return page.getContent()
                .stream().map(workShiftMapper::toWorkShiftResponse).toList();
    }

    public WorkShiftResponse getWorkShift(Long id) {
        return workShiftMapper.toWorkShiftResponse(workShiftRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public WorkShiftResponse updateWorkShift(Long id, WorkShiftRequest request) {
        WorkShift workShift = workShiftRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        // Check source_id uniqueness for update
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            workShiftRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getWorkShiftId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }

        workShiftMapper.updateWorkShift(workShift, request);

        return workShiftMapper.toWorkShiftResponse(workShiftRepository.save(workShift));
    }

    public void deleteWorkShift(Long id) {
        checkForeignKeyConstraints(id);

        workShiftRepository.deleteById(id);
    }

    private void checkForeignKeyConstraints(Long id) {
        if (!workShiftRepository.existsById(id)) {
            throw new NotFoundException(entityName);
        }

        // Check references (RESTRICT strategy)
        long refCount = employeeWorkShiftRepository.countByWorkShift_WorkShiftId(id);
        if (refCount > 0) {
            throw new CannotDeleteException(
                    "WorkShift", id, "EmployeeWorkShift", refCount
            );
        }
    }
}
