package com.example.demo.service.humanresource;

import com.example.demo.dto.BulkOperationResult;
import com.example.demo.dto.humanresource.ExpenseType.ExpenseTypeRequest;
import com.example.demo.dto.humanresource.ExpenseType.ExpenseTypeResponse;
import com.example.demo.entity.humanresource.ExpenseType;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.ExpenseTypeMapper;
import com.example.demo.repository.humanresource.ExpenseTypeRepository;
import com.example.demo.repository.humanresource.EmployeeDecisionRepository;
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
public class ExpenseTypeService {
    final ExpenseTypeRepository expenseTypeRepository;
    final ExpenseTypeMapper expenseTypeMapper;
    final EmployeeDecisionRepository employeeDecisionRepository;
    final EntityManager entityManager;

    @Value("${entities.humanresource.expensetype}")
    private String entityName;

    public ExpenseTypeResponse createExpenseType(ExpenseTypeRequest request) {
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            expenseTypeRepository.findBySourceId(request.getSourceId()).ifPresent(b -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        ExpenseType expenseType = expenseTypeMapper.toExpenseType(request);

        return expenseTypeMapper.toExpenseTypeResponse(expenseTypeRepository.save(expenseType));
    }

    /**
     * Bulk Upsert với Final Batch Logic, Partial Success Pattern:
     * - Safe Batch: saveAll() - requests không có unique conflicts
     * - Final Batch: save + flush từng request - requests có potential conflicts
     * - Trả về detailed result với success/failure breakdown
     */
    public BulkOperationResult<ExpenseTypeResponse> bulkUpsertExpenseTypes(
            List<ExpenseTypeRequest> requests) {

        // 1. Define unique field configurations (ExpenseType has 2 unique fields)
        UniqueFieldConfig<ExpenseTypeRequest> sourceIdConfig =
                new UniqueFieldConfig<>("source_id", ExpenseTypeRequest::getSourceId);
        UniqueFieldConfig<ExpenseTypeRequest> codeConfig =
                new UniqueFieldConfig<>("expense_type_code", ExpenseTypeRequest::getExpenseTypeCode);
        UniqueFieldConfig<ExpenseTypeRequest> nameConfig =
                new UniqueFieldConfig<>("name", ExpenseTypeRequest::getName);

        // 2. Define entity fetchers for each unique field
        Map<String, Function<Set<String>, List<ExpenseType>>> entityFetchers = new HashMap<>();
        entityFetchers.put("source_id", expenseTypeRepository::findBySourceIdIn);
        entityFetchers.put("expense_type_code", expenseTypeRepository::findByExpenseTypeCodeIn);
        entityFetchers.put("name", expenseTypeRepository::findByNameIn);

        // 2.5. Define entity field extractors
        Map<String, Function<ExpenseType, String>> entityFieldExtractors = new HashMap<>();
        entityFieldExtractors.put("source_id", ExpenseType::getSourceId);
        entityFieldExtractors.put("expense_type_code", ExpenseType::getExpenseTypeCode);
        entityFieldExtractors.put("name", ExpenseType::getName);

        // 3. Setup unique fields using helper
        UniqueFieldsSetupHelper.UniqueFieldsSetup<ExpenseTypeRequest> setup =
                UniqueFieldsSetupHelper.buildUniqueFieldsSetup(
                        requests,
                        entityFetchers,
                        entityFieldExtractors,
                        sourceIdConfig,
                        codeConfig,
                        nameConfig
                );

        // 4.  Build bulk upsert config
        BulkUpsertConfig<ExpenseTypeRequest, ExpenseType, ExpenseTypeResponse> config =
                BulkUpsertConfig.<ExpenseTypeRequest, ExpenseType, ExpenseTypeResponse>builder()
                        .uniqueFieldExtractors(setup.getUniqueFieldExtractors())
                        .existingValuesMaps(setup.getExistingValuesMaps())
                        .entityToResponseMapper(expenseTypeMapper::toExpenseTypeResponse)
                        .requestToEntityMapper(expenseTypeMapper::toExpenseType)
                        .entityUpdater(expenseTypeMapper::updateExpenseType)
                        .existingEntityFinder(this::findExistingEntityForUpsert)
                        .repositorySaver(expenseTypeRepository::saveAll)
                        .repositorySaveAndFlusher(expenseTypeRepository::saveAndFlush)
                        .entityManagerClearer(entityManager::clear)
                        .build();

        // 5. Execute bulk upsert
        BulkUpsertProcessor<ExpenseTypeRequest, ExpenseType, ExpenseTypeResponse> processor =
                new BulkUpsertProcessor<>(config);

        return processor.execute(requests);
    }

    /**
     * Helper: Find existing entity
     */
    private ExpenseType findExistingEntityForUpsert(ExpenseTypeRequest request) {
        if (request.getSourceId() != null && !request.getSourceId().trim().isEmpty()) {
            Optional<ExpenseType> bySourceId = expenseTypeRepository.findBySourceId(request.getSourceId());
            if (bySourceId.isPresent()) {
                return bySourceId.get();
            }
        }

        return null;
    }

    // ========================= BULK DELETE  ========================

    public BulkOperationResult<Long> bulkDeleteExpenseTypes(List<Long> ids) {

        // Build config
        BulkDeleteConfig<ExpenseType> config = BulkDeleteConfig.<ExpenseType>builder()
                .entityFinder(id -> expenseTypeRepository.findById(id).orElse(null))
                .foreignKeyConstraintsChecker(this::checkForeignKeyConstraints)
                .repositoryDeleter(expenseTypeRepository::deleteById)
                .entityName(entityName)
                .build();

        // Execute với processor
        BulkDeleteProcessor<ExpenseType> processor = new BulkDeleteProcessor<>(config);

        return processor.execute(ids);
    }

    public List<ExpenseTypeResponse> getExpenseTypes(Pageable pageable) {
        Page<ExpenseType> page = expenseTypeRepository.findAll(pageable);
        return page.getContent()
                .stream().map(expenseTypeMapper::toExpenseTypeResponse).toList();
    }

    public ExpenseTypeResponse getExpenseType(Long id) {
        return expenseTypeMapper.toExpenseTypeResponse(expenseTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public ExpenseTypeResponse updateExpenseType(Long id, ExpenseTypeRequest request) {
        ExpenseType expenseType = expenseTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            expenseTypeRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getExpenseTypeId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }


        expenseTypeMapper.updateExpenseType(expenseType, request);

        return expenseTypeMapper.toExpenseTypeResponse(expenseTypeRepository.save(expenseType));
    }

    public void deleteExpenseType(Long id) {
        checkForeignKeyConstraints(id);

        expenseTypeRepository.deleteById(id);
    }

    private void checkForeignKeyConstraints(Long id) {
        if (!expenseTypeRepository.existsById(id)) {
            throw new NotFoundException(entityName);
        }

        // Check references (RESTRICT strategy) - ExpenseType has 2 FK references
        long level1Count = employeeDecisionRepository.countByCostCategoryLevel1_ExpenseTypeId(id);
        long level2Count = employeeDecisionRepository.countByCostCategoryLevel2_ExpenseTypeId(id);
        long totalCount = level1Count + level2Count;

        if (totalCount > 0) {
            throw new CannotDeleteException(
                    "ExpenseType", id, "EmployeeDecision", totalCount
            );
        }
    }
}
