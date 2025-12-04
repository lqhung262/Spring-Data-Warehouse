package com.example.demo.service.humanresource;

import com.example.demo.dto.BulkOperationResult;
import com.example.demo.dto.humanresource.JobTitle.JobTitleRequest;
import com.example.demo.dto.humanresource.JobTitle.JobTitleResponse;
import com.example.demo.entity.humanresource.JobTitle;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.JobTitleMapper;
import com.example.demo.repository.humanresource.JobTitleRepository;
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
public class JobTitleService {
    final JobTitleRepository jobTitleRepository;
    final JobTitleMapper jobTitleMapper;
    final EmployeeDecisionRepository employeeDecisionRepository;
    final EntityManager entityManager;

    @Value("${entities.humanresource.jobtitle}")
    private String entityName;

    public JobTitleResponse createJobTitle(JobTitleRequest request) {
        // Check source_id uniqueness for create
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            jobTitleRepository.findBySourceId(request.getSourceId()).ifPresent(b -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        JobTitle jobTitle = jobTitleMapper.toJobTitle(request);

        return jobTitleMapper.toJobTitleResponse(jobTitleRepository.save(jobTitle));
    }

    /**
     * Bulk Upsert với Final Batch Logic, Partial Success Pattern:
     * - Safe Batch: saveAll() - requests không có unique conflicts
     * - Final Batch: save + flush từng request - requests có potential conflicts
     * - Trả về detailed result với success/failure breakdown
     */
    public BulkOperationResult<JobTitleResponse> bulkUpsertJobTitles(
            List<JobTitleRequest> requests) {

        // 1. Define unique field configurations (JobTitle has 2 unique fields)
        UniqueFieldConfig<JobTitleRequest> sourceIdConfig =
                new UniqueFieldConfig<>("source_id", JobTitleRequest::getSourceId);
        UniqueFieldConfig<JobTitleRequest> codeConfig =
                new UniqueFieldConfig<>("job_title_code", JobTitleRequest::getJobTitleCode);
        UniqueFieldConfig<JobTitleRequest> nameConfig =
                new UniqueFieldConfig<>("name", JobTitleRequest::getName);

        // 2. Define entity fetchers for each unique field
        Map<String, Function<Set<String>, List<JobTitle>>> entityFetchers = new HashMap<>();
        entityFetchers.put("source_id", jobTitleRepository::findBySourceIdIn);
        entityFetchers.put("job_title_code", jobTitleRepository::findByJobTitleCodeIn);
        entityFetchers.put("name", jobTitleRepository::findByNameIn);

        // 2.5. Define entity field extractors
        Map<String, Function<JobTitle, String>> entityFieldExtractors = new HashMap<>();
        entityFieldExtractors.put("source_id", JobTitle::getSourceId);
        entityFieldExtractors.put("job_title_code", JobTitle::getJobTitleCode);
        entityFieldExtractors.put("name", JobTitle::getName);

        // 3. Setup unique fields using helper
        UniqueFieldsSetupHelper.UniqueFieldsSetup<JobTitleRequest> setup =
                UniqueFieldsSetupHelper.buildUniqueFieldsSetup(
                        requests,
                        entityFetchers,
                        entityFieldExtractors,
                        sourceIdConfig,
                        codeConfig,
                        nameConfig
                );

        // 4.  Build bulk upsert config
        BulkUpsertConfig<JobTitleRequest, JobTitle, JobTitleResponse> config =
                BulkUpsertConfig.<JobTitleRequest, JobTitle, JobTitleResponse>builder()
                        .uniqueFieldExtractors(setup.getUniqueFieldExtractors())
                        .existingValuesMaps(setup.getExistingValuesMaps())
                        .entityToResponseMapper(jobTitleMapper::toJobTitleResponse)
                        .requestToEntityMapper(jobTitleMapper::toJobTitle)
                        .entityUpdater(jobTitleMapper::updateJobTitle)
                        .existingEntityFinder(this::findExistingEntityForUpsert)
                        .repositorySaver(jobTitleRepository::saveAll)
                        .repositorySaveAndFlusher(jobTitleRepository::saveAndFlush)
                        .entityManagerClearer(entityManager::clear)
                        .build();

        // 5. Execute bulk upsert
        BulkUpsertProcessor<JobTitleRequest, JobTitle, JobTitleResponse> processor =
                new BulkUpsertProcessor<>(config);

        return processor.execute(requests);
    }

    /**
     * Helper: Find existing entity
     */
    private JobTitle findExistingEntityForUpsert(JobTitleRequest request) {
        if (request.getSourceId() != null && !request.getSourceId().trim().isEmpty()) {
            Optional<JobTitle> bySourceId = jobTitleRepository.findBySourceId(request.getSourceId());
            if (bySourceId.isPresent()) {
                return bySourceId.get();
            }
        }

        return null;
    }

    // ========================= BULK DELETE  ========================

    public BulkOperationResult<Long> bulkDeleteJobTitles(List<Long> ids) {

        // Build config
        BulkDeleteConfig<JobTitle> config = BulkDeleteConfig.<JobTitle>builder()
                .entityFinder(id -> jobTitleRepository.findById(id).orElse(null))
                .foreignKeyConstraintsChecker(this::checkForeignKeyConstraints)
                .repositoryDeleter(jobTitleRepository::deleteById)
                .entityName(entityName)
                .build();

        // Execute với processor
        BulkDeleteProcessor<JobTitle> processor = new BulkDeleteProcessor<>(config);

        return processor.execute(ids);
    }

    public List<JobTitleResponse> getJobTitles(Pageable pageable) {
        Page<JobTitle> page = jobTitleRepository.findAll(pageable);
        return page.getContent()
                .stream().map(jobTitleMapper::toJobTitleResponse).toList();
    }

    public JobTitleResponse getJobTitle(Long id) {
        return jobTitleMapper.toJobTitleResponse(jobTitleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public JobTitleResponse updateJobTitle(Long id, JobTitleRequest request) {
        JobTitle jobTitle = jobTitleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        // Check source_id uniqueness for update
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            jobTitleRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getJobTitleId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }

        jobTitleMapper.updateJobTitle(jobTitle, request);

        return jobTitleMapper.toJobTitleResponse(jobTitleRepository.save(jobTitle));
    }

    public void deleteJobTitle(Long id) {
        checkForeignKeyConstraints(id);

        jobTitleRepository.deleteById(id);
    }

    private void checkForeignKeyConstraints(Long id) {
        if (!jobTitleRepository.existsById(id)) {
            throw new NotFoundException(entityName);
        }

        // Check references (RESTRICT strategy)
        long refCount = employeeDecisionRepository.countByJobTitle_JobTitleId(id);
        if (refCount > 0) {
            throw new CannotDeleteException(
                    "JobTitle", id, "EmployeeDecision", refCount
            );
        }
    }
}
