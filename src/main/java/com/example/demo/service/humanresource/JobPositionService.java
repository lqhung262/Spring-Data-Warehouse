package com.example.demo.service.humanresource;

import com.example.demo.dto.BulkOperationResult;
import com.example.demo.dto.humanresource.JobPosition.JobPositionRequest;
import com.example.demo.dto.humanresource.JobPosition.JobPositionResponse;
import com.example.demo.entity.humanresource.JobPosition;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.JobPositionMapper;
import com.example.demo.repository.humanresource.JobPositionRepository;
import com.example.demo.util.bulk.*;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.demo.repository.humanresource.EmployeeDecisionRepository;
import com.example.demo.exception.CannotDeleteException;

import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobPositionService {
    final JobPositionRepository jobPositionRepository;
    final JobPositionMapper jobPositionMapper;
    final EmployeeDecisionRepository employeeDecisionRepository;
    final EntityManager entityManager;

    @Value("${entities.humanresource.jobposition}")
    private String entityName;

    public JobPositionResponse createJobPosition(JobPositionRequest request) {
        // Check source_id uniqueness for create
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            jobPositionRepository.findBySourceId(request.getSourceId()).ifPresent(b -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        JobPosition jobPosition = jobPositionMapper.toJobPosition(request);

        return jobPositionMapper.toJobPositionResponse(jobPositionRepository.save(jobPosition));
    }

    /**
     * Bulk Upsert với Final Batch Logic, Partial Success Pattern:
     * - Safe Batch: saveAll() - requests không có unique conflicts
     * - Final Batch: save + flush từng request - requests có potential conflicts
     * - Trả về detailed result với success/failure breakdown
     */
    public BulkOperationResult<JobPositionResponse> bulkUpsertJobPositions(
            List<JobPositionRequest> requests) {

        // 1. Define unique field configurations (JobPosition has 2 unique fields)
        UniqueFieldConfig<JobPositionRequest> sourceIdConfig =
                new UniqueFieldConfig<>("source_id", JobPositionRequest::getSourceId);
        UniqueFieldConfig<JobPositionRequest> codeConfig =
                new UniqueFieldConfig<>("job_position_code", JobPositionRequest::getJobPositionCode);
        UniqueFieldConfig<JobPositionRequest> nameConfig =
                new UniqueFieldConfig<>("name", JobPositionRequest::getName);

        // 2. Define entity fetchers for each unique field
        Map<String, Function<Set<String>, List<JobPosition>>> entityFetchers = new HashMap<>();
        entityFetchers.put("source_id", jobPositionRepository::findBySourceIdIn);
        entityFetchers.put("job_position_code", jobPositionRepository::findByJobPositionCodeIn);
        entityFetchers.put("name", jobPositionRepository::findByNameIn);

        // 2.5. Define entity field extractors
        Map<String, Function<JobPosition, String>> entityFieldExtractors = new HashMap<>();
        entityFieldExtractors.put("source_id", JobPosition::getSourceId);
        entityFieldExtractors.put("job_position_code", JobPosition::getJobPositionCode);
        entityFieldExtractors.put("name", JobPosition::getName);

        // 3. Setup unique fields using helper
        UniqueFieldsSetupHelper.UniqueFieldsSetup<JobPositionRequest> setup =
                UniqueFieldsSetupHelper.buildUniqueFieldsSetup(
                        requests,
                        entityFetchers,
                        entityFieldExtractors,
                        sourceIdConfig,
                        codeConfig,
                        nameConfig
                );

        // 4.  Build bulk upsert config
        BulkUpsertConfig<JobPositionRequest, JobPosition, JobPositionResponse> config =
                BulkUpsertConfig.<JobPositionRequest, JobPosition, JobPositionResponse>builder()
                        .uniqueFieldExtractors(setup.getUniqueFieldExtractors())
                        .existingValuesMaps(setup.getExistingValuesMaps())
                        .entityToResponseMapper(jobPositionMapper::toJobPositionResponse)
                        .requestToEntityMapper(jobPositionMapper::toJobPosition)
                        .entityUpdater(jobPositionMapper::updateJobPosition)
                        .existingEntityFinder(this::findExistingEntityForUpsert)
                        .repositorySaver(jobPositionRepository::saveAll)
                        .repositorySaveAndFlusher(jobPositionRepository::saveAndFlush)
                        .entityManagerClearer(entityManager::clear)
                        .build();

        // 5. Execute bulk upsert
        BulkUpsertProcessor<JobPositionRequest, JobPosition, JobPositionResponse> processor =
                new BulkUpsertProcessor<>(config);

        return processor.execute(requests);
    }

    /**
     * Helper: Find existing entity
     */
    private JobPosition findExistingEntityForUpsert(JobPositionRequest request) {
        if (request.getSourceId() != null && !request.getSourceId().trim().isEmpty()) {
            Optional<JobPosition> bySourceId = jobPositionRepository.findBySourceId(request.getSourceId());
            if (bySourceId.isPresent()) {
                return bySourceId.get();
            }
        }

        return null;
    }

    // ========================= BULK DELETE  ========================

    public BulkOperationResult<Long> bulkDeleteJobPositions(List<Long> ids) {

        // Build config
        BulkDeleteConfig<JobPosition> config = BulkDeleteConfig.<JobPosition>builder()
                .entityFinder(id -> jobPositionRepository.findById(id).orElse(null))
                .foreignKeyConstraintsChecker(this::checkForeignKeyConstraints)
                .repositoryDeleter(jobPositionRepository::deleteById)
                .entityName(entityName)
                .build();

        // Execute với processor
        BulkDeleteProcessor<JobPosition> processor = new BulkDeleteProcessor<>(config);

        return processor.execute(ids);
    }

    public List<JobPositionResponse> getJobPositions(Pageable pageable) {
        Page<JobPosition> page = jobPositionRepository.findAll(pageable);
        return page.getContent()
                .stream().map(jobPositionMapper::toJobPositionResponse).toList();
    }

    public JobPositionResponse getJobPosition(Long id) {
        return jobPositionMapper.toJobPositionResponse(jobPositionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public JobPositionResponse updateJobPosition(Long id, JobPositionRequest request) {
        JobPosition jobPosition = jobPositionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        // Check source_id uniqueness for update
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            jobPositionRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getJobPositionId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }

        jobPositionMapper.updateJobPosition(jobPosition, request);

        return jobPositionMapper.toJobPositionResponse(jobPositionRepository.save(jobPosition));
    }

    public void deleteJobPosition(Long id) {
        checkForeignKeyConstraints(id);

        jobPositionRepository.deleteById(id);
    }

    private void checkForeignKeyConstraints(Long id) {
        if (!jobPositionRepository.existsById(id)) {
            throw new NotFoundException(entityName);
        }

        // Check references (RESTRICT strategy)
        long refCount = employeeDecisionRepository.countByJobPosition_JobPositionId(id);
        if (refCount > 0) {
            throw new CannotDeleteException(
                    "JobPosition", id, "EmployeeDecision", refCount
            );
        }
    }
}
