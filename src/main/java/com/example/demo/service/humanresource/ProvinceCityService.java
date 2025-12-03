package com.example.demo.service.humanresource;

import com.example.demo.dto.BulkOperationResult;
import com.example.demo.dto.humanresource.ProvinceCity.ProvinceCityRequest;
import com.example.demo.dto.humanresource.ProvinceCity.ProvinceCityResponse;
import com.example.demo.entity.humanresource.ProvinceCity;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.CannotDeleteException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.ProvinceCityMapper;
import com.example.demo.repository.humanresource.*;
import com.example.demo.util.bulk.*;
import com.example.demo.util.bulk.BulkDeleteConfig;
import com.example.demo.util.bulk.BulkDeleteProcessor;
import com.example.demo.util.bulk.BulkUpsertConfig;
import com.example.demo.util.bulk.BulkUpsertProcessor;
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

        // 1. Define unique field configurations (ProvinceCity has 2 unique fields)
        UniqueFieldConfig<ProvinceCityRequest> sourceIdConfig =
                new UniqueFieldConfig<>("source_id", ProvinceCityRequest::getSourceId);
        UniqueFieldConfig<ProvinceCityRequest> nameConfig =
                new UniqueFieldConfig<>("name", ProvinceCityRequest::getName);

        // 2. Define entity fetchers for each unique field
        Map<String, Function<Set<String>, List<ProvinceCity>>> entityFetchers = new HashMap<>();
        entityFetchers.put("source_id", provinceCityRepository::findBySourceIdIn);
        entityFetchers.put("name", provinceCityRepository::findByNameIn);

        // 3. Setup unique fields using helper
        UniqueFieldsSetupHelper.UniqueFieldsSetup<ProvinceCityRequest> setup =
                UniqueFieldsSetupHelper.buildUniqueFieldsSetup(
                        requests,
                        entityFetchers,
                        ProvinceCity::getSourceId, // Can use any unique field extractor
                        sourceIdConfig,
                        nameConfig
                );

        // 4.  Build bulk upsert config
        BulkUpsertConfig<ProvinceCityRequest, ProvinceCity, ProvinceCityResponse> config =
                BulkUpsertConfig.<ProvinceCityRequest, ProvinceCity, ProvinceCityResponse>builder()
                        .uniqueFieldExtractors(setup.getUniqueFieldExtractors())
                        .existingValuesMaps(setup.getExistingValuesMaps())
                        .entityToResponseMapper(provinceCityMapper::toProvinceCityResponse)
                        .requestToEntityMapper(provinceCityMapper::toProvinceCity)
                        .entityUpdater(provinceCityMapper::updateProvinceCity)
                        .existingEntityFinder(this::findExistingEntityForUpsert)
                        .repositorySaver(provinceCityRepository::saveAll)
                        .repositorySaveAndFlusher(provinceCityRepository::saveAndFlush)
                        .entityManagerClearer(entityManager::clear)
                        .build();

        // 5. Execute bulk upsert
        BulkUpsertProcessor<ProvinceCityRequest, ProvinceCity, ProvinceCityResponse> processor =
                new BulkUpsertProcessor<>(config);

        return processor.execute(requests);
    }

    /**
     * Helper: Find existing entity
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

    public BulkOperationResult<Long> bulkDeleteProvinceCities(List<Long> ids) {

        // Build config
        BulkDeleteConfig<ProvinceCity> config = BulkDeleteConfig.<ProvinceCity>builder()
                .entityFinder(id -> provinceCityRepository.findById(id).orElse(null))
                .foreignKeyConstraintsChecker(this::checkForeignKeyConstraints)
                .repositoryDeleter(provinceCityRepository::deleteById)
                .entityName(entityName)
                .build();

        // Execute với processor
        BulkDeleteProcessor<ProvinceCity> processor = new BulkDeleteProcessor<>(config);

        return processor.execute(ids);
    }

    /**
     * Helper: Check FK constraints
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
