package com.example.demo.service.humanresource;

import com.example.demo.dto.BulkOperationResult;
import com.example.demo.dto.humanresource.OldProvinceCity.OldProvinceCityRequest;
import com.example.demo.dto.humanresource.OldProvinceCity.OldProvinceCityResponse;
import com.example.demo.entity.humanresource.OldProvinceCity;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.OldProvinceCityMapper;
import com.example.demo.repository.humanresource.OldProvinceCityRepository;
import com.example.demo.util.bulk.*;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.demo.repository.humanresource.OldDistrictRepository;

import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OldProvinceCityService {
    final OldProvinceCityRepository oldProvinceCityRepository;
    final OldProvinceCityMapper oldProvinceCityMapper;
    final OldDistrictRepository oldDistrictRepository;
    final EntityManager entityManager;

    @Value("${entities.humanresource.oldprovincecity}")
    private String entityName;


    public OldProvinceCityResponse createOldProvinceCity(OldProvinceCityRequest request) {
        // Check source_id uniqueness for create
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            oldProvinceCityRepository.findBySourceId(request.getSourceId()).ifPresent(b -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        OldProvinceCity oldProvinceCity = oldProvinceCityMapper.toOldProvinceCity(request);

        // Set FK references from IDs in request
        oldProvinceCityMapper.setReferences(oldProvinceCity, request);

        return oldProvinceCityMapper.toOldProvinceCityResponse(oldProvinceCityRepository.save(oldProvinceCity));
    }


    /**
     * Bulk Upsert với Final Batch Logic, Partial Success Pattern:
     * - Safe Batch: saveAll() - requests không có unique conflicts
     * - Final Batch: save + flush từng request - requests có potential conflicts
     * - Trả về detailed result với success/failure breakdown
     */
    public BulkOperationResult<OldProvinceCityResponse> bulkUpsertOldProvinceCities(
            List<OldProvinceCityRequest> requests) {

        // 1. Define unique field configurations (OldProvinceCity has 2 unique fields)
        UniqueFieldConfig<OldProvinceCityRequest> sourceIdConfig =
                new UniqueFieldConfig<>("source_id", OldProvinceCityRequest::getSourceId);
        UniqueFieldConfig<OldProvinceCityRequest> nameConfig =
                new UniqueFieldConfig<>("name", OldProvinceCityRequest::getName);

        // 2. Define entity fetchers for each unique field
        Map<String, Function<Set<String>, List<OldProvinceCity>>> entityFetchers = new HashMap<>();
        entityFetchers.put("source_id", oldProvinceCityRepository::findBySourceIdIn);
        entityFetchers.put("name", oldProvinceCityRepository::findByNameIn);

        // 2.5. Define entity field extractors (to extract values from returned entities)
        Map<String, Function<OldProvinceCity, String>> entityFieldExtractors = new HashMap<>();
        entityFieldExtractors.put("source_id", OldProvinceCity::getSourceId);
        entityFieldExtractors.put("name", OldProvinceCity::getName);

        // 3. Setup unique fields using helper
        UniqueFieldsSetupHelper.UniqueFieldsSetup<OldProvinceCityRequest> setup =
                UniqueFieldsSetupHelper.buildUniqueFieldsSetup(
                        requests,
                        entityFetchers,
                        entityFieldExtractors,
                        sourceIdConfig,
                        nameConfig
                );

        // 4.  Build bulk upsert config
        BulkUpsertConfig<OldProvinceCityRequest, OldProvinceCity, OldProvinceCityResponse> config =
                BulkUpsertConfig.<OldProvinceCityRequest, OldProvinceCity, OldProvinceCityResponse>builder()
                        .uniqueFieldExtractors(setup.getUniqueFieldExtractors())
                        .existingValuesMaps(setup.getExistingValuesMaps())
                        .entityToResponseMapper(oldProvinceCityMapper::toOldProvinceCityResponse)
                        .requestToEntityMapper(oldProvinceCityMapper::toOldProvinceCity)
                        .entityUpdater(oldProvinceCityMapper::updateOldProvinceCity)
                        .existingEntityFinder(this::findExistingEntityForUpsert)
                        .repositorySaver(oldProvinceCityRepository::saveAll)
                        .repositorySaveAndFlusher(oldProvinceCityRepository::saveAndFlush)
                        .entityManagerClearer(entityManager::clear)
                        .build();

        // 5. Execute bulk upsert
        BulkUpsertProcessor<OldProvinceCityRequest, OldProvinceCity, OldProvinceCityResponse> processor =
                new BulkUpsertProcessor<>(config);

        return processor.execute(requests);
    }

    /**
     * Helper: Find existing entity for upsert
     * STRICT LOGIC: Only match by sourceId (primary identifier)
     */
    private OldProvinceCity findExistingEntityForUpsert(OldProvinceCityRequest request) {
        // ONLY match by sourceId (canonical identifier)
        if (request.getSourceId() != null && !request.getSourceId().trim().isEmpty()) {
            Optional<OldProvinceCity> bySourceId = oldProvinceCityRepository.findBySourceId(request.getSourceId());
            if (bySourceId.isPresent()) {
                return bySourceId.get();
            }
        }

        // Do NOT fallback to code or name matching
        return null;
    }

    // ========================= BULK DELETE  ========================

    public BulkOperationResult<Long> bulkDeleteOldProvinceCities(List<Long> ids) {

        // Build config
        BulkDeleteConfig<OldProvinceCity> config = BulkDeleteConfig.<OldProvinceCity>builder()
                .entityFinder(id -> oldProvinceCityRepository.findById(id).orElse(null))
                .foreignKeyConstraintsChecker(this::checkForeignKeyConstraints)
                .repositoryDeleter(oldProvinceCityRepository::deleteById)
                .entityName(entityName)
                .build();

        // Execute với processor
        BulkDeleteProcessor<OldProvinceCity> processor = new BulkDeleteProcessor<>(config);

        return processor.execute(ids);
    }

    public List<OldProvinceCityResponse> getOldProvinceCities(Pageable pageable) {
        Page<OldProvinceCity> page = oldProvinceCityRepository.findAll(pageable);
        return page.getContent()
                .stream().map(oldProvinceCityMapper::toOldProvinceCityResponse).toList();
    }

    public OldProvinceCityResponse getOldProvinceCity(Long id) {
        return oldProvinceCityMapper.toOldProvinceCityResponse(oldProvinceCityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public OldProvinceCityResponse updateOldProvinceCity(Long id, OldProvinceCityRequest request) {
        OldProvinceCity oldProvinceCity = oldProvinceCityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        // Check source_id uniqueness for update
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            oldProvinceCityRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getOldProvinceCityId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }

        oldProvinceCityMapper.updateOldProvinceCity(oldProvinceCity, request);
        // Set FK references from IDs in request
        oldProvinceCityMapper.setReferences(oldProvinceCity, request);

        return oldProvinceCityMapper.toOldProvinceCityResponse(oldProvinceCityRepository.save(oldProvinceCity));
    }

    public void deleteOldProvinceCity(Long id) {
        checkForeignKeyConstraints(id);

        oldProvinceCityRepository.deleteById(id);
    }

    private void checkForeignKeyConstraints(Long id) {
        if (!oldDistrictRepository.existsById(id)) {
            throw new NotFoundException(entityName);
        }

        // Check references (RESTRICT strategy)
        long childCount = oldDistrictRepository.countByOldProvinceCity_OldProvinceCityId(id);
        if (childCount > 0) {
            throw new com.example.demo.exception.CannotDeleteException(
                    "OldProvinceCity", id, "OldDistrict", childCount
            );
        }
    }
}
