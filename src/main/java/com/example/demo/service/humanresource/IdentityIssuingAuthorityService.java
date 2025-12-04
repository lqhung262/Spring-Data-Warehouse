package com.example.demo.service.humanresource;

import com.example.demo.dto.BulkOperationResult;
import com.example.demo.dto.humanresource.IdentityIssuingAuthority.IdentityIssuingAuthorityRequest;
import com.example.demo.dto.humanresource.IdentityIssuingAuthority.IdentityIssuingAuthorityResponse;
import com.example.demo.entity.humanresource.IdentityIssuingAuthority;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.IdentityIssuingAuthorityMapper;
import com.example.demo.repository.humanresource.IdentityIssuingAuthorityRepository;
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
public class IdentityIssuingAuthorityService {
    final IdentityIssuingAuthorityRepository identityIssuingAuthorityRepository;
    final IdentityIssuingAuthorityMapper identityIssuingAuthorityMapper;
    final EmployeeRepository employeeRepository;
    final EntityManager entityManager;

    @Value("${entities.humanresource.identityissuingauthoirity}")
    private String entityName;

    public IdentityIssuingAuthorityResponse createIdentityIssuingAuthority(IdentityIssuingAuthorityRequest request) {
        // Check source_id uniqueness for create
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            identityIssuingAuthorityRepository.findBySourceId(request.getSourceId()).ifPresent(b -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        IdentityIssuingAuthority identityIssuingAuthority = identityIssuingAuthorityMapper.toIdentityIssuingAuthority(request);
        // Set FK references from IDs in request
        identityIssuingAuthorityMapper.setReferences(identityIssuingAuthority, request);

        return identityIssuingAuthorityMapper.toIdentityIssuingAuthorityResponse(identityIssuingAuthorityRepository.save(identityIssuingAuthority));
    }

    /**
     * Bulk Upsert với Final Batch Logic, Partial Success Pattern:
     * - Safe Batch: saveAll() - requests không có unique conflicts
     * - Final Batch: save + flush từng request - requests có potential conflicts
     * - Trả về detailed result với success/failure breakdown
     */
    public BulkOperationResult<IdentityIssuingAuthorityResponse> bulkUpsertIdentityIssuingAuthorities(
            List<IdentityIssuingAuthorityRequest> requests) {

        // 1. Define unique field configurations (IdentityIssuingAuthority has 2 unique fields)
        UniqueFieldConfig<IdentityIssuingAuthorityRequest> sourceIdConfig =
                new UniqueFieldConfig<>("source_id", IdentityIssuingAuthorityRequest::getSourceId);
        UniqueFieldConfig<IdentityIssuingAuthorityRequest> nameConfig =
                new UniqueFieldConfig<>("name", IdentityIssuingAuthorityRequest::getName);

        // 2. Define entity fetchers for each unique field
        Map<String, Function<Set<String>, List<IdentityIssuingAuthority>>> entityFetchers = new HashMap<>();
        entityFetchers.put("source_id", identityIssuingAuthorityRepository::findBySourceIdIn);
        entityFetchers.put("name", identityIssuingAuthorityRepository::findByNameIn);

        // 2.5. Define entity field extractors
        Map<String, Function<IdentityIssuingAuthority, String>> entityFieldExtractors = new HashMap<>();
        entityFieldExtractors.put("source_id", IdentityIssuingAuthority::getSourceId);
        entityFieldExtractors.put("name", IdentityIssuingAuthority::getName);

        // 3. Setup unique fields using helper
        UniqueFieldsSetupHelper.UniqueFieldsSetup<IdentityIssuingAuthorityRequest> setup =
                UniqueFieldsSetupHelper.buildUniqueFieldsSetup(
                        requests,
                        entityFetchers,
                        entityFieldExtractors,
                        sourceIdConfig,
                        nameConfig
                );

        // 4.  Build bulk upsert config
        BulkUpsertConfig<IdentityIssuingAuthorityRequest, IdentityIssuingAuthority, IdentityIssuingAuthorityResponse> config =
                BulkUpsertConfig.<IdentityIssuingAuthorityRequest, IdentityIssuingAuthority, IdentityIssuingAuthorityResponse>builder()
                        .uniqueFieldExtractors(setup.getUniqueFieldExtractors())
                        .existingValuesMaps(setup.getExistingValuesMaps())
                        .entityToResponseMapper(identityIssuingAuthorityMapper::toIdentityIssuingAuthorityResponse)
                        .requestToEntityMapper(identityIssuingAuthorityMapper::toIdentityIssuingAuthority)
                        .entityUpdater(identityIssuingAuthorityMapper::updateIdentityIssuingAuthority)
                        .existingEntityFinder(this::findExistingEntityForUpsert)
                        .repositorySaver(identityIssuingAuthorityRepository::saveAll)
                        .repositorySaveAndFlusher(identityIssuingAuthorityRepository::saveAndFlush)
                        .entityManagerClearer(entityManager::clear)
                        .build();

        // 5. Execute bulk upsert
        BulkUpsertProcessor<IdentityIssuingAuthorityRequest, IdentityIssuingAuthority, IdentityIssuingAuthorityResponse> processor =
                new BulkUpsertProcessor<>(config);

        return processor.execute(requests);
    }

    /**
     * Helper: Find existing entity
     */
    private IdentityIssuingAuthority findExistingEntityForUpsert(IdentityIssuingAuthorityRequest request) {
        if (request.getSourceId() != null && !request.getSourceId().trim().isEmpty()) {
            Optional<IdentityIssuingAuthority> bySourceId = identityIssuingAuthorityRepository.findBySourceId(request.getSourceId());
            if (bySourceId.isPresent()) {
                return bySourceId.get();
            }
        }

        return null;
    }

    // ========================= BULK DELETE  ========================

    public BulkOperationResult<Long> bulkDeleteIdentityIssuingAuthorities(List<Long> ids) {

        // Build config
        BulkDeleteConfig<IdentityIssuingAuthority> config = BulkDeleteConfig.<IdentityIssuingAuthority>builder()
                .entityFinder(id -> identityIssuingAuthorityRepository.findById(id).orElse(null))
                .foreignKeyConstraintsChecker(this::checkForeignKeyConstraints)
                .repositoryDeleter(identityIssuingAuthorityRepository::deleteById)
                .entityName(entityName)
                .build();

        // Execute với processor
        BulkDeleteProcessor<IdentityIssuingAuthority> processor = new BulkDeleteProcessor<>(config);

        return processor.execute(ids);
    }

    public List<IdentityIssuingAuthorityResponse> getIdentityIssuingAuthorities(Pageable pageable) {
        Page<IdentityIssuingAuthority> page = identityIssuingAuthorityRepository.findAll(pageable);
        return page.getContent()
                .stream().map(identityIssuingAuthorityMapper::toIdentityIssuingAuthorityResponse).toList();
    }

    public IdentityIssuingAuthorityResponse getIdentityIssuingAuthority(Long id) {
        return identityIssuingAuthorityMapper.toIdentityIssuingAuthorityResponse(identityIssuingAuthorityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public IdentityIssuingAuthorityResponse updateIdentityIssuingAuthority(Long id, IdentityIssuingAuthorityRequest request) {
        IdentityIssuingAuthority identityIssuingAuthority = identityIssuingAuthorityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        // Check source_id uniqueness for update
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            identityIssuingAuthorityRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getIdentityIssuingAuthorityId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }

        identityIssuingAuthorityMapper.updateIdentityIssuingAuthority(identityIssuingAuthority, request);
        // Set FK references from IDs in request
        identityIssuingAuthorityMapper.setReferences(identityIssuingAuthority, request);

        return identityIssuingAuthorityMapper.toIdentityIssuingAuthorityResponse(identityIssuingAuthorityRepository.save(identityIssuingAuthority));
    }

    public void deleteIdentityIssuingAuthority(Long id) {
        checkForeignKeyConstraints(id);

        identityIssuingAuthorityRepository.deleteById(id);
    }

    private void checkForeignKeyConstraints(Long id) {
        if (!identityIssuingAuthorityRepository.existsById(id)) {
            throw new NotFoundException(entityName);
        }

        // Check references (RESTRICT strategy) - IdentityIssuingAuthority has 2 FK references
        long cmndCount = employeeRepository.countByIdIssuePlaceCmnd_IdentityIssuingAuthorityId(id);
        long cccdCount = employeeRepository.countByIdIssuePlaceCccd_IdentityIssuingAuthorityId(id);
        long totalCount = cmndCount + cccdCount;

        if (totalCount > 0) {
            throw new CannotDeleteException(
                    "IdentityIssuingAuthority", id, "Employee", totalCount
            );
        }
    }
}
