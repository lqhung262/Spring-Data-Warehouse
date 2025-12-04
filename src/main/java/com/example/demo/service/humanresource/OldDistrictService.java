package com.example.demo.service.humanresource;

import com.example.demo.dto.BulkOperationResult;
import com.example.demo.dto.humanresource.OldDistrict.OldDistrictRequest;
import com.example.demo.dto.humanresource.OldDistrict.OldDistrictResponse;
import com.example.demo.entity.humanresource.OldDistrict;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.OldDistrictMapper;
import com.example.demo.repository.humanresource.OldDistrictRepository;
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

import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OldDistrictService {
    final OldDistrictRepository oldDistrictRepository;
    final OldDistrictMapper oldDistrictMapper;
    final OldWardRepository oldWardRepository;
    final EntityManager entityManager;

    @Value("${entities.humanresource.olddistrict}")
    private String entityName;

    public OldDistrictResponse createOldDistrict(OldDistrictRequest request) {
        // Check source_id uniqueness for create
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            oldDistrictRepository.findBySourceId(request.getSourceId()).ifPresent(b -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        OldDistrict oldDistrict = oldDistrictMapper.toOldDistrict(request);

        // Set FK references from IDs in request
        oldDistrictMapper.setReferences(oldDistrict, request);

        return oldDistrictMapper.toOldDistrictResponse(oldDistrictRepository.save(oldDistrict));
    }

    /**
     * Bulk Upsert với Final Batch Logic, Partial Success Pattern:
     * - Safe Batch: saveAll() - requests không có unique conflicts
     * - Final Batch: save + flush từng request - requests có potential conflicts
     * - Trả về detailed result với success/failure breakdown
     */
    public BulkOperationResult<OldDistrictResponse> bulkUpsertOldDistricts(
            List<OldDistrictRequest> requests) {

        // 1. Define unique field configurations (OldDistrict has 2 unique fields)
        UniqueFieldConfig<OldDistrictRequest> sourceIdConfig =
                new UniqueFieldConfig<>("source_id", OldDistrictRequest::getSourceId);
        UniqueFieldConfig<OldDistrictRequest> nameConfig =
                new UniqueFieldConfig<>("name", OldDistrictRequest::getName);

        // 2. Define entity fetchers for each unique field
        Map<String, Function<Set<String>, List<OldDistrict>>> entityFetchers = new HashMap<>();
        entityFetchers.put("source_id", oldDistrictRepository::findBySourceIdIn);
        entityFetchers.put("name", oldDistrictRepository::findByNameIn);

        // 2.5. Define entity field extractors (to extract values from returned entities)
        Map<String, Function<OldDistrict, String>> entityFieldExtractors = new HashMap<>();
        entityFieldExtractors.put("source_id", OldDistrict::getSourceId);
        entityFieldExtractors.put("name", OldDistrict::getName);

        // 3. Setup unique fields using helper
        UniqueFieldsSetupHelper.UniqueFieldsSetup<OldDistrictRequest> setup =
                UniqueFieldsSetupHelper.buildUniqueFieldsSetup(
                        requests,
                        entityFetchers,
                        entityFieldExtractors,
                        sourceIdConfig,
                        nameConfig
                );

        // 4.  Build bulk upsert config
        BulkUpsertConfig<OldDistrictRequest, OldDistrict, OldDistrictResponse> config =
                BulkUpsertConfig.<OldDistrictRequest, OldDistrict, OldDistrictResponse>builder()
                        .uniqueFieldExtractors(setup.getUniqueFieldExtractors())
                        .existingValuesMaps(setup.getExistingValuesMaps())
                        .entityToResponseMapper(oldDistrictMapper::toOldDistrictResponse)
                        .requestToEntityMapper(oldDistrictMapper::toOldDistrict)
                        .entityUpdater(oldDistrictMapper::updateOldDistrict)
                        .existingEntityFinder(this::findExistingEntityForUpsert)
                        .repositorySaver(oldDistrictRepository::saveAll)
                        .repositorySaveAndFlusher(oldDistrictRepository::saveAndFlush)
                        .entityManagerClearer(entityManager::clear)
                        .build();

        // 5. Execute bulk upsert
        BulkUpsertProcessor<OldDistrictRequest, OldDistrict, OldDistrictResponse> processor =
                new BulkUpsertProcessor<>(config);

        return processor.execute(requests);
    }

    /**
     * Helper: Find existing entity for upsert
     * STRICT LOGIC: Only match by sourceId (primary identifier)
     */
    private OldDistrict findExistingEntityForUpsert(OldDistrictRequest request) {
        // ONLY match by sourceId (canonical identifier)
        if (request.getSourceId() != null && !request.getSourceId().trim().isEmpty()) {
            Optional<OldDistrict> bySourceId = oldDistrictRepository.findBySourceId(request.getSourceId());
            if (bySourceId.isPresent()) {
                return bySourceId.get();
            }
        }

        // Do NOT fallback to code or name matching
        return null;
    }

    // ========================= BULK DELETE  ========================

    public BulkOperationResult<Long> bulkDeleteOldDistricts(List<Long> ids) {

        // Build config
        BulkDeleteConfig<OldDistrict> config = BulkDeleteConfig.<OldDistrict>builder()
                .entityFinder(id -> oldDistrictRepository.findById(id).orElse(null))
                .foreignKeyConstraintsChecker(this::checkForeignKeyConstraints)
                .repositoryDeleter(oldDistrictRepository::deleteById)
                .entityName(entityName)
                .build();

        // Execute với processor
        BulkDeleteProcessor<OldDistrict> processor = new BulkDeleteProcessor<>(config);

        return processor.execute(ids);
    }

    public List<OldDistrictResponse> getOldDistricts(Pageable pageable) {
        Page<OldDistrict> page = oldDistrictRepository.findAll(pageable);
        return page.getContent()
                .stream().map(oldDistrictMapper::toOldDistrictResponse).toList();
    }

    public OldDistrictResponse getOldDistrict(Long id) {
        return oldDistrictMapper.toOldDistrictResponse(oldDistrictRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public OldDistrictResponse updateOldDistrict(Long id, OldDistrictRequest request) {
        OldDistrict oldDistrict = oldDistrictRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        // Check source_id uniqueness for update
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            oldDistrictRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getOldDistrictId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }

        oldDistrictMapper.updateOldDistrict(oldDistrict, request);
        // Set FK references from IDs in request
        oldDistrictMapper.setReferences(oldDistrict, request);

        return oldDistrictMapper.toOldDistrictResponse(oldDistrictRepository.save(oldDistrict));
    }

    public void deleteOldDistrict(Long id) {
        checkForeignKeyConstraints(id);

        oldDistrictRepository.deleteById(id);
    }

    private void checkForeignKeyConstraints(Long id) {
        if (!oldDistrictRepository.existsById(id)) {
            throw new NotFoundException(entityName);
        }

        // Check if any OldWard references this OldDistrict (RESTRICT strategy)
        long childCount = oldWardRepository.countByOldDistrict_OldDistrictId(id);
        if (childCount > 0) {
            throw new com.example.demo.exception.CannotDeleteException(
                    "OldDistrict", id, "OldWard", childCount
            );
        }
    }
}
