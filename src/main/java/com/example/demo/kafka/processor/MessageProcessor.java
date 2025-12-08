package com.example.demo.kafka.processor;

import com.example.demo.dto.request.humanresource.*;
import com.example.demo.kafka.enums.MessageSpec;
import com.example.demo.kafka.exception.RetryableException;
import com.example.demo.kafka.model.KafkaMessage;
import com.example.demo.service.humanresource.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageProcessor {

    private final ObjectMapper objectMapper;

    // Inject all services
    private final EmployeeService employeeService;
    private final DepartmentService departmentService;
    private final JobTitleService jobTitleService;
    private final JobRankService jobRankService;
    private final SchoolService schoolService;
    private final NationalityService nationalityService;
    private final WardService wardService;
    private final OldWardService oldWardService;
    private final OldDistrictService oldDistrictService;
    private final OldProvinceCityService oldProvinceCityService;
    private final WorkShiftService workShiftService;
    private final WorkShiftGroupService workShiftGroupService;
    private final OtTypeService otTypeService;
    private final EmployeeTypeService employeeTypeService;

    public void processMessage(KafkaMessage<?> kafkaMessage, MessageSpec messageSpec) {
        log.info("Processing message with spec: {}, payload size: {}",
                messageSpec, kafkaMessage.getPayload().size());

        try {
            switch (messageSpec) {
                // Employee operations
                case EMPLOYEE_UPSERT:
                    processEmployeeUpsert(kafkaMessage);
                    break;
                case EMPLOYEE_DELETE:
                    processEmployeeDelete(kafkaMessage);
                    break;

                // Department operations
                case DEPARTMENT_UPSERT:
                    processDepartmentUpsert(kafkaMessage);
                    break;
                case DEPARTMENT_DELETE:
                    processDepartmentDelete(kafkaMessage);
                    break;

                // JobTitle operations
                case JOBTITLE_UPSERT:
                    processJobTitleUpsert(kafkaMessage);
                    break;
                case JOBTITLE_DELETE:
                    processJobTitleDelete(kafkaMessage);
                    break;

                // JobRank operations
                case JOBRANK_UPSERT:
                    processJobRankUpsert(kafkaMessage);
                    break;
                case JOBRANK_DELETE:
                    processJobRankDelete(kafkaMessage);
                    break;

                // School operations
                case SCHOOL_UPSERT:
                    processSchoolUpsert(kafkaMessage);
                    break;
                case SCHOOL_DELETE:
                    processSchoolDelete(kafkaMessage);
                    break;

                // Nationality operations
                case NATIONALITY_UPSERT:
                    processNationalityUpsert(kafkaMessage);
                    break;
                case NATIONALITY_DELETE:
                    processNationalityDelete(kafkaMessage);
                    break;

                // Ward operations
                case WARD_UPSERT:
                    processWardUpsert(kafkaMessage);
                    break;
                case WARD_DELETE:
                    processWardDelete(kafkaMessage);
                    break;

                // OldWard operations
                case OLDWARD_UPSERT:
                    processOldWardUpsert(kafkaMessage);
                    break;
                case OLDWARD_DELETE:
                    processOldWardDelete(kafkaMessage);
                    break;

                // OldDistrict operations
                case OLDDISTRICT_UPSERT:
                    processOldDistrictUpsert(kafkaMessage);
                    break;
                case OLDDISTRICT_DELETE:
                    processOldDistrictDelete(kafkaMessage);
                    break;

                // OldProvinceCity operations
                case OLDPROVINCECITY_UPSERT:
                    processOldProvinceCityUpsert(kafkaMessage);
                    break;
                case OLDPROVINCECITY_DELETE:
                    processOldProvinceCityDelete(kafkaMessage);
                    break;

                // WorkShift operations
                case WORKSHIFT_UPSERT:
                    processWorkShiftUpsert(kafkaMessage);
                    break;
                case WORKSHIFT_DELETE:
                    processWorkShiftDelete(kafkaMessage);
                    break;

                // WorkShiftGroup operations
                case WORKSHIFTGROUP_UPSERT:
                    processWorkShiftGroupUpsert(kafkaMessage);
                    break;
                case WORKSHIFTGROUP_DELETE:
                    processWorkShiftGroupDelete(kafkaMessage);
                    break;

                // OtType operations
                case OTTYPE_UPSERT:
                    processOtTypeUpsert(kafkaMessage);
                    break;
                case OTTYPE_DELETE:
                    processOtTypeDelete(kafkaMessage);
                    break;

                // EmployeeType operations
                case EMPLOYEETYPE_UPSERT:
                    processEmployeeTypeUpsert(kafkaMessage);
                    break;
                case EMPLOYEETYPE_DELETE:
                    processEmployeeTypeDelete(kafkaMessage);
                    break;

                default:
                    log.warn("Unknown message spec: {}", messageSpec);
            }
        } catch (DataIntegrityViolationException e) {
            log.error("Database constraint violation - retryable", e);
            throw new RetryableException("Database constraint violation", e);
        } catch (Exception e) {
            log.error("Error processing message", e);
            // Determine if exception should be retried
            if (isRetryable(e)) {
                throw new RetryableException("Transient error occurred", e);
            }
            throw e;
        }
    }

    // ==================== EMPLOYEE ====================

    private void processEmployeeUpsert(KafkaMessage<?> kafkaMessage) {
        List<EmployeeRequest> requests = convertPayload(kafkaMessage.getPayload(), EmployeeRequest.class);
        employeeService.bulkUpsertEmployees(requests);
    }

    private void processEmployeeDelete(KafkaMessage<?> kafkaMessage) {
        List<Long> ids = convertPayload(kafkaMessage.getPayload(), Long.class);
        employeeService.bulkDeleteEmployees(ids);
    }

    // ==================== DEPARTMENT ====================

    private void processDepartmentUpsert(KafkaMessage<?> kafkaMessage) {
        List<DepartmentRequest> requests = convertPayload(kafkaMessage.getPayload(), DepartmentRequest.class);
        departmentService.bulkUpsertDepartments(requests);
    }

    private void processDepartmentDelete(KafkaMessage<?> kafkaMessage) {
        List<Long> ids = convertPayload(kafkaMessage.getPayload(), Long.class);
        departmentService.bulkDeleteDepartments(ids);
    }

    // ==================== JOBTITLE ====================

    private void processJobTitleUpsert(KafkaMessage<?> kafkaMessage) {
        List<JobTitleRequest> requests = convertPayload(kafkaMessage.getPayload(), JobTitleRequest.class);
        jobTitleService.bulkUpsertJobTitles(requests);
    }

    private void processJobTitleDelete(KafkaMessage<?> kafkaMessage) {
        List<Long> ids = convertPayload(kafkaMessage.getPayload(), Long.class);
        jobTitleService.bulkDeleteJobTitles(ids);
    }

    // ==================== JOBRANK ====================

    private void processJobRankUpsert(KafkaMessage<?> kafkaMessage) {
        List<JobRankRequest> requests = convertPayload(kafkaMessage.getPayload(), JobRankRequest.class);
        jobRankService.bulkUpsertJobRanks(requests);
    }

    private void processJobRankDelete(KafkaMessage<?> kafkaMessage) {
        List<Long> ids = convertPayload(kafkaMessage.getPayload(), Long.class);
        jobRankService.bulkDeleteJobRanks(ids);
    }

    // ==================== SCHOOL ====================

    private void processSchoolUpsert(KafkaMessage<?> kafkaMessage) {
        List<SchoolRequest> requests = convertPayload(kafkaMessage.getPayload(), SchoolRequest.class);
        schoolService.bulkUpsertSchools(requests);
    }

    private void processSchoolDelete(KafkaMessage<?> kafkaMessage) {
        List<Long> ids = convertPayload(kafkaMessage.getPayload(), Long.class);
        schoolService.bulkDeleteSchools(ids);
    }

    // ==================== NATIONALITY ====================

    private void processNationalityUpsert(KafkaMessage<?> kafkaMessage) {
        List<NationalityRequest> requests = convertPayload(kafkaMessage.getPayload(), NationalityRequest.class);
        nationalityService.bulkUpsertNationalities(requests);
    }

    private void processNationalityDelete(KafkaMessage<?> kafkaMessage) {
        List<Long> ids = convertPayload(kafkaMessage.getPayload(), Long.class);
        nationalityService.bulkDeleteNationalities(ids);
    }

    // ==================== WARD ====================

    private void processWardUpsert(KafkaMessage<?> kafkaMessage) {
        List<WardRequest> requests = convertPayload(kafkaMessage.getPayload(), WardRequest.class);
        wardService.bulkUpsertWards(requests);
    }

    private void processWardDelete(KafkaMessage<?> kafkaMessage) {
        List<Long> ids = convertPayload(kafkaMessage.getPayload(), Long.class);
        wardService.bulkDeleteWards(ids);
    }

    // ==================== OLDWARD ====================

    private void processOldWardUpsert(KafkaMessage<?> kafkaMessage) {
        List<OldWardRequest> requests = convertPayload(kafkaMessage.getPayload(), OldWardRequest.class);
        oldWardService.bulkUpsertOldWards(requests);
    }

    private void processOldWardDelete(KafkaMessage<?> kafkaMessage) {
        List<Long> ids = convertPayload(kafkaMessage.getPayload(), Long.class);
        oldWardService.bulkDeleteOldWards(ids);
    }

    // ==================== OLDDISTRICT ====================

    private void processOldDistrictUpsert(KafkaMessage<?> kafkaMessage) {
        List<OldDistrictRequest> requests = convertPayload(kafkaMessage.getPayload(), OldDistrictRequest.class);
        oldDistrictService.bulkUpsertOldDistricts(requests);
    }

    private void processOldDistrictDelete(KafkaMessage<?> kafkaMessage) {
        List<Long> ids = convertPayload(kafkaMessage.getPayload(), Long.class);
        oldDistrictService.bulkDeleteOldDistricts(ids);
    }

    // ==================== OLDPROVINCECITY ====================

    private void processOldProvinceCityUpsert(KafkaMessage<?> kafkaMessage) {
        List<OldProvinceCityRequest> requests = convertPayload(kafkaMessage.getPayload(), OldProvinceCityRequest.class);
        oldProvinceCityService.bulkUpsertOldProvinceCities(requests);
    }

    private void processOldProvinceCityDelete(KafkaMessage<?> kafkaMessage) {
        List<Long> ids = convertPayload(kafkaMessage.getPayload(), Long.class);
        oldProvinceCityService.bulkDeleteOldProvinceCities(ids);
    }

    // ==================== WORKSHIFT ====================

    private void processWorkShiftUpsert(KafkaMessage<?> kafkaMessage) {
        List<WorkShiftRequest> requests = convertPayload(kafkaMessage.getPayload(), WorkShiftRequest.class);
        workShiftService.bulkUpsertWorkShifts(requests);
    }

    private void processWorkShiftDelete(KafkaMessage<?> kafkaMessage) {
        List<Long> ids = convertPayload(kafkaMessage.getPayload(), Long.class);
        workShiftService.bulkDeleteWorkShifts(ids);
    }

    // ==================== WORKSHIFTGROUP ====================

    private void processWorkShiftGroupUpsert(KafkaMessage<?> kafkaMessage) {
        List<WorkShiftGroupRequest> requests = convertPayload(kafkaMessage.getPayload(), WorkShiftGroupRequest.class);
        workShiftGroupService.bulkUpsertWorkShiftGroups(requests);
    }

    private void processWorkShiftGroupDelete(KafkaMessage<?> kafkaMessage) {
        List<Long> ids = convertPayload(kafkaMessage.getPayload(), Long.class);
        workShiftGroupService.bulkDeleteWorkShiftGroups(ids);
    }

    // ==================== OTTYPE ====================

    private void processOtTypeUpsert(KafkaMessage<?> kafkaMessage) {
        List<OtTypeRequest> requests = convertPayload(kafkaMessage.getPayload(), OtTypeRequest.class);
        otTypeService.bulkUpsertOtTypes(requests);
    }

    private void processOtTypeDelete(KafkaMessage<?> kafkaMessage) {
        List<Long> ids = convertPayload(kafkaMessage.getPayload(), Long.class);
        otTypeService.bulkDeleteOtTypes(ids);
    }

    // ==================== EMPLOYEETYPE ====================

    private void processEmployeeTypeUpsert(KafkaMessage<?> kafkaMessage) {
        List<EmployeeTypeRequest> requests = convertPayload(kafkaMessage.getPayload(), EmployeeTypeRequest.class);
        employeeTypeService.bulkUpsertEmployeeTypes(requests);
    }

    private void processEmployeeTypeDelete(KafkaMessage<?> kafkaMessage) {
        List<Long> ids = convertPayload(kafkaMessage.getPayload(), Long.class);
        employeeTypeService.bulkDeleteEmployeeTypes(ids);
    }

    // ==================== HELPER METHODS ====================

    private <T> List<T> convertPayload(List<?> payload, Class<T> targetClass) {
        return payload.stream()
                .map(item -> objectMapper.convertValue(item, targetClass))
                .collect(Collectors.toList());
    }

    private boolean isRetryable(Exception e) {
        // Define which exceptions should trigger retry
        return e instanceof DataIntegrityViolationException
                || e.getCause() instanceof java.net.SocketTimeoutException
                || e.getCause() instanceof java.sql.SQLTransientException;
    }
}