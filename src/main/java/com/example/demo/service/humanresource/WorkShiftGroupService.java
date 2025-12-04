package com.example.demo.service.humanresource;

import com.example.demo.dto.BulkOperationResult;
import com.example.demo.dto.humanresource.WorkShiftGroup.WorkShiftGroupRequest;
import com.example.demo.dto.humanresource.WorkShiftGroup.WorkShiftGroupResponse;
import com.example.demo.entity.humanresource.WorkShiftGroup;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.WorkShiftGroupMapper;
import com.example.demo.repository.humanresource.WorkShiftGroupRepository;
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
import com.example.demo.exception.CannotDeleteException;

import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WorkShiftGroupService {
    final WorkShiftGroupRepository workShiftGroupRepository;
    final WorkShiftGroupMapper workShiftGroupMapper;
    final EmployeeWorkShiftRepository employeeWorkShiftRepository;
    final EntityManager entityManager;

    @Value("${entities.humanresource.workshiftgroup}")
    private String entityName;

    public WorkShiftGroupResponse createWorkShiftGroup(WorkShiftGroupRequest request) {
        // Check source_id uniqueness for create
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            workShiftGroupRepository.findBySourceId(request.getSourceId()).ifPresent(b -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        WorkShiftGroup workShiftGroup = workShiftGroupMapper.toWorkShiftGroup(request);

        return workShiftGroupMapper.toWorkShiftGroupResponse(workShiftGroupRepository.save(workShiftGroup));
    }

    /**
     * Bulk Upsert với Final Batch Logic, Partial Success Pattern:
     * - Safe Batch: saveAll() - requests không có unique conflicts
     * - Final Batch: save + flush từng request - requests có potential conflicts
     * - Trả về detailed result với success/failure breakdown
     */
    public BulkOperationResult<WorkShiftGroupResponse> bulkUpsertWorkShiftGroups(
            List<WorkShiftGroupRequest> requests) {

        // 1. Define unique field configurations (WorkShiftGroup has 2 unique fields)
        UniqueFieldConfig<WorkShiftGroupRequest> sourceIdConfig =
                new UniqueFieldConfig<>("source_id", WorkShiftGroupRequest::getSourceId);
        UniqueFieldConfig<WorkShiftGroupRequest> codeConfig =
                new UniqueFieldConfig<>("work_shift_group_code", WorkShiftGroupRequest::getWorkShiftGroupCode);
        UniqueFieldConfig<WorkShiftGroupRequest> nameConfig =
                new UniqueFieldConfig<>("name", WorkShiftGroupRequest::getName);

        // 2. Define entity fetchers for each unique field
        Map<String, Function<Set<String>, List<WorkShiftGroup>>> entityFetchers = new HashMap<>();
        entityFetchers.put("source_id", workShiftGroupRepository::findBySourceIdIn);
        entityFetchers.put("work_shift_group_code", workShiftGroupRepository::findByWorkShiftGroupCodeIn);
        entityFetchers.put("name", workShiftGroupRepository::findByNameIn);

        // 2.5. Define entity field extractors (to extract values from returned entities)
        Map<String, Function<WorkShiftGroup, String>> entityFieldExtractors = new HashMap<>();
        entityFieldExtractors.put("source_id", WorkShiftGroup::getSourceId);
        entityFieldExtractors.put("work_shift_group_code", WorkShiftGroup::getWorkShiftGroupCode);
        entityFieldExtractors.put("name", WorkShiftGroup::getName);

        // 3. Setup unique fields using helper
        UniqueFieldsSetupHelper.UniqueFieldsSetup<WorkShiftGroupRequest> setup =
                UniqueFieldsSetupHelper.buildUniqueFieldsSetup(
                        requests,
                        entityFetchers,
                        entityFieldExtractors,
                        sourceIdConfig,
                        codeConfig,
                        nameConfig
                );

        // 4.  Build bulk upsert config
        BulkUpsertConfig<WorkShiftGroupRequest, WorkShiftGroup, WorkShiftGroupResponse> config =
                BulkUpsertConfig.<WorkShiftGroupRequest, WorkShiftGroup, WorkShiftGroupResponse>builder()
                        .uniqueFieldExtractors(setup.getUniqueFieldExtractors())
                        .existingValuesMaps(setup.getExistingValuesMaps())
                        .entityToResponseMapper(workShiftGroupMapper::toWorkShiftGroupResponse)
                        .requestToEntityMapper(workShiftGroupMapper::toWorkShiftGroup)
                        .entityUpdater(workShiftGroupMapper::updateWorkShiftGroup)
                        .existingEntityFinder(this::findExistingEntityForUpsert)
                        .repositorySaver(workShiftGroupRepository::saveAll)
                        .repositorySaveAndFlusher(workShiftGroupRepository::saveAndFlush)
                        .entityManagerClearer(entityManager::clear)
                        .build();

        // 5. Execute bulk upsert
        BulkUpsertProcessor<WorkShiftGroupRequest, WorkShiftGroup, WorkShiftGroupResponse> processor =
                new BulkUpsertProcessor<>(config);

        return processor.execute(requests);
    }

    /**
     * Helper: Find existing entity for upsert
     * STRICT LOGIC: Only match by sourceId (primary identifier)
     */
    private WorkShiftGroup findExistingEntityForUpsert(WorkShiftGroupRequest request) {
        // ONLY match by sourceId (canonical identifier)
        if (request.getSourceId() != null && !request.getSourceId().trim().isEmpty()) {
            Optional<WorkShiftGroup> bySourceId = workShiftGroupRepository.findBySourceId(request.getSourceId());
            if (bySourceId.isPresent()) {
                return bySourceId.get();
            }
        }

        // Do NOT fallback to code or name matching
        return null;
    }

    // ========================= BULK DELETE  ========================

    public BulkOperationResult<Long> bulkDeleteWorkShiftGroups(List<Long> ids) {

        // Build config
        BulkDeleteConfig<WorkShiftGroup> config = BulkDeleteConfig.<WorkShiftGroup>builder()
                .entityFinder(id -> workShiftGroupRepository.findById(id).orElse(null))
                .foreignKeyConstraintsChecker(this::checkForeignKeyConstraints)
                .repositoryDeleter(workShiftGroupRepository::deleteById)
                .entityName(entityName)
                .build();

        // Execute với processor
        BulkDeleteProcessor<WorkShiftGroup> processor = new BulkDeleteProcessor<>(config);

        return processor.execute(ids);
    }

    public List<WorkShiftGroupResponse> getWorkShiftGroups(Pageable pageable) {
        Page<WorkShiftGroup> page = workShiftGroupRepository.findAll(pageable);
        return page.getContent()
                .stream().map(workShiftGroupMapper::toWorkShiftGroupResponse).toList();
    }

    public WorkShiftGroupResponse getWorkShiftGroup(Long id) {
        return workShiftGroupMapper.toWorkShiftGroupResponse(workShiftGroupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public WorkShiftGroupResponse updateWorkShiftGroup(Long id, WorkShiftGroupRequest request) {
        WorkShiftGroup workShiftGroup = workShiftGroupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        // Check source_id uniqueness for update
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            workShiftGroupRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getWorkShiftGroupId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }

        workShiftGroupMapper.updateWorkShiftGroup(workShiftGroup, request);

        return workShiftGroupMapper.toWorkShiftGroupResponse(workShiftGroupRepository.save(workShiftGroup));
    }

    public void deleteWorkShiftGroup(Long id) {
        checkForeignKeyConstraints(id);

        workShiftGroupRepository.deleteById(id);
    }

    private void checkForeignKeyConstraints(Long id) {
        if (!workShiftGroupRepository.existsById(id)) {
            throw new NotFoundException(entityName);
        }

        // Check references (RESTRICT strategy)
        long refCount = employeeWorkShiftRepository.countByWorkShiftGroup_WorkShiftGroupId(id);
        if (refCount > 0) {
            throw new CannotDeleteException(
                    "WorkShiftGroup", id, "EmployeeWorkShift", refCount
            );
        }
    }
}
