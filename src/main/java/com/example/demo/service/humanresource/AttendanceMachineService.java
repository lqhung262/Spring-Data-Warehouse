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
import com.example.demo.util.BulkOperationUtils;
import com.example.demo.util.bulk.BulkDeleteConfig;
import com.example.demo.util.bulk.BulkDeleteProcessor;
import com.example.demo.util.bulk.BulkUpsertConfig;
import com.example.demo.util.bulk.BulkUpsertProcessor;
import com.example.demo.dto.BulkOperationResult;
import jakarta.transaction.Transactional;
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
import java.util.stream.Collectors;

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


//    /**
//     * Bulk Upsert với Final Batch Logic, Partial Success Pattern:
//     * - Safe Batch: saveAll() - requests không có unique conflicts
//     * - Final Batch: save + flush từng request - requests có potential conflicts
//     * - Trả về detailed result với success/failure breakdown
//     */
//    public BulkOperationResult<AttendanceMachineResponse> bulkUpsertProvinceCities(
//            List<AttendanceMachineRequest> requests) {
//
//        // 1. Setup unique field extractors
//        Map<String, Function<AttendanceMachineRequest, String>> uniqueFieldExtractors = new LinkedHashMap<>();
//        uniqueFieldExtractors.put("attendance_machine_code", AttendanceMachineRequest::getAttendanceMachineCode);
//        uniqueFieldExtractors.put("source_id", AttendanceMachineRequest::getSourceId);
//        uniqueFieldExtractors.put("name", AttendanceMachineRequest::getName);
//
//        // 2. Lấy all value từ requests cho mỗi unique field
//        Set<String> allCodes = BulkOperationUtils.extractUniqueValues(
//                requests, AttendanceMachineRequest::getAttendanceMachineCode);
//        Set<String> allSourceIds = BulkOperationUtils.extractUniqueValues(
//                requests, AttendanceMachineRequest::getSourceId);
//        Set<String> allNames = BulkOperationUtils.extractUniqueValues(
//                requests, AttendanceMachineRequest::getName);
//
//        // Lấy existing values từ DB cho mỗi unique field
//        Map<String, Set<String>> existingValuesMaps = new HashMap<>();
//        existingValuesMaps.put("attendance_machine_code",
//                attendanceMachineRepository.findByAttendanceMachineCode(allCodes)
//                        .stream()
//                        .map(AttendanceMachine::getAttendanceMachineCode)
//                        .collect(Collectors.toSet()));
//        existingValuesMaps.put("source_id",
//                attendanceMachineRepository.findBySourceIdIn(allSourceIds)
//                        .stream()
//                        .map(AttendanceMachine::getSourceId)
//                        .collect(Collectors.toSet()));
//        existingValuesMaps.put("name",
//                attendanceMachineRepository.findByNameIn(allNames)
//                        .stream()
//                        .map(AttendanceMachine::getName)
//                        .collect(Collectors.toSet()));
//
//        // 3. Build config
//        BulkUpsertConfig<AttendanceMachineRequest, AttendanceMachine, AttendanceMachineResponse> config =
//                BulkUpsertConfig.<AttendanceMachineRequest, AttendanceMachine, AttendanceMachineResponse>builder()
//                        .uniqueFieldExtractors(uniqueFieldExtractors)
//                        .existingValuesMaps(existingValuesMaps)
//                        .entityToResponseMapper(attendanceMachineMapper::toAttendanceMachineResponse)
//                        .requestToEntityMapper(attendanceMachineMapper::toAttendanceMachine)
//                        .entityUpdater(attendanceMachineMapper::updateAttendanceMachine)
//                        .existingEntityFinder(this::findExistingEntityForUpsert)
//                        // Method reference matches RepositorySaveAll interface
//                        .repositorySaver(attendanceMachineRepository::saveAll)
//                        // Method reference matches RepositorySave interface
//                        .repositorySaveAndFlusher(attendanceMachineRepository::saveAndFlush)
//                        .entityManagerClearer(entityManager::clear)
//                        .build();
//
//        // 4. Execute
//        BulkUpsertProcessor<AttendanceMachineRequest, AttendanceMachine, AttendanceMachineResponse> processor =
//                new BulkUpsertProcessor<>(config);
//
//        return processor.execute(requests);
//    }
//
//    /**
//     * Helper: Find existing entity
//     */
//    private AttendanceMachine findExistingEntityForUpsert(AttendanceMachineRequest request) {
//        if (request.getSourceId() != null && !request.getSourceId().trim().isEmpty()) {
//            Optional<AttendanceMachine> bySourceId = attendanceMachineRepository.findBySourceId(request.getSourceId());
//            if (bySourceId.isPresent()) {
//                return bySourceId.get();
//            }
//        }
//
//        if (request.getName() != null && !request.getName().trim().isEmpty()) {
//            Optional<AttendanceMachine> byName = attendanceMachineRepository.findByName(request.getName());
//            if (byName.isPresent()) {
//                return byName.get();
//            }
//        }
//
//        return null;
//    }
//
//    // ========================= BULK DELETE  ========================
//
//    public BulkOperationResult<Long> bulkDeleteProvinceCities(List<Long> ids) {
//
//        // Build config
//        BulkDeleteConfig<AttendanceMachine> config = BulkDeleteConfig.<AttendanceMachine>builder()
//                .entityFinder(id -> attendanceMachineRepository.findById(id).orElse(null))
//                .foreignKeyConstraintsChecker(this::checkForeignKeyConstraints)
//                .repositoryDeleter(attendanceMachineRepository::deleteById)
//                .entityName(entityName)
//                .build();
//
//        // Execute với processor
//        BulkDeleteProcessor<AttendanceMachine> processor = new BulkDeleteProcessor<>(config);
//
//        return processor.execute(ids);
//    }

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

        attendanceMachineRepository.deleteById(id);
    }
}
