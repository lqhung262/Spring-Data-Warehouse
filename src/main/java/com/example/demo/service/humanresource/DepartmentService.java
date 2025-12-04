package com.example.demo.service.humanresource;

import com.example.demo.dto.BulkOperationResult;
import com.example.demo.dto.humanresource.Department.DepartmentRequest;
import com.example.demo.dto.humanresource.Department.DepartmentResponse;
import com.example.demo.entity.humanresource.Department;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.DepartmentMapper;
import com.example.demo.repository.humanresource.DepartmentRepository;
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
public class DepartmentService {
    final DepartmentRepository departmentRepository;
    final DepartmentMapper departmentMapper;
    final EmployeeDecisionRepository employeeDecisionRepository;
    final EntityManager entityManager;

    @Value("${entities.humanresource.department}")
    private String entityName;

    public DepartmentResponse createDepartment(DepartmentRequest request) {
        // Check source_id uniqueness for create
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            departmentRepository.findBySourceId(request.getSourceId()).ifPresent(b -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        Department department = departmentMapper.toDepartment(request);

        return departmentMapper.toDepartmentResponse(departmentRepository.save(department));
    }

    /**
     * Bulk Upsert với Final Batch Logic, Partial Success Pattern:
     * - Safe Batch: saveAll() - requests không có unique conflicts
     * - Final Batch: save + flush từng request - requests có potential conflicts
     * - Trả về detailed result với success/failure breakdown
     */
    public BulkOperationResult<DepartmentResponse> bulkUpsertDepartments(
            List<DepartmentRequest> requests) {

        // 1. Define unique field configurations (Department has 2 unique fields)
        UniqueFieldConfig<DepartmentRequest> sourceIdConfig =
                new UniqueFieldConfig<>("source_id", DepartmentRequest::getSourceId);
        UniqueFieldConfig<DepartmentRequest> codeConfig =
                new UniqueFieldConfig<>("department_code", DepartmentRequest::getDepartmentCode);
        UniqueFieldConfig<DepartmentRequest> nameConfig =
                new UniqueFieldConfig<>("name", DepartmentRequest::getName);

        // 2. Define entity fetchers for each unique field
        Map<String, Function<Set<String>, List<Department>>> entityFetchers = new HashMap<>();
        entityFetchers.put("source_id", departmentRepository::findBySourceIdIn);
        entityFetchers.put("department_code", departmentRepository::findByDepartmentCodeIn);
        entityFetchers.put("name", departmentRepository::findByNameIn);

        // 2.5. Define entity field extractors
        Map<String, Function<Department, String>> entityFieldExtractors = new HashMap<>();
        entityFieldExtractors.put("source_id", Department::getSourceId);
        entityFieldExtractors.put("department_code", Department::getDepartmentCode);
        entityFieldExtractors.put("name", Department::getName);

        // 3. Setup unique fields using helper
        UniqueFieldsSetupHelper.UniqueFieldsSetup<DepartmentRequest> setup =
                UniqueFieldsSetupHelper.buildUniqueFieldsSetup(
                        requests,
                        entityFetchers,
                        entityFieldExtractors,
                        sourceIdConfig,
                        codeConfig,
                        nameConfig
                );

        // 4.  Build bulk upsert config
        BulkUpsertConfig<DepartmentRequest, Department, DepartmentResponse> config =
                BulkUpsertConfig.<DepartmentRequest, Department, DepartmentResponse>builder()
                        .uniqueFieldExtractors(setup.getUniqueFieldExtractors())
                        .existingValuesMaps(setup.getExistingValuesMaps())
                        .entityToResponseMapper(departmentMapper::toDepartmentResponse)
                        .requestToEntityMapper(departmentMapper::toDepartment)
                        .entityUpdater(departmentMapper::updateDepartment)
                        .existingEntityFinder(this::findExistingEntityForUpsert)
                        .repositorySaver(departmentRepository::saveAll)
                        .repositorySaveAndFlusher(departmentRepository::saveAndFlush)
                        .entityManagerClearer(entityManager::clear)
                        .build();

        // 5. Execute bulk upsert
        BulkUpsertProcessor<DepartmentRequest, Department, DepartmentResponse> processor =
                new BulkUpsertProcessor<>(config);

        return processor.execute(requests);
    }

    /**
     * Helper: Find existing entity
     */
    private Department findExistingEntityForUpsert(DepartmentRequest request) {
        if (request.getSourceId() != null && !request.getSourceId().trim().isEmpty()) {
            Optional<Department> bySourceId = departmentRepository.findBySourceId(request.getSourceId());
            if (bySourceId.isPresent()) {
                return bySourceId.get();
            }
        }


        return null;
    }

    // ========================= BULK DELETE  ========================

    public BulkOperationResult<Long> bulkDeleteDepartments(List<Long> ids) {

        // Build config
        BulkDeleteConfig<Department> config = BulkDeleteConfig.<Department>builder()
                .entityFinder(id -> departmentRepository.findById(id).orElse(null))
                .foreignKeyConstraintsChecker(this::checkForeignKeyConstraints)
                .repositoryDeleter(departmentRepository::deleteById)
                .entityName(entityName)
                .build();

        // Execute với processor
        BulkDeleteProcessor<Department> processor = new BulkDeleteProcessor<>(config);

        return processor.execute(ids);
    }


    public List<DepartmentResponse> getDepartments(Pageable pageable) {
        Page<Department> page = departmentRepository.findAll(pageable);
        return page.getContent()
                .stream().map(departmentMapper::toDepartmentResponse).toList();
    }

    public DepartmentResponse getDepartment(Long id) {
        return departmentMapper.toDepartmentResponse(departmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public DepartmentResponse updateDepartment(Long id, DepartmentRequest request) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        // Check source_id uniqueness for update
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            departmentRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getDepartmentId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }

        departmentMapper.updateDepartment(department, request);

        return departmentMapper.toDepartmentResponse(departmentRepository.save(department));
    }

    public void deleteDepartment(Long id) {
        checkForeignKeyConstraints(id);

        departmentRepository.deleteById(id);
    }

    private void checkForeignKeyConstraints(Long id) {
        if (!departmentRepository.existsById(id)) {
            throw new NotFoundException(entityName);
        }

        // Check references (RESTRICT strategy)
        long refCount = employeeDecisionRepository.countByDepartment_DepartmentId(id);
        if (refCount > 0) {
            throw new CannotDeleteException(
                    "Department", id, "EmployeeDecision", refCount
            );
        }
    }
}
