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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

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
    final PlatformTransactionManager transactionManager;

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
        return executeEmployeeCreation(request);
    }

    public List<EmployeeResponse> getEmployees(Pageable pageable) {
        // Note: For pageable queries, we fetch lazily to avoid N+1
        // Collections are initialized on-demand when mapping to response
        return employeeRepository.findAll(pageable).getContent().stream()
                .map(e -> {
                    initializeCollections(e);
                    return employeeMapper.toEmployeeResponse(e);
                })
                .toList();
    }

    public EmployeeResponse getEmployee(Long id) {
        // Use EntityGraph to fetch employee with all associations in ONE query (no N+1)
        Employee emp = employeeRepository.findWithAllAssociationsById(id)
                .orElseThrow(() -> new NotFoundException(entityName));
        return employeeMapper.toEmployeeResponse(emp);
    }

    /**
     * Initialize child collections by calling size() - needed only when collections are still lazy.
     * Parent/lookup entities should be loaded via EntityGraph in repository methods.
     * This is a lightweight fallback for cases where entity wasn't fetched with EntityGraph.
     */
    @SuppressWarnings({"ResultOfMethodCallIgnored", "unused"})
    private void initializeCollections(Employee employee) {
        // Trigger initialization of collections by calling size()
        if (employee.getEmployeeDecisionList() != null) {
            employee.getEmployeeDecisionList().size(); // triggers lazy load
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
        // Note: parent entities (gender, maritalStatus, etc.) should already be loaded via EntityGraph
    }

    @Transactional
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        return executeEmployeeUpdate(id, request);
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
     * - Safe Batch: saveAll() trong một transaction - requests không có unique conflicts
     * - Final Batch: mỗi request trong transaction riêng (REQUIRES_NEW) - requests có potential conflicts
     * - Trả về detailed result với success/failure breakdown
     * <p>
     * NOTE: Không dùng @Transactional ở method level để đảm bảo partial commits.
     * Mỗi batch/request sẽ chạy trong transaction riêng qua TransactionTemplate.
     */
    public BulkOperationResult<EmployeeResponse> bulkUpsertEmployees(List<EmployeeRequest> requests) {
        log.info("Starting bulk upsert for {} employee requests", requests.size());
        long startTime = System.currentTimeMillis();

        // 1. Define unique field extractors và fetch existing values
        Map<String, Function<EmployeeRequest, String>> uniqueFieldExtractors = buildUniqueFieldExtractors();
        Map<String, Set<String>> existingValuesMaps = fetchExistingUniqueValues(requests);

        // 2. Classify batch: safe vs final
        BulkOperationUtils.BatchClassification<EmployeeRequest> classification =
                BulkOperationUtils.classifyBatchByUniqueFields(
                        requests,
                        uniqueFieldExtractors,
                        existingValuesMaps
                );

        // 3. Initialize result tracking
        List<EmployeeResponse> successResults = new ArrayList<>();
        List<BulkOperationError> errors = new ArrayList<>();

        // 4. Process batches with per-transaction control
        processBatchesWithTransactions(classification, successResults, errors);

        // 5. Build and return result
        return buildBulkOperationResult(requests.size(), successResults, errors, startTime);
    }

    /**
     * Build unique field extractors for 7 unique fields
     */
    private Map<String, Function<EmployeeRequest, String>> buildUniqueFieldExtractors() {
        Map<String, Function<EmployeeRequest, String>> extractors = new LinkedHashMap<>();
        extractors.put("employeeCode", EmployeeRequest::getEmployeeCode);
        extractors.put("sourceId", EmployeeRequest::getSourceId);
        extractors.put("corporationCode", EmployeeRequest::getCorporationCode);
        extractors.put("taxCode", EmployeeRequest::getTaxCode);
        extractors.put("socialInsuranceNo", EmployeeRequest::getSocialInsuranceNo);
        extractors.put("socialInsuranceCode", EmployeeRequest::getSocialInsuranceCode);
        extractors.put("healthInsuranceCard", EmployeeRequest::getHealthInsuranceCard);
        return extractors;
    }

    /**
     * Fetch existing unique values from database for all 7 unique fields
     */
    private Map<String, Set<String>> fetchExistingUniqueValues(List<EmployeeRequest> requests) {
        Map<String, Set<String>> existingValuesMaps = new LinkedHashMap<>();

        // Extract unique values from requests
        Set<String> employeeCodes = extractUniqueValues(requests, EmployeeRequest::getEmployeeCode);
        Set<String> sourceIds = extractUniqueValues(requests, EmployeeRequest::getSourceId);
        Set<String> corporationCodes = extractUniqueValues(requests, EmployeeRequest::getCorporationCode);
        Set<String> taxCodes = extractUniqueValues(requests, EmployeeRequest::getTaxCode);
        Set<String> socialInsuranceNos = extractUniqueValues(requests, EmployeeRequest::getSocialInsuranceNo);
        Set<String> socialInsuranceCodes = extractUniqueValues(requests, EmployeeRequest::getSocialInsuranceCode);
        Set<String> healthInsuranceCards = extractUniqueValues(requests, EmployeeRequest::getHealthInsuranceCard);

        // Fetch existing values from database
        existingValuesMaps.put("employeeCode", fetchExistingEmployeeCodes(employeeCodes));
        existingValuesMaps.put("sourceId", fetchExistingSourceIds(sourceIds));
        existingValuesMaps.put("corporationCode", fetchExistingCorporationCodes(corporationCodes));
        existingValuesMaps.put("taxCode", fetchExistingTaxCodes(taxCodes));
        existingValuesMaps.put("socialInsuranceNo", fetchExistingSocialInsuranceNos(socialInsuranceNos));
        existingValuesMaps.put("socialInsuranceCode", fetchExistingSocialInsuranceCodes(socialInsuranceCodes));
        existingValuesMaps.put("healthInsuranceCard", fetchExistingHealthInsuranceCards(healthInsuranceCards));

        return existingValuesMaps;
    }

    /**
     * Extract unique values from requests using the provided extractor
     */
    private Set<String> extractUniqueValues(List<EmployeeRequest> requests, Function<EmployeeRequest, String> extractor) {
        return requests.stream()
                .map(extractor)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    /**
     * Fetch existing employee codes from database
     */
    private Set<String> fetchExistingEmployeeCodes(Set<String> values) {
        return values.isEmpty() ? new HashSet<>() :
                employeeRepository.findByEmployeeCodeIn(values).stream()
                        .map(Employee::getEmployeeCode)
                        .collect(Collectors.toSet());
    }

    /**
     * Fetch existing source IDs from database
     */
    private Set<String> fetchExistingSourceIds(Set<String> values) {
        return values.isEmpty() ? new HashSet<>() :
                employeeRepository.findBySourceIdIn(values).stream()
                        .map(Employee::getSourceId)
                        .collect(Collectors.toSet());
    }

    /**
     * Fetch existing corporation codes from database
     */
    private Set<String> fetchExistingCorporationCodes(Set<String> values) {
        return values.isEmpty() ? new HashSet<>() :
                employeeRepository.findByCorporationCodeIn(values).stream()
                        .map(Employee::getCorporationCode)
                        .collect(Collectors.toSet());
    }

    /**
     * Fetch existing tax codes from database
     */
    private Set<String> fetchExistingTaxCodes(Set<String> values) {
        return values.isEmpty() ? new HashSet<>() :
                employeeRepository.findByTaxCodeIn(values).stream()
                        .map(Employee::getTaxCode)
                        .collect(Collectors.toSet());
    }

    /**
     * Fetch existing social insurance numbers from database
     */
    private Set<String> fetchExistingSocialInsuranceNos(Set<String> values) {
        return values.isEmpty() ? new HashSet<>() :
                employeeRepository.findBySocialInsuranceNoIn(values).stream()
                        .map(Employee::getSocialInsuranceNo)
                        .collect(Collectors.toSet());
    }

    /**
     * Fetch existing social insurance codes from database
     */
    private Set<String> fetchExistingSocialInsuranceCodes(Set<String> values) {
        return values.isEmpty() ? new HashSet<>() :
                employeeRepository.findBySocialInsuranceCodeIn(values).stream()
                        .map(Employee::getSocialInsuranceCode)
                        .collect(Collectors.toSet());
    }

    /**
     * Fetch existing health insurance cards from database
     */
    private Set<String> fetchExistingHealthInsuranceCards(Set<String> values) {
        return values.isEmpty() ? new HashSet<>() :
                employeeRepository.findByHealthInsuranceCardIn(values).stream()
                        .map(Employee::getHealthInsuranceCard)
                        .collect(Collectors.toSet());
    }

    /**
     * Process both safe and final batches with transaction control for partial success
     */
    private void processBatchesWithTransactions(
            BulkOperationUtils.BatchClassification<EmployeeRequest> classification,
            List<EmployeeResponse> successResults,
            List<BulkOperationError> errors) {

        // Process SAFE BATCH - no conflicts, use single transaction
        if (classification.hasSafeBatch()) {
            log.info("Processing safe batch: {} requests", classification.getSafeBatch().size());
            processSafeBatchWithTransaction(classification.getSafeBatch(), successResults, errors);
        }

        // Process FINAL BATCH - có conflicts, each request in separate transaction (REQUIRES_NEW)
        if (classification.hasFinalBatch()) {
            log.warn("Processing final batch: {} requests with potential conflicts",
                    classification.getFinalBatch().size());
            processFinalBatchWithTransactions(classification.getFinalBatch(), successResults, errors, classification.getSafeBatch().size()); // classification.getSafeBatch().size() = start index for final batch
        }
    }

    /**
     * Build bulk operation result with timing and summary
     */
    private BulkOperationResult<EmployeeResponse> buildBulkOperationResult(
            int totalRequests,
            List<EmployeeResponse> successResults,
            List<BulkOperationError> errors,
            long startTime) {

        long duration = System.currentTimeMillis() - startTime;
        BulkOperationResult<EmployeeResponse> result = BulkOperationResultBuilder.build(
                totalRequests,
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
     * Sử dụng một transaction duy nhất cho toàn bộ batch để tối ưu performance
     * Nếu batch save fails, fallback sang per-record processing với separate transactions
     */
    private void processSafeBatchWithTransaction(
            List<EmployeeRequest> safeBatch,
            List<EmployeeResponse> successResults,
            List<BulkOperationError> errors) {

        // Create TransactionTemplate for this batch
        TransactionTemplate txTemplate = new TransactionTemplate(transactionManager);

        try {
            // Execute entire safe batch in one transaction
            txTemplate.executeWithoutResult(status -> {
                try {
                    // Prepare entities for batch save
                    List<Employee> entitiesToSave = prepareEntitiesForBatchSave(safeBatch, errors);

                    // Batch save and process results
                    if (!entitiesToSave.isEmpty()) {
                        processBatchSaveResults(entitiesToSave, safeBatch, successResults, errors);
                    }
                } catch (Exception e) {
                    log.error("Safe batch saveAll failed: {}", e.getMessage());
                    // Mark transaction for rollback
                    status.setRollbackOnly();
                    throw e;
                }
            });

        } catch (Exception e) {
            log.error("Safe batch processing failed, falling back to per-record processing: {}", e.getMessage());

            // Fallback: process each request individually with separate transactions
            fallbackToIndividualTransactions(safeBatch, successResults, errors, 0);
        }
    }

    /**
     * Prepare entities for batch save by mapping requests to entities
     */
    private List<Employee> prepareEntitiesForBatchSave(
            List<EmployeeRequest> requests,
            List<BulkOperationError> errors) {

        List<Employee> entitiesToSave = new ArrayList<>();

        for (int i = 0; i < requests.size(); i++) {
            EmployeeRequest request = requests.get(i);

            try {
                Employee entity = prepareEmployeeEntity(request);
                entitiesToSave.add(entity);

            } catch (Exception e) {
                log.error("Error preparing employee at index {}: {}", i, e.getMessage());
                errors.add(buildError(i, request, e.getMessage(), e));
            }
        }

        return entitiesToSave;
    }

    /**
     * Prepare single employee entity for create or update
     */
    private Employee prepareEmployeeEntity(EmployeeRequest request) {
        Employee existingEntity = findExistingEntityForUpsert(request);

        if (existingEntity != null) {
            // UPDATE: Use mapper to update existing entity
            employeeMapper.updateEmployee(existingEntity, request);
            employeeMapper.setReferences(existingEntity, request);
            return existingEntity;
        } else {
            // CREATE: Create new entity
            Employee entity = employeeMapper.toEmployee(request);
            employeeMapper.setReferences(entity, request);

            if (entity.getCreatedBy() == null) entity.setCreatedBy(1L);
            if (entity.getUpdatedBy() == null) entity.setUpdatedBy(1L);
            return entity;
        }
    }

    /**
     * Process results after batch save
     */
    private void processBatchSaveResults(
            List<Employee> entitiesToSave,
            List<EmployeeRequest> requests,
            List<EmployeeResponse> successResults,
            List<BulkOperationError> errors) {

        List<Employee> savedEntities = employeeRepository.saveAll(entitiesToSave);

        // Process child entities for each saved employee
        for (int i = 0; i < savedEntities.size(); i++) {
            Employee saved = savedEntities.get(i);
            EmployeeRequest request = requests.get(i);

            try {
                // Handle child entities AFTER parent is saved
                processChildEntities(saved, request);

                // Reload with EntityGraph to get all associations eagerly loaded
                Employee reloaded = employeeRepository.findWithAllAssociationsById(saved.getId())
                        .orElse(saved); // fallback to saved if reload fails
                successResults.add(employeeMapper.toEmployeeResponse(reloaded));

            } catch (Exception e) {
                log.error("Error processing child entities for employee {}: {}",
                        saved.getId(), e.getMessage());
                errors.add(buildError(i, request,
                        "Parent saved but child processing failed: " + e.getMessage(), e));
            }
        }
    }

    /**
     * Fallback to individual processing if batch fails
     * Each request runs in separate REQUIRES_NEW transaction for partial success
     */
    private void fallbackToIndividualTransactions(
            List<EmployeeRequest> requests,
            List<EmployeeResponse> successResults,
            List<BulkOperationError> errors,
            int startIndex) {

        for (int i = 0; i < requests.size(); i++) {
            int globalIndex = startIndex + i;
            processIndividualRequestWithTransaction(requests.get(i), globalIndex, successResults, errors);
        }
    }

    /**
     * Process final batch - có potential conflicts
     * Mỗi request chạy trong separate transaction (REQUIRES_NEW) để đảm bảo partial commits
     */
    private void processFinalBatchWithTransactions(
            List<EmployeeRequest> finalBatch,
            List<EmployeeResponse> successResults,
            List<BulkOperationError> errors,
            int startIndex) {

        // Process each request in its own transaction
        fallbackToIndividualTransactions(finalBatch, successResults, errors, startIndex);
    }

    /**
     * Process individual request in separate transaction (REQUIRES_NEW)
     * This ensures that failure in one request doesn't affect others - enabling partial success
     */
    private void processIndividualRequestWithTransaction(
            EmployeeRequest request,
            int globalIndex,
            List<EmployeeResponse> successResults,
            List<BulkOperationError> errors) {

        // Create TransactionTemplate with REQUIRES_NEW propagation
        TransactionTemplate txTemplate = new TransactionTemplate(transactionManager);
        txTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        try {
            // Execute this request in its own transaction
            EmployeeResponse response = txTemplate.execute(status -> {
                try {
                    // Find existing entity to determine create vs update
                    Employee existingEntity = findExistingEntityForUpsert(request);

                    EmployeeResponse resp;
                    if (existingEntity != null) {
                        // UPDATE: Call core update logic
                        resp = executeEmployeeUpdate(existingEntity.getId(), request);
                    } else {
                        // CREATE: Call core create logic
                        resp = executeEmployeeCreation(request);
                    }

                    // Flush to ensure DB operations are executed within this transaction
                    entityManager.flush();
                    return resp;

                } catch (Exception e) {
                    // Mark transaction for rollback
                    status.setRollbackOnly();
                    throw e;
                }
            });

            if (response != null) {
                successResults.add(response);
            }

            // Clear entityManager after each transaction to avoid stale entities
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
     * Core execution method for employee creation (can be called from @Transactional or within transaction)
     * This is the single source of truth for employee creation logic
     */
    private EmployeeResponse executeEmployeeCreation(EmployeeRequest request) {
        // Check source_id uniqueness for create
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            employeeRepository.findBySourceId(request.getSourceId()).ifPresent(e -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        Employee employee = employeeMapper.toEmployee(request);
        employeeMapper.setReferences(employee, request);

        if (employee.getCreatedBy() == null) employee.setCreatedBy(1L);
        if (employee.getUpdatedBy() == null) employee.setUpdatedBy(1L);

        Employee saved = employeeRepository.save(employee);

        // Delegate child creation to helpers
        saved.setEmployeeDecisionList(new HashSet<>(createDecisions(saved, request.getEmployeeDecisions())));
        saved.setEmployeeEducationList(new HashSet<>(createEducations(saved, request.getEmployeeEducations())));
        saved.setEmployeeAttendanceMachineList(new HashSet<>(createAttendanceMachines(saved, request.getEmployeeAttendanceMachines())));
        saved.setEmployeeWorkLocationList(new HashSet<>(createWorkLocations(saved, request.getEmployeeWorkLocations())));
        saved.setEmployeeWorkShift(createOrUpdateWorkShift(saved, request.getEmployeeWorkShift()));

        // Reload with EntityGraph to get all associations eagerly loaded
        Employee reloaded = employeeRepository.findWithAllAssociationsById(saved.getId())
                .orElse(saved); // fallback to saved if reload fails
        return employeeMapper.toEmployeeResponse(reloaded);
    }

    /**
     * Core execution method for employee update (can be called from @Transactional or within transaction)
     * This is the single source of truth for employee update logic
     */
    private EmployeeResponse executeEmployeeUpdate(Long id, EmployeeRequest request) {
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

        // Update employee fields
        employeeMapper.updateEmployee(employee, request);
        employeeMapper.setReferences(employee, request);

        Employee saved = employeeRepository.save(employee);

        // Replace child collections if provided
        if (request.getEmployeeDecisions() != null) replaceDecisions(saved, request.getEmployeeDecisions());
        if (request.getEmployeeEducations() != null) replaceEducations(saved, request.getEmployeeEducations());
        if (request.getEmployeeAttendanceMachines() != null)
            replaceAttendanceMachines(saved, request.getEmployeeAttendanceMachines());
        if (request.getEmployeeWorkLocations() != null) replaceWorkLocations(saved, request.getEmployeeWorkLocations());
        if (request.getEmployeeWorkShift() != null) {
            saved.setEmployeeWorkShift(createOrUpdateWorkShift(saved, request.getEmployeeWorkShift()));
        }

        // Reload with EntityGraph to get all associations eagerly loaded
        Employee reloaded = employeeRepository.findWithAllAssociationsById(saved.getId())
                .orElse(saved); // fallback to saved if reload fails
        return employeeMapper.toEmployeeResponse(reloaded);
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
     * Mỗi ID chạy trong separate transaction (REQUIRES_NEW) để đảm bảo partial success
     * <p>
     * NOTE: Không dùng @Transactional để đảm bảo partial commits.
     * Mỗi delete operation chạy trong transaction riêng qua TransactionTemplate.
     */
    public BulkOperationResult<Long> bulkDeleteEmployees(List<Long> ids) {
        log.info("Starting bulk delete for {} employee IDs", ids.size());
        long startTime = System.currentTimeMillis();

        List<Long> successResults = new ArrayList<>();
        List<BulkOperationError> errors = new ArrayList<>();

        // Create TransactionTemplate with REQUIRES_NEW propagation
        TransactionTemplate txTemplate = new TransactionTemplate(transactionManager);
        txTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        // Process each delete in separate transaction for partial success
        for (int i = 0; i < ids.size(); i++) {
            Long id = ids.get(i);

            try {
                // Execute delete in its own transaction
                txTemplate.executeWithoutResult(status -> {
                    try {
                        // Check constraints and delete
                        checkEmployeeForeignKeyConstraints(id);
                        deleteEmployee(id);

                        // Flush to ensure DB operations are executed within this transaction
                        entityManager.flush();

                    } catch (Exception e) {
                        // Mark transaction for rollback
                        status.setRollbackOnly();
                        throw e;
                    }
                });

                successResults.add(id);

                // Clear entityManager after each transaction to avoid stale entities
                entityManager.clear();

            } catch (NotFoundException e) {
                log.warn("Employee not found at index {}: {}", i, e.getMessage());
                errors.add(BulkOperationError.builder()
                        .index(i)
                        .identifier("id: " + id)
                        .errorMessage("Employee not found: " + e.getMessage())
                        .errorType(e.getClass().getSimpleName())
                        .build());
            } catch (Exception e) {
                log.error("Error deleting employee at index {}: {}", i, e.getMessage());
                errors.add(BulkOperationError.builder()
                        .index(i)
                        .identifier("id: " + id)
                        .errorMessage(e.getMessage())
                        .errorType(e.getClass().getSimpleName())
                        .build());
            }
        }

        // Build and return result
        long duration = System.currentTimeMillis() - startTime;
        BulkOperationResult<Long> result = BulkOperationResultBuilder.build(
                ids.size(),
                successResults,
                errors,
                duration
        );

        log.info("Bulk delete employees completed: {}/{} succeeded, {}/{} failed in {}ms",
                result.getSuccessCount(), result.getTotalRequests(),
                result.getFailedCount(), result.getTotalRequests(),
                duration);

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


