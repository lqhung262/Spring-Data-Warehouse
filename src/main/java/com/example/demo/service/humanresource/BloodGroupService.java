package com.example.demo.service.humanresource;

import com.example.demo.dto.BulkOperationResult;
import com.example.demo.dto.humanresource.BloodGroup.BloodGroupRequest;
import com.example.demo.dto.humanresource.BloodGroup.BloodGroupResponse;
import com.example.demo.entity.humanresource.BloodGroup;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.BloodGroupMapper;
import com.example.demo.repository.humanresource.BloodGroupRepository;
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
public class BloodGroupService {
    final BloodGroupRepository bloodGroupRepository;
    final BloodGroupMapper bloodGroupMapper;
    final EmployeeRepository employeeRepository;
    final EntityManager entityManager;

    @Value("${entities.humanresource.bloodgroup}")
    private String entityName;

    public BloodGroupResponse createBloodGroup(BloodGroupRequest request) {
        // Check source_id uniqueness for create
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            bloodGroupRepository.findBySourceId(request.getSourceId()).ifPresent(b -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        BloodGroup bloodGroup = bloodGroupMapper.toBloodGroup(request);

        return bloodGroupMapper.toBloodGroupResponse(bloodGroupRepository.save(bloodGroup));
    }

    /**
     * Bulk Upsert với Final Batch Logic, Partial Success Pattern:
     * - Safe Batch: saveAll() - requests không có unique conflicts
     * - Final Batch: save + flush từng request - requests có potential conflicts
     * - Trả về detailed result với success/failure breakdown
     */
    public BulkOperationResult<BloodGroupResponse> bulkUpsertBloodGroups(
            List<BloodGroupRequest> requests) {

        // 1. Define unique field configurations (BloodGroup has 2 unique fields)
        UniqueFieldConfig<BloodGroupRequest> sourceIdConfig =
                new UniqueFieldConfig<>("source_id", BloodGroupRequest::getSourceId);
        UniqueFieldConfig<BloodGroupRequest> codeConfig =
                new UniqueFieldConfig<>("blood_group_code", BloodGroupRequest::getBloodGroupCode);
        UniqueFieldConfig<BloodGroupRequest> nameConfig =
                new UniqueFieldConfig<>("name", BloodGroupRequest::getName);

        // 2. Define entity fetchers for each unique field
        Map<String, Function<Set<String>, List<BloodGroup>>> entityFetchers = new HashMap<>();
        entityFetchers.put("source_id", bloodGroupRepository::findBySourceIdIn);
        entityFetchers.put("blood_group_code", bloodGroupRepository::findByBloodGroupCodeIn);
        entityFetchers.put("name", bloodGroupRepository::findByNameIn);

        // 2.5. Define entity field extractors
        Map<String, Function<BloodGroup, String>> entityFieldExtractors = new HashMap<>();
        entityFieldExtractors.put("source_id", BloodGroup::getSourceId);
        entityFieldExtractors.put("blood_group_code", BloodGroup::getBloodGroupCode);
        entityFieldExtractors.put("name", BloodGroup::getName);

        // 3. Setup unique fields using helper
        UniqueFieldsSetupHelper.UniqueFieldsSetup<BloodGroupRequest> setup =
                UniqueFieldsSetupHelper.buildUniqueFieldsSetup(
                        requests,
                        entityFetchers,
                        entityFieldExtractors,
                        sourceIdConfig,
                        codeConfig,
                        nameConfig
                );

        // 4.  Build bulk upsert config
        BulkUpsertConfig<BloodGroupRequest, BloodGroup, BloodGroupResponse> config =
                BulkUpsertConfig.<BloodGroupRequest, BloodGroup, BloodGroupResponse>builder()
                        .uniqueFieldExtractors(setup.getUniqueFieldExtractors())
                        .existingValuesMaps(setup.getExistingValuesMaps())
                        .entityToResponseMapper(bloodGroupMapper::toBloodGroupResponse)
                        .requestToEntityMapper(bloodGroupMapper::toBloodGroup)
                        .entityUpdater(bloodGroupMapper::updateBloodGroup)
                        .existingEntityFinder(this::findExistingEntityForUpsert)
                        .repositorySaver(bloodGroupRepository::saveAll)
                        .repositorySaveAndFlusher(bloodGroupRepository::saveAndFlush)
                        .entityManagerClearer(entityManager::clear)
                        .build();

        // 5. Execute bulk upsert
        BulkUpsertProcessor<BloodGroupRequest, BloodGroup, BloodGroupResponse> processor =
                new BulkUpsertProcessor<>(config);

        return processor.execute(requests);
    }

    /**
     * Helper: Find existing entity
     */
    private BloodGroup findExistingEntityForUpsert(BloodGroupRequest request) {
        if (request.getSourceId() != null && !request.getSourceId().trim().isEmpty()) {
            Optional<BloodGroup> bySourceId = bloodGroupRepository.findBySourceId(request.getSourceId());
            if (bySourceId.isPresent()) {
                return bySourceId.get();
            }
        }

        return null;
    }

    // ========================= BULK DELETE  ========================

    public BulkOperationResult<Long> bulkDeleteBloodGroups(List<Long> ids) {

        // Build config
        BulkDeleteConfig<BloodGroup> config = BulkDeleteConfig.<BloodGroup>builder()
                .entityFinder(id -> bloodGroupRepository.findById(id).orElse(null))
                .foreignKeyConstraintsChecker(this::checkForeignKeyConstraints)
                .repositoryDeleter(bloodGroupRepository::deleteById)
                .entityName(entityName)
                .build();

        // Execute với processor
        BulkDeleteProcessor<BloodGroup> processor = new BulkDeleteProcessor<>(config);

        return processor.execute(ids);
    }

    public List<BloodGroupResponse> getBloodGroups(Pageable pageable) {
        Page<BloodGroup> page = bloodGroupRepository.findAll(pageable);
        return page.getContent()
                .stream().map(bloodGroupMapper::toBloodGroupResponse).toList();
    }

    public BloodGroupResponse getBloodGroup(Long id) {
        return bloodGroupMapper.toBloodGroupResponse(bloodGroupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public BloodGroupResponse updateBloodGroup(Long id, BloodGroupRequest request) {
        BloodGroup bloodGroup = bloodGroupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        // Check source_id uniqueness for update
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            bloodGroupRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getBloodGroupId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }

        bloodGroupMapper.updateBloodGroup(bloodGroup, request);

        return bloodGroupMapper.toBloodGroupResponse(bloodGroupRepository.save(bloodGroup));
    }

    public void deleteBloodGroup(Long id) {
        checkForeignKeyConstraints(id);

        bloodGroupRepository.deleteById(id);
    }

    private void checkForeignKeyConstraints(Long id) {
        if (!bloodGroupRepository.existsById(id)) {
            throw new NotFoundException(entityName);
        }

        // Check references from Employee
        long employeeRefCount = employeeRepository.countByBloodGroup_BloodGroupId(id);
        if (employeeRefCount > 0) {
            throw new CannotDeleteException(
                    "BloodGroup", id, "Employee", employeeRefCount
            );
        }
    }
}
