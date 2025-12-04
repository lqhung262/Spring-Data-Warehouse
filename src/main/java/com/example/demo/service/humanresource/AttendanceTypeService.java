package com.example.demo.service.humanresource;

import com.example.demo.dto.BulkOperationResult;
import com.example.demo.dto.humanresource.AttendanceType.AttendanceTypeRequest;
import com.example.demo.dto.humanresource.AttendanceType.AttendanceTypeResponse;
import com.example.demo.entity.humanresource.AttendanceType;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.AttendanceTypeMapper;
import com.example.demo.repository.humanresource.AttendanceTypeRepository;
import com.example.demo.repository.humanresource.EmployeeWorkShiftRepository;
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
public class AttendanceTypeService {
    final AttendanceTypeRepository attendanceTypeRepository;
    final AttendanceTypeMapper attendanceTypeMapper;
    final EmployeeWorkShiftRepository employeeWorkShiftRepository;
    final EntityManager entityManager;

    @Value("${entities.humanresource.attendancetype}")
    private String entityName;

    public AttendanceTypeResponse createAttendanceType(AttendanceTypeRequest request) {
        // Check source_id uniqueness for create
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            attendanceTypeRepository.findBySourceId(request.getSourceId()).ifPresent(b -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        AttendanceType attendanceType = attendanceTypeMapper.toAttendanceType(request);

        return attendanceTypeMapper.toAttendanceTypeResponse(attendanceTypeRepository.save(attendanceType));
    }

    /**
     * Bulk Upsert với Final Batch Logic, Partial Success Pattern:
     * - Safe Batch: saveAll() - requests không có unique conflicts
     * - Final Batch: save + flush từng request - requests có potential conflicts
     * - Trả về detailed result với success/failure breakdown
     */
    public BulkOperationResult<AttendanceTypeResponse> bulkUpsertAttendanceTypes(
            List<AttendanceTypeRequest> requests) {

        // 1. Define unique field configurations (AttendanceType has 2 unique fields)
        UniqueFieldConfig<AttendanceTypeRequest> sourceIdConfig =
                new UniqueFieldConfig<>("source_id", AttendanceTypeRequest::getSourceId);
        UniqueFieldConfig<AttendanceTypeRequest> codeConfig =
                new UniqueFieldConfig<>("attendance_type_code", AttendanceTypeRequest::getAttendanceTypeCode);
        UniqueFieldConfig<AttendanceTypeRequest> nameConfig =
                new UniqueFieldConfig<>("name", AttendanceTypeRequest::getName);

        // 2. Define entity fetchers for each unique field
        Map<String, Function<Set<String>, List<AttendanceType>>> entityFetchers = new HashMap<>();
        entityFetchers.put("source_id", attendanceTypeRepository::findBySourceIdIn);
        entityFetchers.put("attendance_type_code", attendanceTypeRepository::findByAttendanceTypeCodeIn);
        entityFetchers.put("name", attendanceTypeRepository::findByNameIn);

        // 2.5. Define entity field extractors
        Map<String, Function<AttendanceType, String>> entityFieldExtractors = new HashMap<>();
        entityFieldExtractors.put("source_id", AttendanceType::getSourceId);
        entityFieldExtractors.put("attendance_type_code", AttendanceType::getAttendanceTypeCode);
        entityFieldExtractors.put("name", AttendanceType::getName);

        // 3. Setup unique fields using helper
        UniqueFieldsSetupHelper.UniqueFieldsSetup<AttendanceTypeRequest> setup =
                UniqueFieldsSetupHelper.buildUniqueFieldsSetup(
                        requests,
                        entityFetchers,
                        entityFieldExtractors,
                        sourceIdConfig,
                        codeConfig,
                        nameConfig
                );

        // 4.  Build bulk upsert config
        BulkUpsertConfig<AttendanceTypeRequest, AttendanceType, AttendanceTypeResponse> config =
                BulkUpsertConfig.<AttendanceTypeRequest, AttendanceType, AttendanceTypeResponse>builder()
                        .uniqueFieldExtractors(setup.getUniqueFieldExtractors())
                        .existingValuesMaps(setup.getExistingValuesMaps())
                        .entityToResponseMapper(attendanceTypeMapper::toAttendanceTypeResponse)
                        .requestToEntityMapper(attendanceTypeMapper::toAttendanceType)
                        .entityUpdater(attendanceTypeMapper::updateAttendanceType)
                        .existingEntityFinder(this::findExistingEntityForUpsert)
                        .repositorySaver(attendanceTypeRepository::saveAll)
                        .repositorySaveAndFlusher(attendanceTypeRepository::saveAndFlush)
                        .entityManagerClearer(entityManager::clear)
                        .build();

        // 5. Execute bulk upsert
        BulkUpsertProcessor<AttendanceTypeRequest, AttendanceType, AttendanceTypeResponse> processor =
                new BulkUpsertProcessor<>(config);

        return processor.execute(requests);
    }

    /**
     * Helper: Find existing entity
     */
    private AttendanceType findExistingEntityForUpsert(AttendanceTypeRequest request) {
        if (request.getSourceId() != null && !request.getSourceId().trim().isEmpty()) {
            Optional<AttendanceType> bySourceId = attendanceTypeRepository.findBySourceId(request.getSourceId());
            if (bySourceId.isPresent()) {
                return bySourceId.get();
            }
        }

        return null;
    }

    // ========================= BULK DELETE  ========================

    public BulkOperationResult<Long> bulkDeleteAttendanceTypes(List<Long> ids) {

        // Build config
        BulkDeleteConfig<AttendanceType> config = BulkDeleteConfig.<AttendanceType>builder()
                .entityFinder(id -> attendanceTypeRepository.findById(id).orElse(null))
                .foreignKeyConstraintsChecker(this::checkReferenceBeforeDelete)
                .repositoryDeleter(attendanceTypeRepository::deleteById)
                .entityName(entityName)
                .build();

        // Execute với processor
        BulkDeleteProcessor<AttendanceType> processor = new BulkDeleteProcessor<>(config);

        return processor.execute(ids);
    }


    public List<AttendanceTypeResponse> getAttendanceTypes(Pageable pageable) {
        Page<AttendanceType> page = attendanceTypeRepository.findAll(pageable);
        return page.getContent()
                .stream().map(attendanceTypeMapper::toAttendanceTypeResponse).toList();
    }

    public AttendanceTypeResponse getAttendanceType(Long id) {
        return attendanceTypeMapper.toAttendanceTypeResponse(attendanceTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public AttendanceTypeResponse updateAttendanceType(Long id, AttendanceTypeRequest request) {
        AttendanceType attendanceType = attendanceTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        // Check source_id uniqueness for update
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            attendanceTypeRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getAttendanceTypeId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }

        attendanceTypeMapper.updateAttendanceType(attendanceType, request);

        return attendanceTypeMapper.toAttendanceTypeResponse(attendanceTypeRepository.save(attendanceType));
    }

    public void deleteAttendanceType(Long id) {
        checkReferenceBeforeDelete(id);

        attendanceTypeRepository.deleteById(id);
    }

    private void checkReferenceBeforeDelete(Long id) {
        if (!attendanceTypeRepository.existsById(id)) {
            throw new NotFoundException(entityName);
        }

        // Check references (RESTRICT strategy)
        long refCount = employeeWorkShiftRepository.countByAttendanceType_AttendanceTypeId(id);
        if (refCount > 0) {
            throw new CannotDeleteException(
                    "AttendanceType", id, "EmployeeWorkShift", refCount
            );
        }
    }
}
