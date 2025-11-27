package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.ProvinceCity.ProvinceCityRequest;
import com.example.demo.dto.humanresource.ProvinceCity.ProvinceCityResponse;
import com.example.demo.entity.humanresource.ProvinceCity;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.CannotDeleteException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.ProvinceCityMapper;
import com.example.demo.repository.humanresource.ProvinceCityRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.demo.repository.humanresource.WardRepository;
import com.example.demo.repository.humanresource.OldProvinceCityRepository;
import com.example.demo.repository.humanresource.MedicalFacilityRepository;
import com.example.demo.repository.humanresource.EmployeeRepository;
import com.example.demo.util.BulkOperationUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProvinceCityService {
    final ProvinceCityRepository provinceCityRepository;
    final ProvinceCityMapper provinceCityMapper;
    final WardRepository wardRepository;
    final OldProvinceCityRepository oldProvinceCityRepository;
    final MedicalFacilityRepository medicalFacilityRepository;
    final EmployeeRepository employeeRepository;

    @Value("${entities.humanresource.provincecity}")
    private String entityName;

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

    /**
     * Xử lý Bulk Upsert
     */
    @Transactional
    public List<ProvinceCityResponse> bulkUpsertProvinceCities(List<ProvinceCityRequest> requests) {
        // 1. Lấy list source_id
        List<String> sourceIds = requests.stream()
                .map(ProvinceCityRequest::getSourceId)
                .filter(Objects::nonNull)
                .filter(s -> !s.isEmpty())
                .toList();

        // 2. Lấy các entity hiện có từ DB theo source_id
        List<ProvinceCity> existingEntities = provinceCityRepository.findBySourceIdIn(sourceIds);

        Map<String, ProvinceCity> existingMap =
                BulkOperationUtils.toMap(existingEntities, ProvinceCity::getSourceId);


        // 3. Phân loại create vs update
        List<ProvinceCity> entitiesToSave = new ArrayList<>();

        for (ProvinceCityRequest request : requests) {
            ProvinceCity entity;
            if (request.getSourceId() != null && existingMap.containsKey(request.getSourceId())) {
                // UPDATE - entity đã tồn tại
                entity = existingMap.get(request.getSourceId());
                provinceCityMapper.updateProvinceCity(entity, request);
            } else {
                // CREATE - entity mới
                entity = provinceCityMapper.toProvinceCity(request);
            }
            entitiesToSave.add(entity);
        }

        // 4. Lưu tất cả trong 1 batch operation
        List<ProvinceCity> savedEntities = provinceCityRepository.saveAll(entitiesToSave);

        // 5. Map về Response DTO
        return savedEntities.stream()
                .map(provinceCityMapper::toProvinceCityResponse)
                .toList();
    }

    /**
     * Xử lý Bulk Delete
     */
    @Transactional
    public void bulkDeleteProvinceCities(List<Long> ids) {
        // 1. Validate: Remove duplicates và check tồn tại
        Set<Long> uniqueIds = BulkOperationUtils.validateAndExtractUniqueValues(ids, "ID");

        List<Long> idList = new ArrayList<>(uniqueIds);

        List<ProvinceCity> existingEntities = provinceCityRepository.findByProvinceCityIdIn(idList);
        // check tồn tại
        if (existingEntities.size() != idList.size()) {
            Set<Long> foundIds = existingEntities.stream()
                    .map(ProvinceCity::getProvinceCityId)
                    .collect(Collectors.toSet());
            List<Long> missingIds = idList.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();
            throw new NotFoundException(entityName + "s not found with IDs: " + missingIds);
        }

        // 2.  Batch check FK constraints
        checkForeignKeyConstraintsBatch(idList);

        // 3.  Thực hiện delete all
        provinceCityRepository.deleteAllById(idList);
    }

    /**
     * Batch check FK constraints - chỉ 5 queries dành cho tất cả IDs tương ứng với 5 bảng referencing
     */
    private void checkForeignKeyConstraintsBatch(List<Long> provinceCityIds) {
        // 1 query cho Ward
        Map<Long, Long> wardCounts = BulkOperationUtils.toIdCountMap(
                wardRepository.countByProvinceCityIdIn(provinceCityIds)
        );

        // 1 query cho OldProvinceCity
        Map<Long, Long> oldProvinceCityCounts = BulkOperationUtils.toIdCountMap(
                oldProvinceCityRepository.countByProvinceCityIdIn(provinceCityIds)
        );

        // 1 query cho MedicalFacility
        Map<Long, Long> medicalFacilityCounts = BulkOperationUtils.toIdCountMap(
                medicalFacilityRepository.countByProvinceCityIdIn(provinceCityIds)
        );

        // 1 query cho Employee hometown
        Map<Long, Long> empHometownCounts = BulkOperationUtils.toIdCountMap(
                employeeRepository.countHometownByProvinceCityIdIn(provinceCityIds)
        );

        // 1 query cho Employee birthplace
        Map<Long, Long> empBirthplaceCounts = BulkOperationUtils.toIdCountMap(
                employeeRepository.countPlaceOfBirthByProvinceCityIdIn(provinceCityIds)
        );

        // Check từng ID
        List<Long> idsWithReferences = new ArrayList<>();
        Map<Long, Long> referenceCounts = new HashMap<>();

        for (Long id : provinceCityIds) {
            long totalCount =
                    wardCounts.getOrDefault(id, 0L) +
                            oldProvinceCityCounts.getOrDefault(id, 0L) +
                            medicalFacilityCounts.getOrDefault(id, 0L) +
                            empHometownCounts.getOrDefault(id, 0L) +
                            empBirthplaceCounts.getOrDefault(id, 0L);

            if (totalCount > 0) {
                idsWithReferences.add(id);
                referenceCounts.put(id, totalCount);
            }
        }

        if (!idsWithReferences.isEmpty()) {
            throw new CannotDeleteException(
                    String.format("Cannot delete %d ProvinceCity/Cities because they are still referenced.  " +
                                    "IDs with references: %s.  Reference counts: %s",
                            idsWithReferences.size(), idsWithReferences, referenceCounts)
            );
        }
    }


    /**
     * Validate duplicate source_ids và trả về Set sourceIds hợp lệ
     */
    private Set<String> validateAndExtractSourceIds(List<ProvinceCityRequest> requests) {
        Set<String> sourceIds = new HashSet<>();

        for (ProvinceCityRequest request : requests) {
            String sid = request.getSourceId();

            // Chỉ xử lý source_id hợp lệ (not null và not empty)
            if (sid != null && !sid.isEmpty() && !sourceIds.add(sid)) {
                throw new IllegalArgumentException("Duplicate source_id in request: " + sid);
            }

        }

        return sourceIds;
    }

    /**
     * Helper: Check FK constraints
     */
    private void checkForeignKeyConstraints(Long id) {
        if (!provinceCityRepository.existsById(id)) {
            throw new NotFoundException(entityName);
        }

        // Check all references (RESTRICT strategy)
        long wardCount = wardRepository.countByProvinceCity_ProvinceCityId(id);
        long oldProvinceCityCount = oldProvinceCityRepository.countByProvinceCity_ProvinceCityId(id);
        long medicalFacilityCount = medicalFacilityRepository.countByProvinceCity_ProvinceCityId(id);
        long empHometownCount = employeeRepository.countByHometown_ProvinceCityId(id);
        long empBirthplaceCount = employeeRepository.countByPlaceOfBirth_ProvinceCityId(id);
        long totalCount = wardCount + oldProvinceCityCount + medicalFacilityCount + empHometownCount + empBirthplaceCount;

        if (totalCount > 0) {
            throw new CannotDeleteException(
                    "ProvinceCity", id, "referencing records", totalCount
            );
        }
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
}
