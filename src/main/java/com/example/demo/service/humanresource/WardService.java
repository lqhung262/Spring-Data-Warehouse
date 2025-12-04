package com.example.demo.service.humanresource;

import com.example.demo.dto.BulkOperationResult;
import com.example.demo.dto.humanresource.Ward.WardRequest;
import com.example.demo.dto.humanresource.Ward.WardResponse;
import com.example.demo.entity.humanresource.Ward;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.WardMapper;
import com.example.demo.repository.humanresource.WardRepository;
import com.example.demo.util.bulk.*;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.demo.repository.humanresource.OldWardRepository;
import com.example.demo.repository.humanresource.OldDistrictRepository;
import com.example.demo.repository.humanresource.EmployeeRepository;

import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WardService {
    final WardRepository wardRepository;
    final WardMapper wardMapper;
    final OldWardRepository oldWardRepository;
    final OldDistrictRepository oldDistrictRepository;
    final EmployeeRepository employeeRepository;
    final EntityManager entityManager;

    @Value("${entities.humanresource.ward}")
    private String entityName;

    public WardResponse createWard(WardRequest request) {
        // Check source_id uniqueness for create
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            wardRepository.findBySourceId(request.getSourceId()).ifPresent(b -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        Ward ward = wardMapper.toWard(request);
        // Set FK references from IDs in request
        wardMapper.setReferences(ward, request);

        return wardMapper.toWardResponse(wardRepository.save(ward));
    }

    /**
     * Bulk Upsert với Final Batch Logic, Partial Success Pattern:
     * - Safe Batch: saveAll() - requests không có unique conflicts
     * - Final Batch: save + flush từng request - requests có potential conflicts
     * - Trả về detailed result với success/failure breakdown
     */
    public BulkOperationResult<WardResponse> bulkUpsertWards(
            List<WardRequest> requests) {

        // 1. Define unique field configurations (Ward has 2 unique fields)
        UniqueFieldConfig<WardRequest> sourceIdConfig =
                new UniqueFieldConfig<>("source_id", WardRequest::getSourceId);
        UniqueFieldConfig<WardRequest> nameConfig =
                new UniqueFieldConfig<>("name", WardRequest::getName);

        // 2. Define entity fetchers for each unique field
        Map<String, Function<Set<String>, List<Ward>>> entityFetchers = new HashMap<>();
        entityFetchers.put("source_id", wardRepository::findBySourceIdIn);
        entityFetchers.put("name", wardRepository::findByNameIn);

        // 2.5. Define entity field extractors (to extract values from returned entities)
        Map<String, Function<Ward, String>> entityFieldExtractors = new HashMap<>();
        entityFieldExtractors.put("source_id", Ward::getSourceId);
        entityFieldExtractors.put("name", Ward::getName);

        // 3. Setup unique fields using helper
        UniqueFieldsSetupHelper.UniqueFieldsSetup<WardRequest> setup =
                UniqueFieldsSetupHelper.buildUniqueFieldsSetup(
                        requests,
                        entityFetchers,
                        entityFieldExtractors,
                        sourceIdConfig,
                        nameConfig
                );

        // 4.  Build bulk upsert config
        BulkUpsertConfig<WardRequest, Ward, WardResponse> config =
                BulkUpsertConfig.<WardRequest, Ward, WardResponse>builder()
                        .uniqueFieldExtractors(setup.getUniqueFieldExtractors())
                        .existingValuesMaps(setup.getExistingValuesMaps())
                        .entityToResponseMapper(wardMapper::toWardResponse)
                        .requestToEntityMapper(wardMapper::toWard)
                        .entityUpdater(wardMapper::updateWard)
                        .existingEntityFinder(this::findExistingEntityForUpsert)
                        .repositorySaver(wardRepository::saveAll)
                        .repositorySaveAndFlusher(wardRepository::saveAndFlush)
                        .entityManagerClearer(entityManager::clear)
                        .build();

        // 5. Execute bulk upsert
        BulkUpsertProcessor<WardRequest, Ward, WardResponse> processor =
                new BulkUpsertProcessor<>(config);

        return processor.execute(requests);
    }

    /**
     * Helper: Find existing entity for upsert
     * STRICT LOGIC: Only match by sourceId (primary identifier)
     */
    private Ward findExistingEntityForUpsert(WardRequest request) {
        // ONLY match by sourceId (canonical identifier)
        if (request.getSourceId() != null && !request.getSourceId().trim().isEmpty()) {
            Optional<Ward> bySourceId = wardRepository.findBySourceId(request.getSourceId());
            if (bySourceId.isPresent()) {
                return bySourceId.get();
            }
        }

        // Do NOT fallback to code or name matching
        return null;
    }

    // ========================= BULK DELETE  ========================

    public BulkOperationResult<Long> bulkDeleteWards(List<Long> ids) {

        // Build config
        BulkDeleteConfig<Ward> config = BulkDeleteConfig.<Ward>builder()
                .entityFinder(id -> wardRepository.findById(id).orElse(null))
                .foreignKeyConstraintsChecker(this::checkForeignKeyConstraints)
                .repositoryDeleter(wardRepository::deleteById)
                .entityName(entityName)
                .build();

        // Execute với processor
        BulkDeleteProcessor<Ward> processor = new BulkDeleteProcessor<>(config);

        return processor.execute(ids);
    }

    public List<WardResponse> getWards(Pageable pageable) {
        Page<Ward> page = wardRepository.findAll(pageable);
        return page.getContent()
                .stream().map(wardMapper::toWardResponse).toList();
    }

    public WardResponse getWard(Long id) {
        return wardMapper.toWardResponse(wardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public WardResponse updateWard(Long id, WardRequest request) {
        Ward ward = wardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        // Check source_id uniqueness for update
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            wardRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getWardId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }

        wardMapper.updateWard(ward, request);
        // Set FK references from IDs in request
        wardMapper.setReferences(ward, request);

        return wardMapper.toWardResponse(wardRepository.save(ward));
    }

    public void deleteWard(Long id) {
        checkForeignKeyConstraints(id);

        wardRepository.deleteById(id);
    }

    private void checkForeignKeyConstraints(Long id) {
        if (!wardRepository.existsById(id)) {
            throw new NotFoundException(entityName);
        }

        // Check all references (RESTRICT strategy)
        long oldWardCount = oldWardRepository.countByWard_WardId(id);
        long oldDistrictCount = oldDistrictRepository.countByWard_WardId(id);
        long empCurrentCount = employeeRepository.countByCurrentAddressWard_WardId(id);
        long empPermanentCount = employeeRepository.countByPermanentAddressWard_WardId(id);
        long totalCount = oldWardCount + oldDistrictCount + empCurrentCount + empPermanentCount;

        if (totalCount > 0) {
            throw new com.example.demo.exception.CannotDeleteException(
                    "Ward", id, "referencing records", totalCount
            );
        }
    }
}
