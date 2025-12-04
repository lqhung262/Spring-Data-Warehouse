package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.AttendanceMachine.AttendanceMachineRequest;
import com.example.demo.dto.humanresource.AttendanceMachine.AttendanceMachineResponse;
import com.example.demo.entity.humanresource.AttendanceMachine;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.AttendanceMachineMapper;
import com.example.demo.repository.humanresource.AttendanceMachineRepository;
import com.example.demo.repository.humanresource.EmployeeAttendanceMachineRepository;
import com.example.demo.exception.CannotDeleteException;
import com.example.demo.util.bulk.*;
import com.example.demo.dto.BulkOperationResult;
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
public class AttendanceMachineService {
    final AttendanceMachineRepository attendanceMachineRepository;
    final AttendanceMachineMapper attendanceMachineMapper;
    final EmployeeAttendanceMachineRepository employeeAttendanceMachineRepository;
    final EntityManager entityManager;


    @Value("${entities.humanresource.attendancemachine}")
    private String entityName;


    public AttendanceMachineResponse createAttendanceMachine(AttendanceMachineRequest request) {
        // Check source_id uniqueness for create
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            attendanceMachineRepository.findBySourceId(request.getSourceId()).ifPresent(b -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        AttendanceMachine attendanceMachine = attendanceMachineMapper.toAttendanceMachine(request);

        return attendanceMachineMapper.toAttendanceMachineResponse(attendanceMachineRepository.save(attendanceMachine));
    }


    /**
     * Bulk Upsert với Final Batch Logic, Partial Success Pattern:
     * - Safe Batch: saveAll() - requests không có unique conflicts
     * - Final Batch: save + flush từng request - requests có potential conflicts
     * - Trả về detailed result với success/failure breakdown
     */
    public BulkOperationResult<AttendanceMachineResponse> bulkUpsertAttendanceMachines(
            List<AttendanceMachineRequest> requests) {

        // 1. Define unique field configurations (AttendanceMachine has 2 unique fields)
        UniqueFieldConfig<AttendanceMachineRequest> sourceIdConfig =
                new UniqueFieldConfig<>("source_id", AttendanceMachineRequest::getSourceId);
        UniqueFieldConfig<AttendanceMachineRequest> codeConfig =
                new UniqueFieldConfig<>("attendance_machine_code", AttendanceMachineRequest::getAttendanceMachineCode);
        UniqueFieldConfig<AttendanceMachineRequest> nameConfig =
                new UniqueFieldConfig<>("name", AttendanceMachineRequest::getName);

        // 2. Define entity fetchers for each unique field
        Map<String, Function<Set<String>, List<AttendanceMachine>>> entityFetchers = new HashMap<>();
        entityFetchers.put("source_id", attendanceMachineRepository::findBySourceIdIn);
        entityFetchers.put("attendance_machine_code", attendanceMachineRepository::findByAttendanceMachineCodeIn);
        entityFetchers.put("name", attendanceMachineRepository::findByNameIn);

        // 2.5. Define entity field extractors (to extract values from returned entities)
        Map<String, Function<AttendanceMachine, String>> entityFieldExtractors = new HashMap<>();
        entityFieldExtractors.put("source_id", AttendanceMachine::getSourceId);
        entityFieldExtractors.put("attendance_machine_code", AttendanceMachine::getAttendanceMachineCode);
        entityFieldExtractors.put("name", AttendanceMachine::getName);

        // 3. Setup unique fields using helper
        UniqueFieldsSetupHelper.UniqueFieldsSetup<AttendanceMachineRequest> setup =
                UniqueFieldsSetupHelper.buildUniqueFieldsSetup(
                        requests,
                        entityFetchers,
                        entityFieldExtractors,
                        sourceIdConfig,
                        codeConfig,
                        nameConfig
                );

        // 4.  Build bulk upsert config
        BulkUpsertConfig<AttendanceMachineRequest, AttendanceMachine, AttendanceMachineResponse> config =
                BulkUpsertConfig.<AttendanceMachineRequest, AttendanceMachine, AttendanceMachineResponse>builder()
                        .uniqueFieldExtractors(setup.getUniqueFieldExtractors())
                        .existingValuesMaps(setup.getExistingValuesMaps())
                        .entityToResponseMapper(attendanceMachineMapper::toAttendanceMachineResponse)
                        .requestToEntityMapper(attendanceMachineMapper::toAttendanceMachine)
                        .entityUpdater(attendanceMachineMapper::updateAttendanceMachine)
                        .existingEntityFinder(this::findExistingEntityForUpsert)
                        .repositorySaver(attendanceMachineRepository::saveAll)
                        .repositorySaveAndFlusher(attendanceMachineRepository::saveAndFlush)
                        .entityManagerClearer(entityManager::clear)
                        .build();

        // 5. Execute bulk upsert
        BulkUpsertProcessor<AttendanceMachineRequest, AttendanceMachine, AttendanceMachineResponse> processor =
                new BulkUpsertProcessor<>(config);

        return processor.execute(requests);
    }

    /**
     * Helper: Find existing entity for upsert
     * STRICT LOGIC: Only match by sourceId (primary identifier)
     */
    private AttendanceMachine findExistingEntityForUpsert(AttendanceMachineRequest request) {
        // ONLY match by sourceId (canonical identifier)
        if (request.getSourceId() != null && !request.getSourceId().trim().isEmpty()) {
            Optional<AttendanceMachine> bySourceId = attendanceMachineRepository.findBySourceId(request.getSourceId());
            if (bySourceId.isPresent()) {
                return bySourceId.get();
            }
        }

        // Do NOT fallback to code or name matching
        return null;
    }

    // ========================= BULK DELETE  ========================

    public BulkOperationResult<Long> bulkDeleteAttendanceMachines(List<Long> ids) {

        // Build config
        BulkDeleteConfig<AttendanceMachine> config = BulkDeleteConfig.<AttendanceMachine>builder()
                .entityFinder(id -> attendanceMachineRepository.findById(id).orElse(null))
                .foreignKeyConstraintsChecker(this::checkForeignKeyConstraints)
                .repositoryDeleter(attendanceMachineRepository::deleteById)
                .entityName(entityName)
                .build();

        // Execute với processor
        BulkDeleteProcessor<AttendanceMachine> processor = new BulkDeleteProcessor<>(config);

        return processor.execute(ids);
    }

    public List<AttendanceMachineResponse> getAttendanceMachines(Pageable pageable) {
        Page<AttendanceMachine> page = attendanceMachineRepository.findAll(pageable);
        return page.getContent()
                .stream().map(attendanceMachineMapper::toAttendanceMachineResponse).toList();
    }

    public AttendanceMachineResponse getAttendanceMachine(Long id) {
        return attendanceMachineMapper.toAttendanceMachineResponse(attendanceMachineRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public AttendanceMachineResponse updateAttendanceMachine(Long id, AttendanceMachineRequest request) {
        AttendanceMachine attendanceMachine = attendanceMachineRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        // Check source_id uniqueness for update
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            attendanceMachineRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getAttendanceMachineId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }

        attendanceMachineMapper.updateAttendanceMachine(attendanceMachine, request);

        return attendanceMachineMapper.toAttendanceMachineResponse(attendanceMachineRepository.save(attendanceMachine));
    }

    public void deleteAttendanceMachine(Long id) {
        checkForeignKeyConstraints(id);

        attendanceMachineRepository.deleteById(id);
    }

    private void checkForeignKeyConstraints(Long id) {
        if (!attendanceMachineRepository.existsById(id)) {
            throw new NotFoundException(entityName);
        }

        // Check references (RESTRICT strategy)
        long refCount = employeeAttendanceMachineRepository.countByMachine_AttendanceMachineId(id);
        if (refCount > 0) {
            throw new CannotDeleteException(
                    "AttendanceMachine", id, "EmployeeAttendanceMachine", refCount
            );
        }
    }
}
