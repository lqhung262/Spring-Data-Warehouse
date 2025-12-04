package com.example.demo.service.humanresource;

import com.example.demo.dto.BulkOperationResult;
import com.example.demo.dto.humanresource.OldWard.OldWardRequest;
import com.example.demo.dto.humanresource.OldWard.OldWardResponse;
import com.example.demo.entity.humanresource.OldWard;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.OldWardMapper;
import com.example.demo.repository.humanresource.OldWardRepository;
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
public class OldWardService {
    final OldWardRepository oldWardRepository;
    final OldWardMapper oldWardMapper;
    final EntityManager entityManager;

    @Value("${entities.humanresource.oldward}")
    private String entityName;

    public OldWardResponse createOldWard(OldWardRequest request) {
        // Check source_id uniqueness for create
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            oldWardRepository.findBySourceId(request.getSourceId()).ifPresent(b -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        OldWard oldWard = oldWardMapper.toOldWard(request);
        // Set FK references from IDs in request
        oldWardMapper.setReferences(oldWard, request);

        return oldWardMapper.toOldWardResponse(oldWardRepository.save(oldWard));
    }


    /**
     * Bulk Upsert với Final Batch Logic, Partial Success Pattern:
     * - Safe Batch: saveAll() - requests không có unique conflicts
     * - Final Batch: save + flush từng request - requests có potential conflicts
     * - Trả về detailed result với success/failure breakdown
     */
    public BulkOperationResult<OldWardResponse> bulkUpsertOldWards(
            List<OldWardRequest> requests) {

        // 1. Define unique field configurations (OldWard has 2 unique fields)
        UniqueFieldConfig<OldWardRequest> sourceIdConfig =
                new UniqueFieldConfig<>("source_id", OldWardRequest::getSourceId);
        UniqueFieldConfig<OldWardRequest> nameConfig =
                new UniqueFieldConfig<>("name", OldWardRequest::getName);

        // 2. Define entity fetchers for each unique field
        Map<String, Function<Set<String>, List<OldWard>>> entityFetchers = new HashMap<>();
        entityFetchers.put("source_id", oldWardRepository::findBySourceIdIn);
        entityFetchers.put("name", oldWardRepository::findByNameIn);

        // 2.5. Define entity field extractors (to extract values from returned entities)
        Map<String, Function<OldWard, String>> entityFieldExtractors = new HashMap<>();
        entityFieldExtractors.put("source_id", OldWard::getSourceId);
        entityFieldExtractors.put("name", OldWard::getName);

        // 3. Setup unique fields using helper
        UniqueFieldsSetupHelper.UniqueFieldsSetup<OldWardRequest> setup =
                UniqueFieldsSetupHelper.buildUniqueFieldsSetup(
                        requests,
                        entityFetchers,
                        entityFieldExtractors,
                        sourceIdConfig,
                        nameConfig
                );

        // 4.  Build bulk upsert config
        BulkUpsertConfig<OldWardRequest, OldWard, OldWardResponse> config =
                BulkUpsertConfig.<OldWardRequest, OldWard, OldWardResponse>builder()
                        .uniqueFieldExtractors(setup.getUniqueFieldExtractors())
                        .existingValuesMaps(setup.getExistingValuesMaps())
                        .entityToResponseMapper(oldWardMapper::toOldWardResponse)
                        .requestToEntityMapper(oldWardMapper::toOldWard)
                        .entityUpdater(oldWardMapper::updateOldWard)
                        .existingEntityFinder(this::findExistingEntityForUpsert)
                        .repositorySaver(oldWardRepository::saveAll)
                        .repositorySaveAndFlusher(oldWardRepository::saveAndFlush)
                        .entityManagerClearer(entityManager::clear)
                        .build();

        // 5. Execute bulk upsert
        BulkUpsertProcessor<OldWardRequest, OldWard, OldWardResponse> processor =
                new BulkUpsertProcessor<>(config);

        return processor.execute(requests);
    }

    /**
     * Helper: Find existing entity for upsert
     * STRICT LOGIC: Only match by sourceId (primary identifier)
     */
    private OldWard findExistingEntityForUpsert(OldWardRequest request) {
        // ONLY match by sourceId (canonical identifier)
        if (request.getSourceId() != null && !request.getSourceId().trim().isEmpty()) {
            Optional<OldWard> bySourceId = oldWardRepository.findBySourceId(request.getSourceId());
            if (bySourceId.isPresent()) {
                return bySourceId.get();
            }
        }

        // Do NOT fallback to code or name matching
        return null;
    }

    // ========================= BULK DELETE  ========================

    public BulkOperationResult<Long> bulkDeleteOldWards(List<Long> ids) {

        // Build config
        BulkDeleteConfig<OldWard> config = BulkDeleteConfig.<OldWard>builder()
                .entityFinder(id -> oldWardRepository.findById(id).orElse(null))
                .foreignKeyConstraintsChecker(this::checkForeignKeyConstraints)
                .repositoryDeleter(oldWardRepository::deleteById)
                .entityName(entityName)
                .build();

        // Execute với processor
        BulkDeleteProcessor<OldWard> processor = new BulkDeleteProcessor<>(config);

        return processor.execute(ids);
    }

    public List<OldWardResponse> getOldWards(Pageable pageable) {
        Page<OldWard> page = oldWardRepository.findAll(pageable);
        return page.getContent()
                .stream().map(oldWardMapper::toOldWardResponse).toList();
    }

    public OldWardResponse getOldWard(Long id) {
        return oldWardMapper.toOldWardResponse(oldWardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public OldWardResponse updateOldWard(Long id, OldWardRequest request) {
        OldWard oldWard = oldWardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        // Check source_id uniqueness for update
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            oldWardRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getOldWardId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }

        oldWardMapper.updateOldWard(oldWard, request);
        // Set FK references from IDs in request
        oldWardMapper.setReferences(oldWard, request);

        return oldWardMapper.toOldWardResponse(oldWardRepository.save(oldWard));
    }

    public void deleteOldWard(Long id) {
        checkForeignKeyConstraints(id);

        oldWardRepository.deleteById(id);
    }

    private void checkForeignKeyConstraints(Long id) {
        if (!oldWardRepository.existsById(id)) {
            throw new NotFoundException(entityName);
        }

        // Note: OldWard can be safely deleted as no direct FK references
        // Database will enforce constraints if needed
    }
}
