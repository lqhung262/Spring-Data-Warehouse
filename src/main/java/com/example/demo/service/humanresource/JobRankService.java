package com.example.demo.service.humanresource;

import com.example.demo.dto.BulkOperationResult;
import com.example.demo.dto.humanresource.JobRank.JobRankRequest;
import com.example.demo.dto.humanresource.JobRank.JobRankResponse;
import com.example.demo.entity.humanresource.JobRank;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.JobRankMapper;
import com.example.demo.repository.humanresource.JobRankRepository;
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
public class JobRankService {
    final JobRankRepository jobRankRepository;
    final JobRankMapper jobRankMapper;
    final EmployeeDecisionRepository employeeDecisionRepository;
    final EntityManager entityManager;

    @Value("${entities.humanresource.jobrank}")
    private String entityName;

    public JobRankResponse createJobRank(JobRankRequest request) {
        // Check source_id uniqueness for create
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            jobRankRepository.findBySourceId(request.getSourceId()).ifPresent(b -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        JobRank jobRank = jobRankMapper.toJobRank(request);

        return jobRankMapper.toJobRankResponse(jobRankRepository.save(jobRank));
    }

    /**
     * Bulk Upsert với Final Batch Logic, Partial Success Pattern:
     * - Safe Batch: saveAll() - requests không có unique conflicts
     * - Final Batch: save + flush từng request - requests có potential conflicts
     * - Trả về detailed result với success/failure breakdown
     */
    public BulkOperationResult<JobRankResponse> bulkUpsertJobRanks(
            List<JobRankRequest> requests) {

        // 1. Define unique field configurations (JobRank has 2 unique fields)
        UniqueFieldConfig<JobRankRequest> sourceIdConfig =
                new UniqueFieldConfig<>("source_id", JobRankRequest::getSourceId);
        UniqueFieldConfig<JobRankRequest> codeConfig =
                new UniqueFieldConfig<>("job_rank_code", JobRankRequest::getJobRankCode);
        UniqueFieldConfig<JobRankRequest> nameConfig =
                new UniqueFieldConfig<>("name", JobRankRequest::getName);

        // 2. Define entity fetchers for each unique field
        Map<String, Function<Set<String>, List<JobRank>>> entityFetchers = new HashMap<>();
        entityFetchers.put("source_id", jobRankRepository::findBySourceIdIn);
        entityFetchers.put("job_rank_code", jobRankRepository::findByJobRankCodeIn);
        entityFetchers.put("name", jobRankRepository::findByNameIn);

        // 2.5. Define entity field extractors
        Map<String, Function<JobRank, String>> entityFieldExtractors = new HashMap<>();
        entityFieldExtractors.put("source_id", JobRank::getSourceId);
        entityFieldExtractors.put("job_rank_code", JobRank::getJobRankCode);
        entityFieldExtractors.put("name", JobRank::getName);

        // 3. Setup unique fields using helper
        UniqueFieldsSetupHelper.UniqueFieldsSetup<JobRankRequest> setup =
                UniqueFieldsSetupHelper.buildUniqueFieldsSetup(
                        requests,
                        entityFetchers,
                        entityFieldExtractors,
                        sourceIdConfig,
                        codeConfig,
                        nameConfig
                );

        // 4.  Build bulk upsert config
        BulkUpsertConfig<JobRankRequest, JobRank, JobRankResponse> config =
                BulkUpsertConfig.<JobRankRequest, JobRank, JobRankResponse>builder()
                        .uniqueFieldExtractors(setup.getUniqueFieldExtractors())
                        .existingValuesMaps(setup.getExistingValuesMaps())
                        .entityToResponseMapper(jobRankMapper::toJobRankResponse)
                        .requestToEntityMapper(jobRankMapper::toJobRank)
                        .entityUpdater(jobRankMapper::updateJobRank)
                        .existingEntityFinder(this::findExistingEntityForUpsert)
                        .repositorySaver(jobRankRepository::saveAll)
                        .repositorySaveAndFlusher(jobRankRepository::saveAndFlush)
                        .entityManagerClearer(entityManager::clear)
                        .build();

        // 5. Execute bulk upsert
        BulkUpsertProcessor<JobRankRequest, JobRank, JobRankResponse> processor =
                new BulkUpsertProcessor<>(config);

        return processor.execute(requests);
    }

    /**
     * Helper: Find existing entity
     */
    private JobRank findExistingEntityForUpsert(JobRankRequest request) {
        if (request.getSourceId() != null && !request.getSourceId().trim().isEmpty()) {
            Optional<JobRank> bySourceId = jobRankRepository.findBySourceId(request.getSourceId());
            if (bySourceId.isPresent()) {
                return bySourceId.get();
            }
        }


        return null;
    }

    // ========================= BULK DELETE  ========================

    public BulkOperationResult<Long> bulkDeleteJobRanks(List<Long> ids) {

        // Build config
        BulkDeleteConfig<JobRank> config = BulkDeleteConfig.<JobRank>builder()
                .entityFinder(id -> jobRankRepository.findById(id).orElse(null))
                .foreignKeyConstraintsChecker(this::checkForeignKeyConstraints)
                .repositoryDeleter(jobRankRepository::deleteById)
                .entityName(entityName)
                .build();

        // Execute với processor
        BulkDeleteProcessor<JobRank> processor = new BulkDeleteProcessor<>(config);

        return processor.execute(ids);
    }

    public List<JobRankResponse> getJobRanks(Pageable pageable) {
        Page<JobRank> page = jobRankRepository.findAll(pageable);
        return page.getContent()
                .stream().map(jobRankMapper::toJobRankResponse).toList();
    }

    public JobRankResponse getJobRank(Long id) {
        return jobRankMapper.toJobRankResponse(jobRankRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public JobRankResponse updateJobRank(Long id, JobRankRequest request) {
        JobRank jobRank = jobRankRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        // Check source_id uniqueness for update
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            jobRankRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getJobRankId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }

        jobRankMapper.updateJobRank(jobRank, request);

        return jobRankMapper.toJobRankResponse(jobRankRepository.save(jobRank));
    }

    public void deleteJobRank(Long id) {
        checkForeignKeyConstraints(id);

        jobRankRepository.deleteById(id);
    }

    private void checkForeignKeyConstraints(Long id) {
        if (!jobRankRepository.existsById(id)) {
            throw new NotFoundException(entityName);
        }

        // Check references (RESTRICT strategy)
        long refCount = employeeDecisionRepository.countByJobRank_JobRankId(id);
        if (refCount > 0) {
            throw new CannotDeleteException(
                    "JobRank", id, "EmployeeDecision", refCount
            );
        }
    }
}
