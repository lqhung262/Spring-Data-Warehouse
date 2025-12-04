package com.example.demo.service.humanresource;

import com.example.demo.dto.BulkOperationResult;
import com.example.demo.dto.humanresource.Nationality.NationalityRequest;
import com.example.demo.dto.humanresource.Nationality.NationalityResponse;
import com.example.demo.entity.humanresource.Nationality;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.NationalityMapper;
import com.example.demo.repository.humanresource.NationalityRepository;
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
public class NationalityService {
    final NationalityRepository nationalityRepository;
    final NationalityMapper nationalityMapper;
    final EmployeeRepository employeeRepository;
    final EntityManager entityManager;

    @Value("${entities.humanresource.nationality}")
    private String entityName;

    public NationalityResponse createNationality(NationalityRequest request) {
        // Check source_id uniqueness for create
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            nationalityRepository.findBySourceId(request.getSourceId()).ifPresent(b -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        Nationality nationality = nationalityMapper.toNationality(request);

        return nationalityMapper.toNationalityResponse(nationalityRepository.save(nationality));
    }

    /**
     * Bulk Upsert với Final Batch Logic, Partial Success Pattern:
     * - Safe Batch: saveAll() - requests không có unique conflicts
     * - Final Batch: save + flush từng request - requests có potential conflicts
     * - Trả về detailed result với success/failure breakdown
     */
    public BulkOperationResult<NationalityResponse> bulkUpsertNationalities(
            List<NationalityRequest> requests) {

        // 1. Define unique field configurations (Nationality has 2 unique fields)
        UniqueFieldConfig<NationalityRequest> sourceIdConfig =
                new UniqueFieldConfig<>("source_id", NationalityRequest::getSourceId);
        UniqueFieldConfig<NationalityRequest> codeConfig =
                new UniqueFieldConfig<>("nationality_code", NationalityRequest::getNationalityCode);
        UniqueFieldConfig<NationalityRequest> nameConfig =
                new UniqueFieldConfig<>("name", NationalityRequest::getName);

        // 2. Define entity fetchers for each unique field
        Map<String, Function<Set<String>, List<Nationality>>> entityFetchers = new HashMap<>();
        entityFetchers.put("source_id", nationalityRepository::findBySourceIdIn);
        entityFetchers.put("nationality_code", nationalityRepository::findByNationalityCodeIn);
        entityFetchers.put("name", nationalityRepository::findByNameIn);

        // 2.5. Define entity field extractors (to extract values from returned entities)
        Map<String, Function<Nationality, String>> entityFieldExtractors = new HashMap<>();
        entityFieldExtractors.put("source_id", Nationality::getSourceId);
        entityFieldExtractors.put("nationality_code", Nationality::getNationalityCode);
        entityFieldExtractors.put("name", Nationality::getName);

        // 3. Setup unique fields using helper
        UniqueFieldsSetupHelper.UniqueFieldsSetup<NationalityRequest> setup =
                UniqueFieldsSetupHelper.buildUniqueFieldsSetup(
                        requests,
                        entityFetchers,
                        entityFieldExtractors,
                        sourceIdConfig,
                        codeConfig,
                        nameConfig
                );

        // 4.  Build bulk upsert config
        BulkUpsertConfig<NationalityRequest, Nationality, NationalityResponse> config =
                BulkUpsertConfig.<NationalityRequest, Nationality, NationalityResponse>builder()
                        .uniqueFieldExtractors(setup.getUniqueFieldExtractors())
                        .existingValuesMaps(setup.getExistingValuesMaps())
                        .entityToResponseMapper(nationalityMapper::toNationalityResponse)
                        .requestToEntityMapper(nationalityMapper::toNationality)
                        .entityUpdater(nationalityMapper::updateNationality)
                        .existingEntityFinder(this::findExistingEntityForUpsert)
                        .repositorySaver(nationalityRepository::saveAll)
                        .repositorySaveAndFlusher(nationalityRepository::saveAndFlush)
                        .entityManagerClearer(entityManager::clear)
                        .build();

        // 5. Execute bulk upsert
        BulkUpsertProcessor<NationalityRequest, Nationality, NationalityResponse> processor =
                new BulkUpsertProcessor<>(config);

        return processor.execute(requests);
    }

    /**
     * Helper: Find existing entity for upsert
     * STRICT LOGIC: Only match by sourceId (primary identifier)
     */
    private Nationality findExistingEntityForUpsert(NationalityRequest request) {
        // ONLY match by sourceId (canonical identifier)
        if (request.getSourceId() != null && !request.getSourceId().trim().isEmpty()) {
            Optional<Nationality> bySourceId = nationalityRepository.findBySourceId(request.getSourceId());
            if (bySourceId.isPresent()) {
                return bySourceId.get();
            }
        }

        // Do NOT fallback to code or name matching
        return null;
    }

    // ========================= BULK DELETE  ========================

    public BulkOperationResult<Long> bulkDeleteNationalities(List<Long> ids) {

        // Build config
        BulkDeleteConfig<Nationality> config = BulkDeleteConfig.<Nationality>builder()
                .entityFinder(id -> nationalityRepository.findById(id).orElse(null))
                .foreignKeyConstraintsChecker(this::checkForeignKeyConstraints)
                .repositoryDeleter(nationalityRepository::deleteById)
                .entityName(entityName)
                .build();

        // Execute với processor
        BulkDeleteProcessor<Nationality> processor = new BulkDeleteProcessor<>(config);

        return processor.execute(ids);
    }

    public List<NationalityResponse> getNationalities(Pageable pageable) {
        Page<Nationality> page = nationalityRepository.findAll(pageable);
        return page.getContent()
                .stream().map(nationalityMapper::toNationalityResponse).toList();
    }

    public NationalityResponse getNationality(Long id) {
        return nationalityMapper.toNationalityResponse(nationalityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public NationalityResponse updateNationality(Long id, NationalityRequest request) {
        Nationality nationality = nationalityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        // Check source_id uniqueness for update
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            nationalityRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getNationalityId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }

        nationalityMapper.updateNationality(nationality, request);

        return nationalityMapper.toNationalityResponse(nationalityRepository.save(nationality));
    }

    public void deleteNationality(Long id) {
        checkForeignKeyConstraints(id);

        nationalityRepository.deleteById(id);
    }

    private void checkForeignKeyConstraints(Long id) {
        if (!nationalityRepository.existsById(id)) {
            throw new NotFoundException(entityName);
        }

        // Check references (RESTRICT strategy)
        long refCount = employeeRepository.countByNationality_NationalityId(id);
        if (refCount > 0) {
            throw new CannotDeleteException(
                    "Nationality", id, "Employee", refCount
            );
        }
    }
}
