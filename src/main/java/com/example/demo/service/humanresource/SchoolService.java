package com.example.demo.service.humanresource;

import com.example.demo.dto.BulkOperationResult;
import com.example.demo.dto.humanresource.School.SchoolRequest;
import com.example.demo.dto.humanresource.School.SchoolResponse;
import com.example.demo.entity.humanresource.School;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.SchoolMapper;
import com.example.demo.repository.humanresource.SchoolRepository;
import com.example.demo.util.bulk.*;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.demo.repository.humanresource.EmployeeEducationRepository;
import com.example.demo.repository.humanresource.EmployeeRepository;
import com.example.demo.exception.CannotDeleteException;

import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SchoolService {
    final SchoolRepository schoolRepository;
    final SchoolMapper schoolMapper;
    final EmployeeEducationRepository employeeEducationRepository;
    final EmployeeRepository employeeRepository;
    final EntityManager entityManager;

    @Value("${entities.humanresource.school}")
    private String entityName;

    public SchoolResponse createSchool(SchoolRequest request) {
        // Check source_id uniqueness for create
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            schoolRepository.findBySourceId(request.getSourceId()).ifPresent(b -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        School school = schoolMapper.toSchool(request);

        return schoolMapper.toSchoolResponse(schoolRepository.save(school));
    }


    /**
     * Bulk Upsert với Final Batch Logic, Partial Success Pattern:
     * - Safe Batch: saveAll() - requests không có unique conflicts
     * - Final Batch: save + flush từng request - requests có potential conflicts
     * - Trả về detailed result với success/failure breakdown
     */
    public BulkOperationResult<SchoolResponse> bulkUpsertSchools(
            List<SchoolRequest> requests) {

        // 1. Define unique field configurations (School has 2 unique fields)
        UniqueFieldConfig<SchoolRequest> sourceIdConfig =
                new UniqueFieldConfig<>("source_id", SchoolRequest::getSourceId);
        UniqueFieldConfig<SchoolRequest> codeConfig =
                new UniqueFieldConfig<>("school_code", SchoolRequest::getSchoolCode);
        UniqueFieldConfig<SchoolRequest> nameConfig =
                new UniqueFieldConfig<>("name", SchoolRequest::getName);

        // 2. Define entity fetchers for each unique field
        Map<String, Function<Set<String>, List<School>>> entityFetchers = new HashMap<>();
        entityFetchers.put("source_id", schoolRepository::findBySourceIdIn);
        entityFetchers.put("school_code", schoolRepository::findBySchoolCodeIn);
        entityFetchers.put("name", schoolRepository::findByNameIn);

        // 2.5. Define entity field extractors (to extract values from returned entities)
        Map<String, Function<School, String>> entityFieldExtractors = new HashMap<>();
        entityFieldExtractors.put("source_id", School::getSourceId);
        entityFieldExtractors.put("school_code", School::getSchoolCode);
        entityFieldExtractors.put("name", School::getName);

        // 3. Setup unique fields using helper
        UniqueFieldsSetupHelper.UniqueFieldsSetup<SchoolRequest> setup =
                UniqueFieldsSetupHelper.buildUniqueFieldsSetup(
                        requests,
                        entityFetchers,
                        entityFieldExtractors,
                        sourceIdConfig,
                        codeConfig,
                        nameConfig
                );

        // 4.  Build bulk upsert config
        BulkUpsertConfig<SchoolRequest, School, SchoolResponse> config =
                BulkUpsertConfig.<SchoolRequest, School, SchoolResponse>builder()
                        .uniqueFieldExtractors(setup.getUniqueFieldExtractors())
                        .existingValuesMaps(setup.getExistingValuesMaps())
                        .entityToResponseMapper(schoolMapper::toSchoolResponse)
                        .requestToEntityMapper(schoolMapper::toSchool)
                        .entityUpdater(schoolMapper::updateSchool)
                        .existingEntityFinder(this::findExistingEntityForUpsert)
                        .repositorySaver(schoolRepository::saveAll)
                        .repositorySaveAndFlusher(schoolRepository::saveAndFlush)
                        .entityManagerClearer(entityManager::clear)
                        .build();

        // 5. Execute bulk upsert
        BulkUpsertProcessor<SchoolRequest, School, SchoolResponse> processor =
                new BulkUpsertProcessor<>(config);

        return processor.execute(requests);
    }

    /**
     * Helper: Find existing entity for upsert
     * STRICT LOGIC: Only match by sourceId (primary identifier)
     */
    private School findExistingEntityForUpsert(SchoolRequest request) {
        // ONLY match by sourceId (canonical identifier)
        if (request.getSourceId() != null && !request.getSourceId().trim().isEmpty()) {
            Optional<School> bySourceId = schoolRepository.findBySourceId(request.getSourceId());
            if (bySourceId.isPresent()) {
                return bySourceId.get();
            }
        }

        // Do NOT fallback to code or name matching
        return null;
    }

    // ========================= BULK DELETE  ========================

    public BulkOperationResult<Long> bulkDeleteSchools(List<Long> ids) {

        // Build config
        BulkDeleteConfig<School> config = BulkDeleteConfig.<School>builder()
                .entityFinder(id -> schoolRepository.findById(id).orElse(null))
                .foreignKeyConstraintsChecker(this::checkForeignKeyConstraints)
                .repositoryDeleter(schoolRepository::deleteById)
                .entityName(entityName)
                .build();

        // Execute với processor
        BulkDeleteProcessor<School> processor = new BulkDeleteProcessor<>(config);

        return processor.execute(ids);
    }

    public List<SchoolResponse> getSchools(Pageable pageable) {
        Page<School> page = schoolRepository.findAll(pageable);
        return page.getContent()
                .stream().map(schoolMapper::toSchoolResponse).toList();
    }

    public SchoolResponse getSchool(Long id) {
        return schoolMapper.toSchoolResponse(schoolRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public SchoolResponse updateSchool(Long id, SchoolRequest request) {
        School school = schoolRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        // Check source_id uniqueness for update
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            schoolRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getSchoolId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }

        schoolMapper.updateSchool(school, request);

        return schoolMapper.toSchoolResponse(schoolRepository.save(school));
    }

    public void deleteSchool(Long id) {
        checkForeignKeyConstraints(id);

        schoolRepository.deleteById(id);
    }

    private void checkForeignKeyConstraints(Long id) {
        if (!schoolRepository.existsById(id)) {
            throw new NotFoundException(entityName);
        }

        // Check references (RESTRICT strategy) - School has 2 FK references
        long educationCount = employeeEducationRepository.countBySchool_SchoolId(id);
        long employeeCount = employeeRepository.countByGraduationSchool_SchoolId(id);
        long totalCount = educationCount + employeeCount;

        if (totalCount > 0) {
            throw new CannotDeleteException(
                    "School", id, "referencing records", totalCount
            );
        }
    }
}
