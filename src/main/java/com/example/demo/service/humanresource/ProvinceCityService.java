package com.example.demo.service.humanresource;

import com.example.demo.dto.BulkOperationResult;
import com.example.demo.dto.BulkOperationError;
import com.example.demo.dto.humanresource.ProvinceCity.ProvinceCityRequest;
import com.example.demo.dto.humanresource.ProvinceCity.ProvinceCityResponse;
import com.example.demo.entity.humanresource.ProvinceCity;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.CannotDeleteException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.ProvinceCityMapper;
import com.example.demo.repository.humanresource.*;
import com.example.demo.util.BulkOperationUtils;
import com.example.demo.util.BulkOperationUtils.BatchClassification;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class ProvinceCityService {
    final ProvinceCityRepository provinceCityRepository;
    final ProvinceCityMapper provinceCityMapper;
    final WardRepository wardRepository;
    final OldProvinceCityRepository oldProvinceCityRepository;
    final MedicalFacilityRepository medicalFacilityRepository;
    final EmployeeRepository employeeRepository;
    final EntityManager entityManager;


    @Value("${entities.humanresource.provincecity}")
    private String entityName;

    // ----------------------------------- Handle Single -----------------------------------------
    public ProvinceCityResponse createProvinceCity(ProvinceCityRequest request) {
        // Check source_id uniqueness for create
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            provinceCityRepository.findBySourceId(request.getSourceId()).ifPresent(b -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        ProvinceCity provinceCity = provinceCityMapper.toProvinceCity(request);

        return provinceCityMapper.toProvinceCityResponse(provinceCityRepository.save(provinceCity));
    }


    public List<ProvinceCityResponse> getProvinceCities(Pageable pageable) {
        Page<ProvinceCity> page = provinceCityRepository.findAll(pageable);
        return page.getContent()
                .stream().map(provinceCityMapper::toProvinceCityResponse).toList();
    }

    public ProvinceCityResponse getProvinceCity(Long id) {
        return provinceCityMapper.toProvinceCityResponse(provinceCityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public ProvinceCityResponse updateProvinceCity(Long id, ProvinceCityRequest request) {
        ProvinceCity provinceCity = provinceCityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        // Check source_id uniqueness for update
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            provinceCityRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getProvinceCityId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }

        provinceCityMapper.updateProvinceCity(provinceCity, request);

        return provinceCityMapper.toProvinceCityResponse(provinceCityRepository.save(provinceCity));
    }

    public void deleteProvinceCity(Long id) {
        checkForeignKeyConstraints(id);

        provinceCityRepository.deleteById(id);
    }

    // ----------------------------------- Handle Bulk -----------------------------------------

    /**
     * Bulk Upsert với Final Batch Logic, Partial Success Pattern:
     * - Safe Batch: saveAll() - requests không có unique conflicts
     * - Final Batch: save + flush từng request - requests có potential conflicts
     * - Trả về detailed result với success/failure breakdown
     */
    public BulkOperationResult<ProvinceCityResponse> bulkUpsertProvinceCities(
            List<ProvinceCityRequest> requests) {

        log.info("Starting bulk upsert for {} province cities", requests.size());
        long startTime = System.currentTimeMillis();

        // 1. Setup unique field extractors
        Map<String, Function<ProvinceCityRequest, String>> uniqueFieldExtractors = new LinkedHashMap<>();
        uniqueFieldExtractors.put("source_id", ProvinceCityRequest::getSourceId);
        uniqueFieldExtractors.put("name", ProvinceCityRequest::getName);

        // 2. Extract unique values
        Set<String> allSourceIds = BulkOperationUtils.extractUniqueValues(
                requests, ProvinceCityRequest::getSourceId);
        Set<String> allNames = BulkOperationUtils.extractUniqueValues(
                requests, ProvinceCityRequest::getName);

        // 3. Query existing values từ DB
        Map<String, Set<String>> existingValuesMaps = new HashMap<>();
        existingValuesMaps.put("source_id",
                provinceCityRepository.findBySourceIdIn(allSourceIds)
                        .stream()
                        .map(ProvinceCity::getSourceId)
                        .collect(Collectors.toSet()));
        existingValuesMaps.put("name",
                provinceCityRepository.findByNameIn(allNames)
                        .stream()
                        .map(ProvinceCity::getName)
                        .collect(Collectors.toSet()));

        // 4.  Phân loại batch
        BatchClassification<ProvinceCityRequest> classification =
                BulkOperationUtils.classifyBatchByUniqueFields(
                        requests, uniqueFieldExtractors, existingValuesMaps);

        // Initialize result tracking
        List<ProvinceCityResponse> successResults = new ArrayList<>();
        List<BulkOperationError> errors = new ArrayList<>();

        // 5. Process Safe Batch
        if (classification.hasSafeBatch()) {
            log.info("Processing safe batch: {} requests", classification.getSafeBatch().size());
            processSafeBatchWithTracking(
                    classification.getSafeBatch(),
                    existingValuesMaps,
                    successResults,
                    errors
                    // starting index
            );
        }

        // 6. Process Final Batch
        if (classification.hasFinalBatch()) {
            log.warn("Processing final batch: {} requests (potential conflicts)",
                    classification.getFinalBatch().size());
            int finalBatchStartIndex = classification.getSafeBatch().size();
            processFinalBatchWithTracking(
                    classification.getFinalBatch(),
                    successResults,
                    errors,
                    finalBatchStartIndex
            );
        }

        // 7. Build result
        long duration = System.currentTimeMillis() - startTime;
        BulkOperationResult<ProvinceCityResponse> result = buildBulkOperationResult(
                requests.size(),
                successResults,
                errors,
                duration
        );

        log.info("Bulk upsert completed: {}/{} succeeded, {}/{} failed in {}ms",
                result.getSuccessCount(), result.getTotalRequests(),
                result.getFailedCount(), result.getTotalRequests(),
                duration);

        return result;
    }

    /**
     * Process Safe Batch với error tracking
     */
    private void processSafeBatchWithTracking(
            List<ProvinceCityRequest> safeBatch,
            Map<String, Set<String>> existingValuesMaps,
            List<ProvinceCityResponse> successResults,
            List<BulkOperationError> errors) {

        Set<String> existingSourceIds = existingValuesMaps.get("source_id");
        List<ProvinceCity> existingEntities = provinceCityRepository.findBySourceIdIn(existingSourceIds);
        Map<String, ProvinceCity> existingMap = BulkOperationUtils.toMap(
                existingEntities, ProvinceCity::getSourceId);

        List<ProvinceCity> entitiesToSave = new ArrayList<>();
        Map<Integer, ProvinceCityRequest> indexToRequestMap = new HashMap<>();

        // Build entities to save
        for (int globalIndex = 0; globalIndex < safeBatch.size(); globalIndex++) {
            ProvinceCityRequest request = safeBatch.get(globalIndex);

            try {
                ProvinceCity entity;
                String sid = request.getSourceId();

                if (sid != null && !sid.trim().isEmpty() && existingMap.containsKey(sid)) {
                    entity = existingMap.get(sid);
                    provinceCityMapper.updateProvinceCity(entity, request);
                    log.debug("Safe batch [{}]: Updating source_id={}", globalIndex, sid);
                } else {
                    entity = provinceCityMapper.toProvinceCity(request);
                    log.debug("Safe batch [{}]: Creating source_id={}", globalIndex, sid);
                }

                entitiesToSave.add(entity);
                indexToRequestMap.put(globalIndex, request);

            } catch (Exception e) {
                log.error("Safe batch [{}]: Preparation failed - {}", globalIndex, e.getMessage());
                errors.add(buildError(globalIndex, request, "Preparation failed", e));
            }
        }

        // Save all and track results
        try {
            List<ProvinceCity> savedEntities = provinceCityRepository.saveAll(entitiesToSave);

            for (ProvinceCity saved : savedEntities) {
                successResults.add(provinceCityMapper.toProvinceCityResponse(saved));
            }

        } catch (Exception e) {
            log.error("Safe batch: saveAll failed - {}", e.getMessage());

            // If batch save fails, mark all as errors
            for (int globalIndex = 0; globalIndex < entitiesToSave.size(); globalIndex++) {
                ProvinceCityRequest request = indexToRequestMap.get(globalIndex);
                errors.add(buildError(globalIndex, request, "Batch save failed", e));
            }
        }
    }

    /**
     * Process Final Batch với individual save + tracking
     */
    private void processFinalBatchWithTracking(
            List<ProvinceCityRequest> finalBatch,
            List<ProvinceCityResponse> successResults,
            List<BulkOperationError> errors,
            int startIndex) {

        for (int i = 0; i < finalBatch.size(); i++) {
            ProvinceCityRequest request = finalBatch.get(i);
            int globalIndex = startIndex + i;

            try {
                ProvinceCity entity = findExistingEntityForUpsert(request);

                if (entity != null) {
                    log.debug("Final batch [{}]: Updating entity id={}",
                            globalIndex, entity.getProvinceCityId());
                    provinceCityMapper.updateProvinceCity(entity, request);
                } else {
                    log.debug("Final batch [{}]: Creating new entity", globalIndex);
                    entity = provinceCityMapper.toProvinceCity(request);
                }

                // Save + flush individual record
                ProvinceCity saved = provinceCityRepository.saveAndFlush(entity);
                entityManager.clear();

                successResults.add(provinceCityMapper.toProvinceCityResponse(saved));

            } catch (Exception e) {
                log.error("Final batch [{}]: Failed - source_id={}, name={}, error: {}",
                        globalIndex, request.getSourceId(), request.getName(), e.getMessage());
                errors.add(buildError(globalIndex, request, "Save failed", e));
            }
        }
    }

    /**
     * Helper: Build error object
     */
    private BulkOperationError buildError(
            int index,
            ProvinceCityRequest request,
            String context,
            Exception e) {

        String identifier = String.format("source_id=%s, name=%s",
                request.getSourceId(), request.getName());

        String errorMessage = context + ": " +
                (e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName());

        return BulkOperationError.builder()
                .index(index)
                .identifier(identifier)
                .requestDetails(null)  // Không expose sensitive data
                .errorMessage(errorMessage)
                .errorType(e.getClass().getSimpleName())
                .build();
    }

    /**
     * Helper: Build final result
     */
    private BulkOperationResult<ProvinceCityResponse> buildBulkOperationResult(
            int totalRequests,
            List<ProvinceCityResponse> successResults,
            List<BulkOperationError> errors,
            long durationMs) {

        int successCount = successResults.size();
        int failedCount = errors.size();

        String summary = String.format(
                "Bulk upsert completed: %d/%d succeeded, %d/%d failed (%.2fs)",
                successCount, totalRequests,
                failedCount, totalRequests,
                durationMs / 1000.0
        );

        return BulkOperationResult.<ProvinceCityResponse>builder()
                .totalRequests(totalRequests)
                .successCount(successCount)
                .failedCount(failedCount)
                .successResults(successResults)
                .errors(errors)
                .summary(summary)
                .build();
    }

    /**
     * Helper: Find existing entity for upsert
     */
    private ProvinceCity findExistingEntityForUpsert(ProvinceCityRequest request) {
        if (request.getSourceId() != null && !request.getSourceId().trim().isEmpty()) {
            Optional<ProvinceCity> bySourceId = provinceCityRepository.findBySourceId(request.getSourceId());
            if (bySourceId.isPresent()) {
                return bySourceId.get();
            }
        }

        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            Optional<ProvinceCity> byName = provinceCityRepository.findByName(request.getName());
            if (byName.isPresent()) {
                return byName.get();
            }
        }

        return null;
    }

    // ========================= BULK DELETE  ========================

    public void bulkDeleteProvinceCities(List<Long> ids) {
        log.info("Starting bulk delete for {} province cities", ids.size());

        Set<Long> uniqueIds = BulkOperationUtils.validateAndExtractUniqueValues(ids, "ID");
        List<Long> idList = new ArrayList<>(uniqueIds);

        List<ProvinceCity> existingEntities = provinceCityRepository.findByProvinceCityIdIn(idList);

        if (existingEntities.size() != idList.size()) {
            Set<Long> foundIds = existingEntities.stream()
                    .map(ProvinceCity::getProvinceCityId)
                    .collect(Collectors.toSet());
            List<Long> missingIds = idList.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();
            throw new NotFoundException(entityName + "s not found with IDs: " + missingIds);
        }

        for (Long id : idList) {
            checkForeignKeyConstraints(id);
        }

        provinceCityRepository.deleteAllById(idList);
        log.info("Bulk delete completed: {} province cities deleted", idList.size());
    }

    private void checkForeignKeyConstraints(Long id) {
        if (!provinceCityRepository.existsById(id)) {
            throw new NotFoundException(entityName);
        }

        long wardCount = wardRepository.countByProvinceCity_ProvinceCityId(id);
        long oldProvinceCityCount = oldProvinceCityRepository.countByProvinceCity_ProvinceCityId(id);
        long medicalFacilityCount = medicalFacilityRepository.countByProvinceCity_ProvinceCityId(id);
        long empHometownCount = employeeRepository.countByHometown_ProvinceCityId(id);
        long empBirthplaceCount = employeeRepository.countByPlaceOfBirth_ProvinceCityId(id);
        long totalCount = wardCount + oldProvinceCityCount + medicalFacilityCount +
                empHometownCount + empBirthplaceCount;

        if (totalCount > 0) {
            throw new CannotDeleteException("ProvinceCity", id, "referencing records", totalCount);
        }
    }
}
