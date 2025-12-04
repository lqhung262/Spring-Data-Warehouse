package com.example.demo.service.humanresource;

import com.example.demo.dto.BulkOperationResult;
import com.example.demo.dto.humanresource.OtType.OtTypeRequest;
import com.example.demo.dto.humanresource.OtType.OtTypeResponse;
import com.example.demo.entity.humanresource.OtType;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.OtTypeMapper;
import com.example.demo.repository.humanresource.EmployeeWorkShiftRepository;
import com.example.demo.exception.CannotDeleteException;
import com.example.demo.repository.humanresource.OtTypeRepository;
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
public class OtTypeService {
    final OtTypeRepository otTypeRepository;
    final OtTypeMapper otTypeMapper;
    final EmployeeWorkShiftRepository employeeWorkShiftRepository;
    final EntityManager entityManager;

    @Value("${entities.humanresource.ottype}")
    private String entityName;

    public OtTypeResponse createOtType(OtTypeRequest request) {
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            otTypeRepository.findBySourceId(request.getSourceId()).ifPresent(b -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        OtType otType = otTypeMapper.toOtType(request);

        return otTypeMapper.toOtTypeResponse(otTypeRepository.save(otType));
    }

    /**
     * Bulk Upsert với Final Batch Logic, Partial Success Pattern:
     * - Safe Batch: saveAll() - requests không có unique conflicts
     * - Final Batch: save + flush từng request - requests có potential conflicts
     * - Trả về detailed result với success/failure breakdown
     */
    public BulkOperationResult<OtTypeResponse> bulkUpsertOtTypes(
            List<OtTypeRequest> requests) {

        // 1. Define unique field configurations (OtType has 2 unique fields)
        UniqueFieldConfig<OtTypeRequest> sourceIdConfig =
                new UniqueFieldConfig<>("source_id", OtTypeRequest::getSourceId);
        UniqueFieldConfig<OtTypeRequest> codeConfig =
                new UniqueFieldConfig<>("ot_type_code", OtTypeRequest::getOtTypeCode);
        UniqueFieldConfig<OtTypeRequest> nameConfig =
                new UniqueFieldConfig<>("name", OtTypeRequest::getName);

        // 2. Define entity fetchers for each unique field
        Map<String, Function<Set<String>, List<OtType>>> entityFetchers = new HashMap<>();
        entityFetchers.put("source_id", otTypeRepository::findBySourceIdIn);
        entityFetchers.put("ot_type_code", otTypeRepository::findByOtTypeCodeIn);
        entityFetchers.put("name", otTypeRepository::findByNameIn);

        // 2.5. Define entity field extractors (to extract values from returned entities)
        Map<String, Function<OtType, String>> entityFieldExtractors = new HashMap<>();
        entityFieldExtractors.put("source_id", OtType::getSourceId);
        entityFieldExtractors.put("ot_type_code", OtType::getOtTypeCode);
        entityFieldExtractors.put("name", OtType::getName);

        // 3. Setup unique fields using helper
        UniqueFieldsSetupHelper.UniqueFieldsSetup<OtTypeRequest> setup =
                UniqueFieldsSetupHelper.buildUniqueFieldsSetup(
                        requests,
                        entityFetchers,
                        entityFieldExtractors,
                        sourceIdConfig,
                        codeConfig,
                        nameConfig
                );

        // 4.  Build bulk upsert config
        BulkUpsertConfig<OtTypeRequest, OtType, OtTypeResponse> config =
                BulkUpsertConfig.<OtTypeRequest, OtType, OtTypeResponse>builder()
                        .uniqueFieldExtractors(setup.getUniqueFieldExtractors())
                        .existingValuesMaps(setup.getExistingValuesMaps())
                        .entityToResponseMapper(otTypeMapper::toOtTypeResponse)
                        .requestToEntityMapper(otTypeMapper::toOtType)
                        .entityUpdater(otTypeMapper::updateOtType)
                        .existingEntityFinder(this::findExistingEntityForUpsert)
                        .repositorySaver(otTypeRepository::saveAll)
                        .repositorySaveAndFlusher(otTypeRepository::saveAndFlush)
                        .entityManagerClearer(entityManager::clear)
                        .build();

        // 5. Execute bulk upsert
        BulkUpsertProcessor<OtTypeRequest, OtType, OtTypeResponse> processor =
                new BulkUpsertProcessor<>(config);

        return processor.execute(requests);
    }

    /**
     * Helper: Find existing entity for upsert
     * STRICT LOGIC: Only match by sourceId (primary identifier)
     */
    private OtType findExistingEntityForUpsert(OtTypeRequest request) {
        // ONLY match by sourceId (canonical identifier)
        if (request.getSourceId() != null && !request.getSourceId().trim().isEmpty()) {
            Optional<OtType> bySourceId = otTypeRepository.findBySourceId(request.getSourceId());
            if (bySourceId.isPresent()) {
                return bySourceId.get();
            }
        }

        // Do NOT fallback to code or name matching
        return null;
    }

    // ========================= BULK DELETE  ========================

    public BulkOperationResult<Long> bulkDeleteOtTypes(List<Long> ids) {

        // Build config
        BulkDeleteConfig<OtType> config = BulkDeleteConfig.<OtType>builder()
                .entityFinder(id -> otTypeRepository.findById(id).orElse(null))
                .foreignKeyConstraintsChecker(this::checkForeignKeyConstraints)
                .repositoryDeleter(otTypeRepository::deleteById)
                .entityName(entityName)
                .build();

        // Execute với processor
        BulkDeleteProcessor<OtType> processor = new BulkDeleteProcessor<>(config);

        return processor.execute(ids);
    }

    public List<OtTypeResponse> getOtTypes(Pageable pageable) {
        Page<OtType> page = otTypeRepository.findAll(pageable);
        return page.getContent()
                .stream().map(otTypeMapper::toOtTypeResponse).toList();
    }

    public OtTypeResponse getOtType(Long id) {
        return otTypeMapper.toOtTypeResponse(otTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public OtTypeResponse updateOtType(Long id, OtTypeRequest request) {
        OtType otType = otTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        // Check source_id uniqueness for update
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            otTypeRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getOtTypeId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }

        otTypeMapper.updateOtType(otType, request);

        return otTypeMapper.toOtTypeResponse(otTypeRepository.save(otType));
    }

    public void deleteOtType(Long id) {
        checkForeignKeyConstraints(id);

        otTypeRepository.deleteById(id);
    }

    private void checkForeignKeyConstraints(Long id) {
        if (!otTypeRepository.existsById(id)) {
            throw new NotFoundException(entityName);
        }

        // Check references (RESTRICT strategy)
        long refCount = employeeWorkShiftRepository.countByOtType_OtTypeId(id);
        if (refCount > 0) {
            throw new CannotDeleteException(
                    "OtType", id, "EmployeeWorkShift", refCount
            );
        }
    }
}
