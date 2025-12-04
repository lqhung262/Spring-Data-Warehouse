package com.example.demo.service.humanresource;

import com.example.demo.dto.BulkOperationResult;
import com.example.demo.dto.humanresource.Major.MajorRequest;
import com.example.demo.dto.humanresource.Major.MajorResponse;
import com.example.demo.entity.humanresource.Major;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.MajorMapper;
import com.example.demo.repository.humanresource.MajorRepository;
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
import com.example.demo.exception.CannotDeleteException;

import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MajorService {
    final MajorRepository majorRepository;
    final MajorMapper majorMapper;
    final EmployeeEducationRepository employeeEducationRepository;
    final EntityManager entityManager;

    @Value("${entities.humanresource.major}")
    private String entityName;

    public MajorResponse createMajor(MajorRequest request) {
        // Check source_id uniqueness for create
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            majorRepository.findBySourceId(request.getSourceId()).ifPresent(b -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        Major major = majorMapper.toMajor(request);

        return majorMapper.toMajorResponse(majorRepository.save(major));
    }

    /**
     * Bulk Upsert với Final Batch Logic, Partial Success Pattern:
     * - Safe Batch: saveAll() - requests không có unique conflicts
     * - Final Batch: save + flush từng request - requests có potential conflicts
     * - Trả về detailed result với success/failure breakdown
     */
    public BulkOperationResult<MajorResponse> bulkUpsertMajors(
            List<MajorRequest> requests) {

        // 1. Define unique field configurations (Major has 2 unique fields)
        UniqueFieldConfig<MajorRequest> sourceIdConfig =
                new UniqueFieldConfig<>("source_id", MajorRequest::getSourceId);
        UniqueFieldConfig<MajorRequest> codeConfig =
                new UniqueFieldConfig<>("major_code", MajorRequest::getMajorCode);
        UniqueFieldConfig<MajorRequest> nameConfig =
                new UniqueFieldConfig<>("name", MajorRequest::getName);

        // 2. Define entity fetchers for each unique field
        Map<String, Function<Set<String>, List<Major>>> entityFetchers = new HashMap<>();
        entityFetchers.put("source_id", majorRepository::findBySourceIdIn);
        entityFetchers.put("major_code", majorRepository::findByMajorCodeIn);
        entityFetchers.put("name", majorRepository::findByNameIn);

        // 2.5. Define entity field extractors
        Map<String, Function<Major, String>> entityFieldExtractors = new HashMap<>();
        entityFieldExtractors.put("source_id", Major::getSourceId);
        entityFieldExtractors.put("major_code", Major::getMajorCode);
        entityFieldExtractors.put("name", Major::getName);

        // 3. Setup unique fields using helper
        UniqueFieldsSetupHelper.UniqueFieldsSetup<MajorRequest> setup =
                UniqueFieldsSetupHelper.buildUniqueFieldsSetup(
                        requests,
                        entityFetchers,
                        entityFieldExtractors,
                        sourceIdConfig,
                        codeConfig,
                        nameConfig
                );

        // 4.  Build bulk upsert config
        BulkUpsertConfig<MajorRequest, Major, MajorResponse> config =
                BulkUpsertConfig.<MajorRequest, Major, MajorResponse>builder()
                        .uniqueFieldExtractors(setup.getUniqueFieldExtractors())
                        .existingValuesMaps(setup.getExistingValuesMaps())
                        .entityToResponseMapper(majorMapper::toMajorResponse)
                        .requestToEntityMapper(majorMapper::toMajor)
                        .entityUpdater(majorMapper::updateMajor)
                        .existingEntityFinder(this::findExistingEntityForUpsert)
                        .repositorySaver(majorRepository::saveAll)
                        .repositorySaveAndFlusher(majorRepository::saveAndFlush)
                        .entityManagerClearer(entityManager::clear)
                        .build();

        // 5. Execute bulk upsert
        BulkUpsertProcessor<MajorRequest, Major, MajorResponse> processor =
                new BulkUpsertProcessor<>(config);

        return processor.execute(requests);
    }

    /**
     * Helper: Find existing entity
     */
    private Major findExistingEntityForUpsert(MajorRequest request) {
        if (request.getSourceId() != null && !request.getSourceId().trim().isEmpty()) {
            Optional<Major> bySourceId = majorRepository.findBySourceId(request.getSourceId());
            if (bySourceId.isPresent()) {
                return bySourceId.get();
            }
        }

        return null;
    }

    // ========================= BULK DELETE  ========================

    public BulkOperationResult<Long> bulkDeleteMajors(List<Long> ids) {

        // Build config
        BulkDeleteConfig<Major> config = BulkDeleteConfig.<Major>builder()
                .entityFinder(id -> majorRepository.findById(id).orElse(null))
                .foreignKeyConstraintsChecker(this::checkReferenceBeforeDelete)
                .repositoryDeleter(majorRepository::deleteById)
                .entityName(entityName)
                .build();

        // Execute với processor
        BulkDeleteProcessor<Major> processor = new BulkDeleteProcessor<>(config);

        return processor.execute(ids);
    }

    public List<MajorResponse> getMajors(Pageable pageable) {
        Page<Major> page = majorRepository.findAll(pageable);
        return page.getContent()
                .stream().map(majorMapper::toMajorResponse).toList();
    }

    public MajorResponse getMajor(Long id) {
        return majorMapper.toMajorResponse(majorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public MajorResponse updateMajor(Long id, MajorRequest request) {
        Major major = majorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        // Check source_id uniqueness for update
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            majorRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getMajorId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }

        majorMapper.updateMajor(major, request);

        return majorMapper.toMajorResponse(majorRepository.save(major));
    }

    public void deleteMajor(Long id) {
        checkReferenceBeforeDelete(id);

        majorRepository.deleteById(id);
    }

    private void checkReferenceBeforeDelete(Long id) {
        if (!majorRepository.existsById(id)) {
            throw new NotFoundException(entityName);
        }

        // Check references (RESTRICT strategy)
        long refCount = employeeEducationRepository.countByMajor_MajorId(id);
        if (refCount > 0) {
            throw new CannotDeleteException(
                    "Major", id, "EmployeeEducation", refCount
            );
        }
    }
}
