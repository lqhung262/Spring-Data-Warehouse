package com.example.demo.service.humanresource;

import com.example.demo.dto.BulkOperationResult;
import com.example.demo.dto.humanresource.WorkLocation.WorkLocationRequest;
import com.example.demo.dto.humanresource.WorkLocation.WorkLocationResponse;
import com.example.demo.entity.humanresource.WorkLocation;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.WorkLocationMapper;
import com.example.demo.repository.humanresource.WorkLocationRepository;
import com.example.demo.repository.humanresource.EmployeeWorkLocationRepository;
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
public class WorkLocationService {
    final WorkLocationRepository workLocationRepository;
    final WorkLocationMapper workLocationMapper;
    final EmployeeWorkLocationRepository employeeWorkLocationRepository;
    final EntityManager entityManager;

    @Value("${entities.humanresource.worklocation}")
    private String entityName;


    public WorkLocationResponse createWorkLocation(WorkLocationRequest request) {
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            workLocationRepository.findBySourceId(request.getSourceId()).ifPresent(b -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        WorkLocation workLocation = workLocationMapper.toWorkLocation(request);

        return workLocationMapper.toWorkLocationResponse(workLocationRepository.save(workLocation));
    }

    /**
     * Bulk Upsert với Final Batch Logic, Partial Success Pattern:
     * - Safe Batch: saveAll() - requests không có unique conflicts
     * - Final Batch: save + flush từng request - requests có potential conflicts
     * - Trả về detailed result với success/failure breakdown
     */
    public BulkOperationResult<WorkLocationResponse> bulkUpsertWorkLocations(
            List<WorkLocationRequest> requests) {

        // 1. Define unique field configurations (WorkLocation has 2 unique fields)
        UniqueFieldConfig<WorkLocationRequest> sourceIdConfig =
                new UniqueFieldConfig<>("source_id", WorkLocationRequest::getSourceId);
        UniqueFieldConfig<WorkLocationRequest> codeConfig =
                new UniqueFieldConfig<>("work_location_code", WorkLocationRequest::getWorkLocationCode);
        UniqueFieldConfig<WorkLocationRequest> nameConfig =
                new UniqueFieldConfig<>("name", WorkLocationRequest::getName);

        // 2. Define entity fetchers for each unique field
        Map<String, Function<Set<String>, List<WorkLocation>>> entityFetchers = new HashMap<>();
        entityFetchers.put("source_id", workLocationRepository::findBySourceIdIn);
        entityFetchers.put("work_location_code", workLocationRepository::findByWorkLocationCodeIn);
        entityFetchers.put("name", workLocationRepository::findByNameIn);

        // 2.5. Define entity field extractors (to extract values from returned entities)
        Map<String, Function<WorkLocation, String>> entityFieldExtractors = new HashMap<>();
        entityFieldExtractors.put("source_id", WorkLocation::getSourceId);
        entityFieldExtractors.put("work_location_code", WorkLocation::getWorkLocationCode);
        entityFieldExtractors.put("name", WorkLocation::getName);

        // 3. Setup unique fields using helper
        UniqueFieldsSetupHelper.UniqueFieldsSetup<WorkLocationRequest> setup =
                UniqueFieldsSetupHelper.buildUniqueFieldsSetup(
                        requests,
                        entityFetchers,
                        entityFieldExtractors,
                        sourceIdConfig,
                        codeConfig,
                        nameConfig
                );

        // 4.  Build bulk upsert config
        BulkUpsertConfig<WorkLocationRequest, WorkLocation, WorkLocationResponse> config =
                BulkUpsertConfig.<WorkLocationRequest, WorkLocation, WorkLocationResponse>builder()
                        .uniqueFieldExtractors(setup.getUniqueFieldExtractors())
                        .existingValuesMaps(setup.getExistingValuesMaps())
                        .entityToResponseMapper(workLocationMapper::toWorkLocationResponse)
                        .requestToEntityMapper(workLocationMapper::toWorkLocation)
                        .entityUpdater(workLocationMapper::updateWorkLocation)
                        .existingEntityFinder(this::findExistingEntityForUpsert)
                        .repositorySaver(workLocationRepository::saveAll)
                        .repositorySaveAndFlusher(workLocationRepository::saveAndFlush)
                        .entityManagerClearer(entityManager::clear)
                        .build();

        // 5. Execute bulk upsert
        BulkUpsertProcessor<WorkLocationRequest, WorkLocation, WorkLocationResponse> processor =
                new BulkUpsertProcessor<>(config);

        return processor.execute(requests);
    }

    /**
     * Helper: Find existing entity for upsert
     * STRICT LOGIC: Only match by sourceId (primary identifier)
     */
    private WorkLocation findExistingEntityForUpsert(WorkLocationRequest request) {
        // ONLY match by sourceId (canonical identifier)
        if (request.getSourceId() != null && !request.getSourceId().trim().isEmpty()) {
            Optional<WorkLocation> bySourceId = workLocationRepository.findBySourceId(request.getSourceId());
            if (bySourceId.isPresent()) {
                return bySourceId.get();
            }
        }

        // Do NOT fallback to code or name matching
        return null;
    }

    // ========================= BULK DELETE  ========================

    public BulkOperationResult<Long> bulkDeleteWorkLocations(List<Long> ids) {

        // Build config
        BulkDeleteConfig<WorkLocation> config = BulkDeleteConfig.<WorkLocation>builder()
                .entityFinder(id -> workLocationRepository.findById(id).orElse(null))
                .foreignKeyConstraintsChecker(this::checkForeignKeyConstraints)
                .repositoryDeleter(workLocationRepository::deleteById)
                .entityName(entityName)
                .build();

        // Execute với processor
        BulkDeleteProcessor<WorkLocation> processor = new BulkDeleteProcessor<>(config);

        return processor.execute(ids);
    }

    public List<WorkLocationResponse> getWorkLocations(Pageable pageable) {
        Page<WorkLocation> page = workLocationRepository.findAll(pageable);
        return page.getContent()
                .stream().map(workLocationMapper::toWorkLocationResponse).toList();
    }

    public WorkLocationResponse getWorkLocation(Long id) {
        return workLocationMapper.toWorkLocationResponse(workLocationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public WorkLocationResponse updateWorkLocation(Long id, WorkLocationRequest request) {
        WorkLocation workLocation = workLocationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            workLocationRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getWorkLocationId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }

        workLocationMapper.updateWorkLocation(workLocation, request);

        return workLocationMapper.toWorkLocationResponse(workLocationRepository.save(workLocation));
    }

    public void deleteWorkLocation(Long id) {
        checkForeignKeyConstraints(id);

        workLocationRepository.deleteById(id);
    }

    private void checkForeignKeyConstraints(Long id) {
        if (!workLocationRepository.existsById(id)) {
            throw new NotFoundException(entityName);
        }

        // Check references (RESTRICT strategy)
        long refCount = employeeWorkLocationRepository.countByWorkLocation_WorkLocationId(id);
        if (refCount > 0) {
            throw new CannotDeleteException(
                    "WorkLocation", id, "EmployeeWorkLocation", refCount
            );
        }
    }
}
