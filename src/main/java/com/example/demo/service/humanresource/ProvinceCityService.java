package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.ProvinceCity.ProvinceCityRequest;
import com.example.demo.dto.humanresource.ProvinceCity.ProvinceCityResponse;
import com.example.demo.entity.humanresource.ProvinceCity;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.BulkOperationException;
import com.example.demo.exception.CannotDeleteException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.ProvinceCityMapper;
import com.example.demo.repository.humanresource.*;
import com.example.demo.util.BulkOperationUtils;
import com.example.demo.util.BulkOperationUtils.BatchClassification;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
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
     * Bulk Upsert với Final Batch Logic:
     * - Safe Batch: saveAll() - requests không có unique conflicts
     * - Final Batch: save + flush từng request - requests có potential conflicts
     */
    @Transactional
    public List<ProvinceCityResponse> bulkUpsertProvinceCities(List<ProvinceCityRequest> requests) {
        log.info("Starting bulk upsert for {} province cities", requests.size());

        // 1. Setup unique field extractors (ProvinceCity có 2 unique fields)
        Map<String, Function<ProvinceCityRequest, String>> uniqueFieldExtractors = new LinkedHashMap<>();
        uniqueFieldExtractors.put("source_id", ProvinceCityRequest::getSourceId);
        uniqueFieldExtractors.put("name", ProvinceCityRequest::getName);

        // 2. Extract tất cả unique values từ requests
        Set<String> allSourceIds = BulkOperationUtils.extractUniqueValues(
                requests, ProvinceCityRequest::getSourceId);
        Set<String> allNames = BulkOperationUtils.extractUniqueValues(
                requests, ProvinceCityRequest::getName);

        // 3. Query existing values từ DB (2 queries cho 2 unique fields)
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

        // 4. Phân loại requests thành safe batch và final batch
        BatchClassification<ProvinceCityRequest> classification =
                BulkOperationUtils.classifyBatchByUniqueFields(
                        requests, uniqueFieldExtractors, existingValuesMaps);

        List<ProvinceCityResponse> allResults = new ArrayList<>();

        // 5. Xử lý Safe Batch (saveAll - không có conflicts)
        if (classification.hasSafeBatch()) {
            log.info("Processing safe batch: {} requests", classification.getSafeBatch().size());
            List<ProvinceCityResponse> safeBatchResults = processSafeBatch(
                    classification.getSafeBatch(), existingValuesMaps);
            allResults.addAll(safeBatchResults);
        }

        // 6. Xử lý Final Batch (save + flush từng request - có potential conflicts)
        if (classification.hasFinalBatch()) {
            log.warn("Processing final batch: {} requests (potential conflicts)",
                    classification.getFinalBatch().size());
            List<ProvinceCityResponse> finalBatchResults = processFinalBatch(
                    classification.getFinalBatch());
            allResults.addAll(finalBatchResults);
        }

        log.info("Bulk upsert completed: {} total results", allResults.size());
        return allResults;
    }

    /**
     * Xử lý Safe Batch - saveAll() cùng lúc
     */
    private List<ProvinceCityResponse> processSafeBatch(
            List<ProvinceCityRequest> safeBatch,
            Map<String, Set<String>> existingValuesMaps) {

        // Build existing map từ DB (chỉ cần query by source_id vì là primary lookup key)
        Set<String> existingSourceIds = existingValuesMaps.get("source_id");
        List<ProvinceCity> existingEntities = provinceCityRepository.findBySourceIdIn(existingSourceIds);
        Map<String, ProvinceCity> existingMap = BulkOperationUtils.toMap(
                existingEntities, ProvinceCity::getSourceId);

        List<ProvinceCity> entitiesToSave = new ArrayList<>();

        for (ProvinceCityRequest request : safeBatch) {
            ProvinceCity entity;
            String sid = request.getSourceId();

            // Phân loại CREATE vs UPDATE dựa trên source_id
            if (sid != null && !sid.trim().isEmpty() && existingMap.containsKey(sid)) {
                // UPDATE - entity đã tồn tại
                entity = existingMap.get(sid);
                provinceCityMapper.updateProvinceCity(entity, request);
                log.debug("Safe batch: Updating existing entity with source_id={}", sid);
            } else {
                // CREATE - entity mới
                entity = provinceCityMapper.toProvinceCity(request);
                log.debug("Safe batch: Creating new entity with source_id={}", sid);
            }
            entitiesToSave.add(entity);
        }

        // Save all cùng lúc (batch operation)
        List<ProvinceCity> savedEntities = provinceCityRepository.saveAll(entitiesToSave);
        return savedEntities.stream()
                .map(provinceCityMapper::toProvinceCityResponse)
                .toList();
    }

    /**
     * Xử lý Final Batch - save + flush từng request
     * Try-catch mỗi request, nếu fail → collect errors → rollback toàn bộ transaction
     */
    private List<ProvinceCityResponse> processFinalBatch(List<ProvinceCityRequest> finalBatch) {
        List<ProvinceCityResponse> results = new ArrayList<>();
        List<String> failedRequests = new ArrayList<>();

        for (int i = 0; i < finalBatch.size(); i++) {
            ProvinceCityRequest request = finalBatch.get(i);
            try {
                // Tìm existing entity (check cả source_id và name)
                ProvinceCity entity = findExistingEntityForUpsert(request);

                if (entity != null) {
                    // UPDATE - entity đã tồn tại
                    log.debug("Final batch [{}]: Updating entity id={}, source_id={}",
                            i, entity.getProvinceCityId(), entity.getSourceId());
                    provinceCityMapper.updateProvinceCity(entity, request);
                } else {
                    // CREATE - entity mới
                    log.debug("Final batch [{}]: Creating new entity with source_id={}, name={}",
                            i, request.getSourceId(), request.getName());
                    entity = provinceCityMapper.toProvinceCity(request);
                }

                // Save + flush ngay lập tức để commit vào DB
                ProvinceCity saved = provinceCityRepository.saveAndFlush(entity);

                // Clear persistence context để tránh memory issues với large batches
                entityManager.clear();

                results.add(provinceCityMapper.toProvinceCityResponse(saved));

            } catch (Exception e) {
                log.error("Final batch [{}]: Failed - source_id={}, name={}.  Error: {}",
                        i, request.getSourceId(), request.getName(), e.getMessage(), e);
                failedRequests.add(String.format("[%d] source_id=%s, name=%s: %s",
                        i, request.getSourceId(), request.getName(), e.getMessage()));
            }
        }

        // Nếu có lỗi → throw exception để rollback toàn bộ transaction
        if (!failedRequests.isEmpty()) {
            log.error("Final batch processing failed for {}/{} requests",
                    failedRequests.size(), finalBatch.size());
            throw new BulkOperationException("Final Batch Upsert", finalBatch.size(), failedRequests);
        }

        return results;
    }

    /**
     * Helper: Tìm entity đã tồn tại (check cả source_id và name vì cả 2 đều unique)
     */
    private ProvinceCity findExistingEntityForUpsert(ProvinceCityRequest request) {
        // Priority 1: Check by source_id (primary key cho upsert logic)
        if (request.getSourceId() != null && !request.getSourceId().trim().isEmpty()) {
            Optional<ProvinceCity> bySourceId = provinceCityRepository.findBySourceId(request.getSourceId());
            if (bySourceId.isPresent()) {
                return bySourceId.get();
            }
        }

        // Priority 2: Check by name (vì name cũng là unique)
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            Optional<ProvinceCity> byName = provinceCityRepository.findByName(request.getName());
            if (byName.isPresent()) {
                return byName.get();
            }
        }

        return null;
    }

    // ========== BULK DELETE ==========

    /**
     * Bulk Delete với validation đầy đủ
     */
    @Transactional
    public void bulkDeleteProvinceCities(List<Long> ids) {
        log.info("Starting bulk delete for {} province cities", ids.size());

        // 1. Validate và remove duplicates
        Set<Long> uniqueIds = BulkOperationUtils.validateAndExtractUniqueValues(ids, "ID");
        List<Long> idList = new ArrayList<>(uniqueIds);

        // 2. Check tất cả IDs tồn tại
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

        // 3.  Batch check FK constraints
        checkForeignKeyConstraintsBatch(idList);

        // 4. Delete all
        provinceCityRepository.deleteAllById(idList);
        log.info("Bulk delete completed: {} province cities deleted", idList.size());
    }

    /**
     * Batch check FK constraints - 5 queries thay vì N*5 queries
     */
    private void checkForeignKeyConstraintsBatch(List<Long> provinceCityIds) {
        // Cần implement các batch count methods trong repositories
        // Tạm thời giữ logic đơn giản - sẽ optimize sau
        for (Long id : provinceCityIds) {
            checkForeignKeyConstraints(id);
        }
    }

    /**
     * Single FK check - dùng cho single delete và temporary cho bulk delete
     */
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
