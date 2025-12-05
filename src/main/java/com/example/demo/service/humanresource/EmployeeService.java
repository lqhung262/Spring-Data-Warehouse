package com.example.demo.service.humanresource;

import com.example.demo.dto.BulkOperationError;
import com.example.demo.dto.BulkOperationResult;
import com.example.demo.dto.humanresource.Employee.EmployeeRequest;
import com.example.demo.dto.humanresource.Employee.EmployeeResponse;
import com.example.demo.dto.humanresource.EmployeeAttendanceMachine.EmployeeAttendanceMachineRequest;
import com.example.demo.dto.humanresource.EmployeeDecision.EmployeeDecisionRequest;
import com.example.demo.dto.humanresource.EmployeeEducation.EmployeeEducationRequest;
import com.example.demo.dto.humanresource.EmployeeWorkLocation.EmployeeWorkLocationRequest;
import com.example.demo.dto.humanresource.EmployeeWorkShift.EmployeeWorkShiftRequest;
import com.example.demo.entity.humanresource.*;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.*;
import com.example.demo.repository.humanresource.*;
import com.example.demo.util.BulkOperationUtils;
import com.example.demo.util.bulk.BulkDeleteConfig;
import com.example.demo.util.bulk.BulkDeleteProcessor;
import com.example.demo.util.bulk.BulkOperationResultBuilder;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class EmployeeService {
    final EmployeeRepository employeeRepository;
    final EmployeeMapper employeeMapper;

    final EmployeeDecisionRepository employeeDecisionRepository;
    final EmployeeEducationRepository employeeEducationRepository;
    final EmployeeAttendanceMachineRepository employeeAttendanceMachineRepository;
    final EmployeeWorkLocationRepository employeeWorkLocationRepository;
    final EmployeeWorkShiftRepository employeeWorkShiftRepository;

    final EmployeeDecisionMapper employeeDecisionMapper;
    final EmployeeEducationMapper employeeEducationMapper;
    final EmployeeAttendanceMachineMapper employeeAttendanceMachineMapper;
    final EmployeeWorkLocationMapper employeeWorkLocationMapper;
    final EmployeeWorkShiftMapper employeeWorkShiftMapper;

    final EntityManager entityManager;

    @Value("${entities.humanresource.employee}")
    private String entityName;

    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        // Check source_id uniqueness for create
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            employeeRepository.findBySourceId(request.getSourceId()).ifPresent(e -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        Employee employee = employeeMapper.toEmployee(request);
        // Set all FK references from IDs in request
        employeeMapper.setReferences(employee, request);

        if (employee.getCreatedBy() == null) employee.setCreatedBy(1L);
        if (employee.getUpdatedBy() == null) employee.setUpdatedBy(1L);

        Employee saved = employeeRepository.save(employee);

        // delegate child creation to helpers (these will validate duplicates)
        Set<EmployeeDecision> createdDecisions = new HashSet<>(createDecisions(saved, request.getEmployeeDecisions()));
        saved.setEmployeeDecisionList(createdDecisions);

        Set<EmployeeEducation> createdEducations = new HashSet<>(createEducations(saved, request.getEmployeeEducations()));
        saved.setEmployeeEducationList(createdEducations);

        Set<EmployeeAttendanceMachine> createdMachines = new HashSet<>(createAttendanceMachines(saved, request.getEmployeeAttendanceMachines()));
        saved.setEmployeeAttendanceMachineList(createdMachines);

        Set<EmployeeWorkLocation> createdLocations = new HashSet<>(createWorkLocations(saved, request.getEmployeeWorkLocations()));
        saved.setEmployeeWorkLocationList(createdLocations);

        EmployeeWorkShift shift = createOrUpdateWorkShift(saved, request.getEmployeeWorkShift());
        saved.setEmployeeWorkShift(shift);

        loadChildCollections(saved);
        return employeeMapper.toEmployeeResponse(saved);
    }

    public List<EmployeeResponse> getEmployees(Pageable pageable) {
        return employeeRepository.findAll(pageable).getContent().stream()
                .map(e -> {
                    loadChildCollections(e);
                    return employeeMapper.toEmployeeResponse(e);
                })
                .toList();
    }

    public EmployeeResponse getEmployee(Long id) {
        Employee emp = employeeRepository.findById(id).orElseThrow(() -> new NotFoundException(entityName));
        loadChildCollections(emp);
        return employeeMapper.toEmployeeResponse(emp);
    }

    private void loadChildCollections(Employee employee) {
        if (employee.getEmployeeDecisionList() != null) {
            employee.getEmployeeDecisionList().size();
        }
        if (employee.getEmployeeEducationList() != null) {
            employee.getEmployeeEducationList().size();
        }
        if (employee.getEmployeeAttendanceMachineList() != null) {
            employee.getEmployeeAttendanceMachineList().size();
        }
        if (employee.getEmployeeWorkLocationList() != null) {
            employee.getEmployeeWorkLocationList().size();
        }
        if (employee.getEmployeeWorkShift() != null) {
            employee.getEmployeeWorkShift().getEmployeeWorkShiftId();
        }
    }

    @Transactional
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        // Check source_id uniqueness for update
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            employeeRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }

        // update employee fields
        employeeMapper.updateEmployee(employee, request);
        // Set all FK references from IDs in request
        employeeMapper.setReferences(employee, request);

        Employee saved = employeeRepository.save(employee);

        // replace child collections if provided
        if (request.getEmployeeDecisions() != null) replaceDecisions(saved, request.getEmployeeDecisions());
        if (request.getEmployeeEducations() != null) replaceEducations(saved, request.getEmployeeEducations());
        if (request.getEmployeeAttendanceMachines() != null)
            replaceAttendanceMachines(saved, request.getEmployeeAttendanceMachines());
        if (request.getEmployeeWorkLocations() != null) replaceWorkLocations(saved, request.getEmployeeWorkLocations());
        if (request.getEmployeeWorkShift() != null) {
            EmployeeWorkShift shift = createOrUpdateWorkShift(saved, request.getEmployeeWorkShift());
            saved.setEmployeeWorkShift(shift);
        }

        loadChildCollections(saved);
        return employeeMapper.toEmployeeResponse(saved);
    }

    public void deleteEmployee(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new NotFoundException(entityName);
        }

        // delete child records first
        List<EmployeeDecision> decisions = employeeDecisionRepository.findByEmployee_Id(employeeId);
        if (!decisions.isEmpty()) employeeDecisionRepository.deleteAll(decisions);

        List<EmployeeEducation> educations = employeeEducationRepository.findByEmployee_Id(employeeId);
        if (!educations.isEmpty()) employeeEducationRepository.deleteAll(educations);

        List<EmployeeAttendanceMachine> machines = employeeAttendanceMachineRepository.findByEmployee_Id(employeeId);
        if (!machines.isEmpty()) employeeAttendanceMachineRepository.deleteAll(machines);

        List<EmployeeWorkLocation> locations = employeeWorkLocationRepository.findByEmployee_Id(employeeId);
        if (!locations.isEmpty()) employeeWorkLocationRepository.deleteAll(locations);

        List<EmployeeWorkShift> shifts = employeeWorkShiftRepository.findByEmployee_Id(employeeId);
        if (!shifts.isEmpty()) employeeWorkShiftRepository.deleteAll(shifts);

        // finally delete employee
        employeeRepository.deleteById(employeeId);
    }

    // --- helpers ---
    private Set<EmployeeDecision> createDecisions(Employee employee, Set<EmployeeDecisionRequest> decisions) {
        if (decisions == null || decisions.isEmpty()) return Set.of();
        // check duplicates in input decisionNo
        Set<String> seen = new HashSet<>();
        Set<EmployeeDecision> created = new HashSet<>();
        for (EmployeeDecisionRequest d : decisions) {
            if (!seen.add(d.getDecisionNo()))
                throw new IllegalArgumentException("Duplicate decisionNo in request: " + d.getDecisionNo());
            // check existing in DB
            employeeDecisionRepository.findByDecisionNo(d.getDecisionNo()).ifPresent(x -> {
                throw new IllegalArgumentException("Employee Decision with decisionNo " + d.getDecisionNo() + " already exists.");
            });
            // check composite uniqueness per employee (department, employeeType, jobPosition, jobTitle, jobRank, costCategoryLevel1)
            employeeDecisionRepository.findByEmployee_IdAndDepartment_DepartmentIdAndEmployeeType_EmployeeTypeIdAndJobPosition_JobPositionIdAndJobTitle_JobTitleIdAndJobRank_JobRankIdAndCostCategoryLevel1_ExpenseTypeId(employee.getId(), d.getDepartmentId(), d.getEmployeeTypeId(), d.getJobPositionId(), d.getJobTitleId(), d.getJobRankId(), d.getCostCategoryLevel1()).ifPresent(x -> {
                throw new IllegalArgumentException("Employee Decision with same role/department combination already exists for employee " + employee.getId());
            });
            EmployeeDecision dec = employeeDecisionMapper.toEmployeeDecision(d);
            dec.setEmployee(employee);
            // Set all FK references from IDs in request
            employeeDecisionMapper.setReferences(dec, d);
            created.add(employeeDecisionRepository.save(dec));
        }
        return created;
    }

    private void replaceDecisions(Employee employee, Set<EmployeeDecisionRequest> decisions) {
        // delete existing
        List<EmployeeDecision> existing = employeeDecisionRepository.findByEmployee_Id(employee.getId());
        if (!existing.isEmpty()) employeeDecisionRepository.deleteAll(existing);
        // create new
        createDecisions(employee, decisions);
    }

    private Set<EmployeeEducation> createEducations(Employee employee, Set<EmployeeEducationRequest> educations) {
        if (educations == null || educations.isEmpty()) return Set.of();
        Set<String> seen = new HashSet<>();
        Set<EmployeeEducation> created = new HashSet<>();
        for (EmployeeEducationRequest e : educations) {
            String key = e.getMajorId() + "|" + e.getSpecializationId() + "|" + e.getEducationLevelId() + "|" + e.getSchoolId();
            if (!seen.add(key)) throw new IllegalArgumentException("Duplicate education combo in request: " + key);
            employeeEducationRepository.findByEmployee_IdAndMajor_MajorIdAndSpecialization_SpecializationIdAndEducationLevel_EducationLevelIdAndSchool_SchoolId(
                    employee.getId(),
                    e.getMajorId(),
                    e.getSpecializationId(),
                    e.getEducationLevelId(),
                    e.getSchoolId()
            ).ifPresent(x -> {
                throw new IllegalArgumentException("Employee Education with same child ids already exists for employee " + employee.getId());
            });
            EmployeeEducation ee = employeeEducationMapper.toEmployeeEducation(e);
            ee.setEmployee(employee);
            // Set all FK references from IDs in request
            employeeEducationMapper.setReferences(ee, e);
            created.add(employeeEducationRepository.save(ee));
        }
        return created;
    }

    private void replaceEducations(Employee employee, Set<EmployeeEducationRequest> educations) {
        List<EmployeeEducation> existing = employeeEducationRepository.findByEmployee_Id(employee.getId());
        if (!existing.isEmpty()) employeeEducationRepository.deleteAll(existing);
        createEducations(employee, educations);
    }

    private Set<EmployeeAttendanceMachine> createAttendanceMachines(Employee employee, Set<EmployeeAttendanceMachineRequest> machines) {
        if (machines == null || machines.isEmpty()) return Set.of();
        Set<Long> seen = new HashSet<>();
        Set<EmployeeAttendanceMachine> created = new HashSet<>();
        for (EmployeeAttendanceMachineRequest m : machines) {
            if (!seen.add(m.getMachineId()))
                throw new IllegalArgumentException("Duplicate machineId in request: " + m.getMachineId());
            employeeAttendanceMachineRepository.findByEmployee_IdAndMachine_AttendanceMachineId(employee.getId(), m.getMachineId()).ifPresent(x -> {
                throw new IllegalArgumentException("Employee Attendance Machine with machineId " + m.getMachineId() + " already exists for employee " + employee.getId());
            });
            EmployeeAttendanceMachine eam = employeeAttendanceMachineMapper.toEmployeeAttendanceMachine(m);
            eam.setEmployee(employee);
            // Set all FK references from IDs in request
            employeeAttendanceMachineMapper.setReferences(eam, m);
            created.add(employeeAttendanceMachineRepository.save(eam));
        }
        return created;
    }

    private void replaceAttendanceMachines(Employee employee, Set<EmployeeAttendanceMachineRequest> machines) {
        List<EmployeeAttendanceMachine> existing = employeeAttendanceMachineRepository.findByEmployee_Id(employee.getId());
        if (!existing.isEmpty()) employeeAttendanceMachineRepository.deleteAll(existing);
        createAttendanceMachines(employee, machines);
    }

    private Set<EmployeeWorkLocation> createWorkLocations(Employee employee, Set<EmployeeWorkLocationRequest> locations) {
        if (locations == null || locations.isEmpty()) return Set.of();
        Set<Long> seen = new HashSet<>();
        Set<EmployeeWorkLocation> created = new HashSet<>();
        for (EmployeeWorkLocationRequest l : locations) {
            if (!seen.add(l.getWorkLocationId()))
                throw new IllegalArgumentException("Duplicate workLocationId in request: " + l.getWorkLocationId());
            employeeWorkLocationRepository.findByEmployee_IdAndWorkLocation_WorkLocationId(employee.getId(), l.getWorkLocationId()).ifPresent(x -> {
                throw new IllegalArgumentException("Employee Work Location with workLocationId " + l.getWorkLocationId() + " already exists for employee " + employee.getId());
            });
            EmployeeWorkLocation ewl = employeeWorkLocationMapper.toEmployeeWorkLocation(l);
            ewl.setEmployee(employee);
            // Set all FK references from IDs in request
            employeeWorkLocationMapper.setReferences(ewl, l);
            created.add(employeeWorkLocationRepository.save(ewl));
        }
        return created;
    }

    private void replaceWorkLocations(Employee employee, Set<EmployeeWorkLocationRequest> locations) {
        List<EmployeeWorkLocation> existing = employeeWorkLocationRepository.findByEmployee_Id(employee.getId());
        if (!existing.isEmpty()) employeeWorkLocationRepository.deleteAll(existing);
        createWorkLocations(employee, locations);
    }

    private EmployeeWorkShift createOrUpdateWorkShift(Employee employee, EmployeeWorkShiftRequest wsReq) {
        if (wsReq == null) return null;

        // If there is already a work shift for this employee (1-0..1), update it
        List<EmployeeWorkShift> existingList = employeeWorkShiftRepository.findByEmployee_Id(employee.getId());
        if (existingList != null && !existingList.isEmpty()) {
            EmployeeWorkShift shift = existingList.getFirst();
            employeeWorkShiftMapper.updateEmployeeWorkShift(shift, wsReq);
            shift.setEmployee(employee);
            // Set all FK references from IDs in request
            employeeWorkShiftMapper.setReferences(shift, wsReq);
            return employeeWorkShiftRepository.save(shift);
        }

        // Otherwise create new
        EmployeeWorkShift shift = employeeWorkShiftMapper.toEmployeeWorkShift(wsReq);
        shift.setEmployee(employee);
        // Set all FK references from IDs in request
        employeeWorkShiftMapper.setReferences(shift, wsReq);
        return employeeWorkShiftRepository.save(shift);
    }

    // ==================================== BULK OPERATIONS ====================================

    /**
     * Bulk Upsert Employee với Final Batch Logic, Partial Success Pattern:
     * - Safe Batch: saveAll() - requests không có unique conflicts
     * - Final Batch: gọi createEmployee/updateEmployee từng request - requests có potential conflicts
     * - Trả về detailed result với success/failure breakdown
     */
    @Transactional
    public BulkOperationResult<EmployeeResponse> bulkUpsertEmployees(List<EmployeeRequest> requests) {
        log.info("Starting bulk upsert for {} employee requests", requests.size());
        long startTime = System.currentTimeMillis();

        // 1. Define unique field extractors cho 7 unique fields
        Map<String, Function<EmployeeRequest, String>> uniqueFieldExtractors = new LinkedHashMap<>();
        uniqueFieldExtractors.put("employeeCode", EmployeeRequest::getEmployeeCode);
        uniqueFieldExtractors.put("sourceId", EmployeeRequest::getSourceId);
        uniqueFieldExtractors.put("corporationCode", EmployeeRequest::getCorporationCode);
        uniqueFieldExtractors.put("taxCode", EmployeeRequest::getTaxCode);
        uniqueFieldExtractors.put("socialInsuranceNo", EmployeeRequest::getSocialInsuranceNo);
        uniqueFieldExtractors.put("socialInsuranceCode", EmployeeRequest::getSocialInsuranceCode);
        uniqueFieldExtractors.put("healthInsuranceCard", EmployeeRequest::getHealthInsuranceCard);

        // 2. Fetch existing values từ database cho tất cả 7 unique fields
        Map<String, Set<String>> existingValuesMaps = new LinkedHashMap<>();

        // Extract all unique values from requests
        Set<String> employeeCodes = requests.stream()
                .map(EmployeeRequest::getEmployeeCode)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Set<String> sourceIds = requests.stream()
                .map(EmployeeRequest::getSourceId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Set<String> corporationCodes = requests.stream()
                .map(EmployeeRequest::getCorporationCode)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Set<String> taxCodes = requests.stream()
                .map(EmployeeRequest::getTaxCode)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Set<String> socialInsuranceNos = requests.stream()
                .map(EmployeeRequest::getSocialInsuranceNo)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Set<String> socialInsuranceCodes = requests.stream()
                .map(EmployeeRequest::getSocialInsuranceCode)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Set<String> healthInsuranceCards = requests.stream()
                .map(EmployeeRequest::getHealthInsuranceCard)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // Fetch existing entities for each unique field - just extract the field values
        if (!employeeCodes.isEmpty()) {
            existingValuesMaps.put("employeeCode",
                    employeeRepository.findByEmployeeCodeIn(employeeCodes).stream()
                            .map(Employee::getEmployeeCode)
                            .collect(Collectors.toSet()));
        } else {
            existingValuesMaps.put("employeeCode", new HashSet<>());
        }

        if (!sourceIds.isEmpty()) {
            existingValuesMaps.put("sourceId",
                    employeeRepository.findBySourceIdIn(sourceIds).stream()
                            .map(Employee::getSourceId)
                            .collect(Collectors.toSet()));
        } else {
            existingValuesMaps.put("sourceId", new HashSet<>());
        }

        if (!corporationCodes.isEmpty()) {
            existingValuesMaps.put("corporationCode",
                    employeeRepository.findByCorporationCodeIn(corporationCodes).stream()
                            .map(Employee::getCorporationCode)
                            .collect(Collectors.toSet()));
        } else {
            existingValuesMaps.put("corporationCode", new HashSet<>());
        }

        if (!taxCodes.isEmpty()) {
            existingValuesMaps.put("taxCode",
                    employeeRepository.findByTaxCodeIn(taxCodes).stream()
                            .map(Employee::getTaxCode)
                            .collect(Collectors.toSet()));
        } else {
            existingValuesMaps.put("taxCode", new HashSet<>());
        }

        if (!socialInsuranceNos.isEmpty()) {
            existingValuesMaps.put("socialInsuranceNo",
                    employeeRepository.findBySocialInsuranceNoIn(socialInsuranceNos).stream()
                            .map(Employee::getSocialInsuranceNo)
                            .collect(Collectors.toSet()));
        } else {
            existingValuesMaps.put("socialInsuranceNo", new HashSet<>());
        }

        if (!socialInsuranceCodes.isEmpty()) {
            existingValuesMaps.put("socialInsuranceCode",
                    employeeRepository.findBySocialInsuranceCodeIn(socialInsuranceCodes).stream()
                            .map(Employee::getSocialInsuranceCode)
                            .collect(Collectors.toSet()));
        } else {
            existingValuesMaps.put("socialInsuranceCode", new HashSet<>());
        }

        if (!healthInsuranceCards.isEmpty()) {
            existingValuesMaps.put("healthInsuranceCard",
                    employeeRepository.findByHealthInsuranceCardIn(healthInsuranceCards).stream()
                            .map(Employee::getHealthInsuranceCard)
                            .collect(Collectors.toSet()));
        } else {
            existingValuesMaps.put("healthInsuranceCard", new HashSet<>());
        }

        // 3.  Classify batch: safe vs final
        BulkOperationUtils.BatchClassification<EmployeeRequest> classification =
                BulkOperationUtils.classifyBatchByUniqueFields(
                        requests,
                        uniqueFieldExtractors,
                        existingValuesMaps
                );

        // 4. Initialize result tracking
        List<EmployeeResponse> successResults = new ArrayList<>();
        List<BulkOperationError> errors = new ArrayList<>();

        // 5. Process SAFE BATCH - no conflicts, can use batch processing
        if (classification.hasSafeBatch()) {
            log.info("Processing safe batch: {} requests", classification.getSafeBatch().size());
            processSafeBatch(classification.getSafeBatch(), successResults, errors, 0);
        }

        // 6. Process FINAL BATCH - có conflicts, phải xử lý từng request riêng biệt
        if (classification.hasFinalBatch()) {
            log.warn("Processing final batch: {} requests with potential conflicts",
                    classification.getFinalBatch().size());
            int finalBatchStartIndex = classification.getSafeBatch().size();
            processFinalBatch(classification.getFinalBatch(), successResults, errors, finalBatchStartIndex);
        }

        // 7. Build and return result
        long duration = System.currentTimeMillis() - startTime;
        BulkOperationResult<EmployeeResponse> result = BulkOperationResultBuilder.build(
                requests.size(),
                successResults,
                errors,
                duration
        );

        log.info("Bulk upsert employees completed: {}/{} succeeded, {}/{} failed in {}ms",
                result.getSuccessCount(), result.getTotalRequests(),
                result.getFailedCount(), result.getTotalRequests(),
                duration);

        return result;
    }

    /**
     * Process safe batch - không có unique conflicts
     * Có thể xử lý batch vì đã chắc chắn không có duplicate
     */
    private void processSafeBatch(
            List<EmployeeRequest> safeBatch,
            List<EmployeeResponse> successResults,
            List<BulkOperationError> errors,
            int startIndex) {

        try {
            // Process all safe requests in batch
            List<Employee> entitiesToSave = new ArrayList<>();

            for (int i = 0; i < safeBatch.size(); i++) {
                EmployeeRequest request = safeBatch.get(i);
                int globalIndex = startIndex + i;

                try {
                    // Find existing entity for update
                    Employee existingEntity = findExistingEntityForUpsert(request);

                    Employee entity;
                    if (existingEntity != null) {
                        // UPDATE: Use mapper to update existing entity
                        employeeMapper.updateEmployee(existingEntity, request);
                        employeeMapper.setReferences(existingEntity, request);
                        entity = existingEntity;
                    } else {
                        // CREATE: Create new entity
                        entity = employeeMapper.toEmployee(request);
                        employeeMapper.setReferences(entity, request);

                        if (entity.getCreatedBy() == null) entity.setCreatedBy(1L);
                        if (entity.getUpdatedBy() == null) entity.setUpdatedBy(1L);
                    }

                    entitiesToSave.add(entity);

                } catch (Exception e) {
                    log.error("Error preparing employee at index {}: {}", globalIndex, e.getMessage());
                    errors.add(buildError(globalIndex, request, e.getMessage(), e));
                }
            }

            // Batch save all entities
            if (!entitiesToSave.isEmpty()) {
                List<Employee> savedEntities = employeeRepository.saveAll(entitiesToSave);

                // Process child entities for each saved employee
                for (int i = 0; i < savedEntities.size(); i++) {
                    Employee saved = savedEntities.get(i);
                    EmployeeRequest request = safeBatch.get(i);

                    try {
                        // Handle child entities AFTER parent is saved
                        processChildEntities(saved, request);

                        // Load collections and convert to response
                        loadChildCollections(saved);
                        successResults.add(employeeMapper.toEmployeeResponse(saved));

                    } catch (Exception e) {
                        log.error("Error processing child entities for employee {}: {}",
                                saved.getId(), e.getMessage());
                        errors.add(buildError(startIndex + i, request,
                                "Parent saved but child processing failed: " + e.getMessage(), e));
                    }
                }
            }

        } catch (Exception e) {
            log.error("Safe batch processing failed: {}", e.getMessage(), e);
            // If batch fails, fall back to individual processing
            for (int i = 0; i < safeBatch.size(); i++) {
                processIndividualRequest(safeBatch.get(i), startIndex + i, successResults, errors);
            }
        }
    }

    /**
     * Process final batch - có potential conflicts
     * Phải xử lý từng request riêng, gọi createEmployee/updateEmployee
     */
    private void processFinalBatch(
            List<EmployeeRequest> finalBatch,
            List<EmployeeResponse> successResults,
            List<BulkOperationError> errors,
            int startIndex) {

        for (int i = 0; i < finalBatch.size(); i++) {
            EmployeeRequest request = finalBatch.get(i);
            int globalIndex = startIndex + i;

            processIndividualRequest(request, globalIndex, successResults, errors);
        }
    }

    /**
     * Process individual request - gọi createEmployee hoặc updateEmployee
     */
    private void processIndividualRequest(
            EmployeeRequest request,
            int globalIndex,
            List<EmployeeResponse> successResults,
            List<BulkOperationError> errors) {

        try {
            // Find existing entity to determine create vs update
            Employee existingEntity = findExistingEntityForUpsert(request);

            EmployeeResponse response;
            if (existingEntity != null) {
                // UPDATE: Gọi updateEmployee method đã có
                response = updateEmployee(existingEntity.getId(), request);
            } else {
                // CREATE: Gọi createEmployee method đã có
                response = createEmployee(request);
            }

            successResults.add(response);
            entityManager.flush();
            entityManager.clear();

        } catch (AlreadyExistsException e) {
            log.warn("Employee already exists at index {}: {}", globalIndex, e.getMessage());
            errors.add(buildError(globalIndex, request,
                    "Employee already exists: " + e.getMessage(), e));
        } catch (Exception e) {
            log.error("Error processing employee at index {}: {}", globalIndex, e.getMessage());
            errors.add(buildError(globalIndex, request, e.getMessage(), e));
        }
    }

    /**
     * Process child entities: decisions, educations, attendance machines, work locations, work shift
     */
    private void processChildEntities(Employee employee, EmployeeRequest request) {
        // Handle child collections if provided in request
        if (request.getEmployeeDecisions() != null && !request.getEmployeeDecisions().isEmpty()) {
            replaceDecisions(employee, request.getEmployeeDecisions());
        }

        if (request.getEmployeeEducations() != null && !request.getEmployeeEducations().isEmpty()) {
            replaceEducations(employee, request.getEmployeeEducations());
        }

        if (request.getEmployeeAttendanceMachines() != null && !request.getEmployeeAttendanceMachines().isEmpty()) {
            replaceAttendanceMachines(employee, request.getEmployeeAttendanceMachines());
        }

        if (request.getEmployeeWorkLocations() != null && !request.getEmployeeWorkLocations().isEmpty()) {
            replaceWorkLocations(employee, request.getEmployeeWorkLocations());
        }

        if (request.getEmployeeWorkShift() != null) {
            EmployeeWorkShift shift = createOrUpdateWorkShift(employee, request.getEmployeeWorkShift());
            employee.setEmployeeWorkShift(shift);
        }
    }

    /**
     * Find existing entity for upsert based on sourceId (primary identifier)
     */
    private Employee findExistingEntityForUpsert(EmployeeRequest request) {
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            return employeeRepository.findBySourceId(request.getSourceId()).orElse(null);
        }
        return null;
    }

    /**
     * Build error object
     */
    private BulkOperationError buildError(int index, EmployeeRequest request, String message, Exception exception) {
        return BulkOperationError.builder()
                .index(index)
                .identifier("sourceId: " + request.getSourceId() +
                        ", employeeCode: " + request.getEmployeeCode())
                .errorMessage(message)
                .errorType(exception != null ? exception.getClass().getSimpleName() : "ValidationError")
                .build();
    }

    /**
     * Bulk Delete Employees với Partial Success Pattern
     * Gọi deleteEmployee method cho từng ID
     */
    @Transactional
    public BulkOperationResult<Long> bulkDeleteEmployees(List<Long> ids) {
        log.info("Starting bulk delete for {} employee IDs", ids.size());

        // 1. Setup bulk delete config
        BulkDeleteConfig<Employee> config = BulkDeleteConfig.<Employee>builder()
                .entityName(entityName)
                .foreignKeyConstraintsChecker(this::checkEmployeeForeignKeyConstraints)
                .repositoryDeleter(this::deleteEmployee)
                .build();

        // 2. Create processor and execute
        BulkDeleteProcessor<Employee> processor = new BulkDeleteProcessor<>(config);
        BulkOperationResult<Long> result = processor.execute(ids);

        log.info("Bulk delete employees completed: {}/{} succeeded, {}/{} failed",
                result.getSuccessCount(), result.getTotalRequests(),
                result.getFailedCount(), result.getTotalRequests());

        return result;
    }

    /**
     * Check foreign key constraints (also validates existence)
     */
    private void checkEmployeeForeignKeyConstraints(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new NotFoundException(entityName);
        }

        // Check if employee has any references that would prevent deletion
        // For Employee, we allow deletion and cascade to child entities
        // So no foreign key checks needed here
    }

}


