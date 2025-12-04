package com.example.demo.service.humanresource;

import com.example.demo.dto.BulkOperationResult;
import com.example.demo.dto.humanresource.EmployeeType.EmployeeTypeRequest;
import com.example.demo.dto.humanresource.EmployeeType.EmployeeTypeResponse;
import com.example.demo.entity.humanresource.EmployeeType;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.EmployeeTypeMapper;
import com.example.demo.repository.humanresource.EmployeeTypeRepository;
import com.example.demo.util.bulk.*;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.demo.repository.humanresource.EmployeeDecisionRepository;
import com.example.demo.exception.CannotDeleteException;

import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeTypeService {
    final EmployeeTypeRepository employeeTypeRepository;
    final EmployeeTypeMapper employeeTypeMapper;
    final EmployeeDecisionRepository employeeDecisionRepository;
    final EntityManager entityManager;

    @Value("${entities.humanresource.employeetype}")
    private String entityName;


    public EmployeeTypeResponse createEmployeeType(EmployeeTypeRequest request) {
        // Check source_id uniqueness for create
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            employeeTypeRepository.findBySourceId(request.getSourceId()).ifPresent(b -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        EmployeeType employeeType = employeeTypeMapper.toEmployeeType(request);

        return employeeTypeMapper.toEmployeeTypeResponse(employeeTypeRepository.save(employeeType));
    }


    /**
     * Bulk Upsert với Final Batch Logic, Partial Success Pattern:
     * - Safe Batch: saveAll() - requests không có unique conflicts
     * - Final Batch: save + flush từng request - requests có potential conflicts
     * - Trả về detailed result với success/failure breakdown
     */
    public BulkOperationResult<EmployeeTypeResponse> bulkUpsertEmployeeTypes(
            List<EmployeeTypeRequest> requests) {

        // 1. Define unique field configurations (EmployeeType has 2 unique fields)
        UniqueFieldConfig<EmployeeTypeRequest> sourceIdConfig =
                new UniqueFieldConfig<>("source_id", EmployeeTypeRequest::getSourceId);
        UniqueFieldConfig<EmployeeTypeRequest> codeConfig =
                new UniqueFieldConfig<>("employee_type_code", EmployeeTypeRequest::getEmployeeTypeCode);
        UniqueFieldConfig<EmployeeTypeRequest> nameConfig =
                new UniqueFieldConfig<>("name", EmployeeTypeRequest::getName);

        // 2. Define entity fetchers for each unique field
        Map<String, Function<Set<String>, List<EmployeeType>>> entityFetchers = new HashMap<>();
        entityFetchers.put("source_id", employeeTypeRepository::findBySourceIdIn);
        entityFetchers.put("employee_type_code", employeeTypeRepository::findByEmployeeTypeCodeIn);
        entityFetchers.put("name", employeeTypeRepository::findByNameIn);

        // 2.5. Define entity field extractors
        Map<String, Function<EmployeeType, String>> entityFieldExtractors = new HashMap<>();
        entityFieldExtractors.put("source_id", EmployeeType::getSourceId);
        entityFieldExtractors.put("employee_type_code", EmployeeType::getEmployeeTypeCode);
        entityFieldExtractors.put("name", EmployeeType::getName);

        // 3. Setup unique fields using helper
        UniqueFieldsSetupHelper.UniqueFieldsSetup<EmployeeTypeRequest> setup =
                UniqueFieldsSetupHelper.buildUniqueFieldsSetup(
                        requests,
                        entityFetchers,
                        entityFieldExtractors,
                        sourceIdConfig,
                        codeConfig,
                        nameConfig
                );

        // 4.  Build bulk upsert config
        BulkUpsertConfig<EmployeeTypeRequest, EmployeeType, EmployeeTypeResponse> config =
                BulkUpsertConfig.<EmployeeTypeRequest, EmployeeType, EmployeeTypeResponse>builder()
                        .uniqueFieldExtractors(setup.getUniqueFieldExtractors())
                        .existingValuesMaps(setup.getExistingValuesMaps())
                        .entityToResponseMapper(employeeTypeMapper::toEmployeeTypeResponse)
                        .requestToEntityMapper(employeeTypeMapper::toEmployeeType)
                        .entityUpdater(employeeTypeMapper::updateEmployeeType)
                        .existingEntityFinder(this::findExistingEntityForUpsert)
                        .repositorySaver(employeeTypeRepository::saveAll)
                        .repositorySaveAndFlusher(employeeTypeRepository::saveAndFlush)
                        .entityManagerClearer(entityManager::clear)
                        .build();

        // 5. Execute bulk upsert
        BulkUpsertProcessor<EmployeeTypeRequest, EmployeeType, EmployeeTypeResponse> processor =
                new BulkUpsertProcessor<>(config);

        return processor.execute(requests);
    }

    /**
     * Helper: Find existing entity
     */
    private EmployeeType findExistingEntityForUpsert(EmployeeTypeRequest request) {
        if (request.getSourceId() != null && !request.getSourceId().trim().isEmpty()) {
            Optional<EmployeeType> bySourceId = employeeTypeRepository.findBySourceId(request.getSourceId());
            if (bySourceId.isPresent()) {
                return bySourceId.get();
            }
        }

        return null;
    }

    // ========================= BULK DELETE  ========================

    public BulkOperationResult<Long> bulkDeleteEmployeeTypes(List<Long> ids) {

        // Build config
        BulkDeleteConfig<EmployeeType> config = BulkDeleteConfig.<EmployeeType>builder()
                .entityFinder(id -> employeeTypeRepository.findById(id).orElse(null))
                .foreignKeyConstraintsChecker(this::checkForeignKeyConstraints)
                .repositoryDeleter(employeeTypeRepository::deleteById)
                .entityName(entityName)
                .build();

        // Execute với processor
        BulkDeleteProcessor<EmployeeType> processor = new BulkDeleteProcessor<>(config);

        return processor.execute(ids);
    }

    public List<EmployeeTypeResponse> getEmployeeTypes(Pageable pageable) {
        Page<EmployeeType> page = employeeTypeRepository.findAll(pageable);
        return page.getContent()
                .stream().map(employeeTypeMapper::toEmployeeTypeResponse).toList();
    }

    public EmployeeTypeResponse getEmployeeType(Long id) {
        return employeeTypeMapper.toEmployeeTypeResponse(employeeTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public EmployeeTypeResponse updateEmployeeType(Long id, EmployeeTypeRequest request) {
        EmployeeType employeeType = employeeTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        // Check source_id uniqueness for update
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            employeeTypeRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getEmployeeTypeId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }

        employeeTypeMapper.updateEmployeeType(employeeType, request);

        return employeeTypeMapper.toEmployeeTypeResponse(employeeTypeRepository.save(employeeType));
    }

    public void deleteEmployeeType(Long id) {
        checkForeignKeyConstraints(id);

        employeeTypeRepository.deleteById(id);
    }

    private void checkForeignKeyConstraints(Long id) {
        if (!employeeTypeRepository.existsById(id)) {
            throw new NotFoundException(entityName);
        }

        // Check references from EmployeeDecision
        long refCount = employeeDecisionRepository.countByEmployeeType_EmployeeTypeId(id);
        if (refCount > 0) {
            throw new CannotDeleteException(
                    "EmployeeType", id, "EmployeeDecision", refCount
            );
        }
    }
}
