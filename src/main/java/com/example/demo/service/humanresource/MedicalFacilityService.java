package com.example.demo.service.humanresource;

import com.example.demo.dto.BulkOperationResult;
import com.example.demo.dto.humanresource.MedicalFacility.MedicalFacilityRequest;
import com.example.demo.dto.humanresource.MedicalFacility.MedicalFacilityResponse;
import com.example.demo.entity.humanresource.MedicalFacility;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.MedicalFacilityMapper;
import com.example.demo.repository.humanresource.MedicalFacilityRepository;
import com.example.demo.repository.humanresource.EmployeeRepository;
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
public class MedicalFacilityService {
    final MedicalFacilityRepository medicalFacilityRepository;
    final MedicalFacilityMapper medicalFacilityMapper;
    final EmployeeRepository employeeRepository;
    final EntityManager entityManager;

    @Value("${entities.humanresource.medicalfacility}")
    private String entityName;

    public MedicalFacilityResponse createMedicalFacility(MedicalFacilityRequest request) {
        // Check source_id uniqueness for create
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            medicalFacilityRepository.findBySourceId(request.getSourceId()).ifPresent(b -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        MedicalFacility medicalFacility = medicalFacilityMapper.toMedicalFacility(request);
        // Set FK references from IDs in request
        medicalFacilityMapper.setReferences(medicalFacility, request);

        return medicalFacilityMapper.toMedicalFacilityResponse(medicalFacilityRepository.save(medicalFacility));
    }


    /**
     * Bulk Upsert với Final Batch Logic, Partial Success Pattern:
     * - Safe Batch: saveAll() - requests không có unique conflicts
     * - Final Batch: save + flush từng request - requests có potential conflicts
     * - Trả về detailed result với success/failure breakdown
     */
    public BulkOperationResult<MedicalFacilityResponse> bulkUpsertMedicalFacilities(
            List<MedicalFacilityRequest> requests) {

        // 1. Define unique field configurations (MedicalFacility has 2 unique fields)
        UniqueFieldConfig<MedicalFacilityRequest> sourceIdConfig =
                new UniqueFieldConfig<>("source_id", MedicalFacilityRequest::getSourceId);
        UniqueFieldConfig<MedicalFacilityRequest> codeConfig =
                new UniqueFieldConfig<>("medical_facility_code", MedicalFacilityRequest::getMedicalFacilityCode);
        UniqueFieldConfig<MedicalFacilityRequest> nameConfig =
                new UniqueFieldConfig<>("name", MedicalFacilityRequest::getName);

        // 2. Define entity fetchers for each unique field
        Map<String, Function<Set<String>, List<MedicalFacility>>> entityFetchers = new HashMap<>();
        entityFetchers.put("source_id", medicalFacilityRepository::findBySourceIdIn);
        entityFetchers.put("medical_facility_code", medicalFacilityRepository::findByMedicalFacilityCodeIn);
        entityFetchers.put("name", medicalFacilityRepository::findByNameIn);

        // 2.5. Define entity field extractors
        Map<String, Function<MedicalFacility, String>> entityFieldExtractors = new HashMap<>();
        entityFieldExtractors.put("source_id", MedicalFacility::getSourceId);
        entityFieldExtractors.put("medical_facility_code", MedicalFacility::getMedicalFacilityCode);
        entityFieldExtractors.put("name", MedicalFacility::getName);

        // 3. Setup unique fields using helper
        UniqueFieldsSetupHelper.UniqueFieldsSetup<MedicalFacilityRequest> setup =
                UniqueFieldsSetupHelper.buildUniqueFieldsSetup(
                        requests,
                        entityFetchers,
                        entityFieldExtractors,
                        sourceIdConfig,
                        codeConfig,
                        nameConfig
                );

        // 4.  Build bulk upsert config
        BulkUpsertConfig<MedicalFacilityRequest, MedicalFacility, MedicalFacilityResponse> config =
                BulkUpsertConfig.<MedicalFacilityRequest, MedicalFacility, MedicalFacilityResponse>builder()
                        .uniqueFieldExtractors(setup.getUniqueFieldExtractors())
                        .existingValuesMaps(setup.getExistingValuesMaps())
                        .entityToResponseMapper(medicalFacilityMapper::toMedicalFacilityResponse)
                        .requestToEntityMapper(medicalFacilityMapper::toMedicalFacility)
                        .entityUpdater(medicalFacilityMapper::updateMedicalFacility)
                        .existingEntityFinder(this::findExistingEntityForUpsert)
                        .repositorySaver(medicalFacilityRepository::saveAll)
                        .repositorySaveAndFlusher(medicalFacilityRepository::saveAndFlush)
                        .entityManagerClearer(entityManager::clear)
                        .build();

        // 5. Execute bulk upsert
        BulkUpsertProcessor<MedicalFacilityRequest, MedicalFacility, MedicalFacilityResponse> processor =
                new BulkUpsertProcessor<>(config);

        return processor.execute(requests);
    }

    /**
     * Helper: Find existing entity
     */
    private MedicalFacility findExistingEntityForUpsert(MedicalFacilityRequest request) {
        if (request.getSourceId() != null && !request.getSourceId().trim().isEmpty()) {
            Optional<MedicalFacility> bySourceId = medicalFacilityRepository.findBySourceId(request.getSourceId());
            if (bySourceId.isPresent()) {
                return bySourceId.get();
            }
        }

        return null;
    }

    // ========================= BULK DELETE  ========================

    public BulkOperationResult<Long> bulkDeleteMedicalFacilities(List<Long> ids) {

        // Build config
        BulkDeleteConfig<MedicalFacility> config = BulkDeleteConfig.<MedicalFacility>builder()
                .entityFinder(id -> medicalFacilityRepository.findById(id).orElse(null))
                .foreignKeyConstraintsChecker(this::checkForeignKeyConstraints)
                .repositoryDeleter(medicalFacilityRepository::deleteById)
                .entityName(entityName)
                .build();

        // Execute với processor
        BulkDeleteProcessor<MedicalFacility> processor = new BulkDeleteProcessor<>(config);

        return processor.execute(ids);
    }

    public List<MedicalFacilityResponse> getMedicalFacilities(Pageable pageable) {
        Page<MedicalFacility> page = medicalFacilityRepository.findAll(pageable);
        return page.getContent()
                .stream().map(medicalFacilityMapper::toMedicalFacilityResponse).toList();
    }

    public MedicalFacilityResponse getMedicalFacility(Long id) {
        return medicalFacilityMapper.toMedicalFacilityResponse(medicalFacilityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public MedicalFacilityResponse updateMedicalFacility(Long id, MedicalFacilityRequest request) {
        MedicalFacility medicalFacility = medicalFacilityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        // Check source_id uniqueness for update
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            medicalFacilityRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getMedicalFacilityId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }

        medicalFacilityMapper.updateMedicalFacility(medicalFacility, request);
        // Set FK references from IDs in request
        medicalFacilityMapper.setReferences(medicalFacility, request);

        return medicalFacilityMapper.toMedicalFacilityResponse(medicalFacilityRepository.save(medicalFacility));
    }

    public void deleteMedicalFacility(Long id) {
        checkForeignKeyConstraints(id);

        medicalFacilityRepository.deleteById(id);
    }

    private void checkForeignKeyConstraints(Long id) {
        if (!medicalFacilityRepository.existsById(id)) {
            throw new NotFoundException(entityName);
        }

        // Check references (RESTRICT strategy)
        long refCount = employeeRepository.countByMedicalRegistration_MedicalFacilityId(id);
        if (refCount > 0) {
            throw new CannotDeleteException(
                    "MedicalFacility", id, "Employee", refCount
            );
        }
    }
}
