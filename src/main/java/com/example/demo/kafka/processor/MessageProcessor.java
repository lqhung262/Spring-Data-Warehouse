package com.example.demo.kafka.processor;


import com.example.demo.dto.BulkOperationResult;
import com.example.demo.dto.humanresource.Department.DepartmentRequest;
import com.example.demo.dto.humanresource.Employee.EmployeeRequest;
import com.example.demo.dto.humanresource.EmployeeType.EmployeeTypeRequest;
import com.example.demo.dto.humanresource.JobRank.JobRankRequest;
import com.example.demo.dto.humanresource.JobTitle.JobTitleRequest;
import com.example.demo.dto.humanresource.Nationality.NationalityRequest;
import com.example.demo.dto.humanresource.OldDistrict.OldDistrictRequest;
import com.example.demo.dto.humanresource.OldProvinceCity.OldProvinceCityRequest;
import com.example.demo.dto.humanresource.OldWard.OldWardRequest;
import com.example.demo.dto.humanresource.OtType.OtTypeRequest;
import com.example.demo.dto.humanresource.School.SchoolRequest;
import com.example.demo.dto.humanresource.Ward.WardRequest;
import com.example.demo.dto.humanresource.WorkShift.WorkShiftRequest;
import com.example.demo.dto.humanresource.WorkShiftGroup.WorkShiftGroupRequest;
import com.example.demo.kafka.enums.MessageSpec;
import com.example.demo.kafka.exception.RetryableException;
import com.example.demo.kafka.model.KafkaMessage;
import com.example.demo.kafka.service.KafkaJobStatusService;
import com.example.demo.service.humanresource.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageProcessor {

    private final ObjectMapper objectMapper;
    private final KafkaJobStatusService jobStatusService;

    // All services...
    private final EmployeeService employeeService;
    private final DepartmentService departmentService;
    private final JobTitleService jobTitleService;
    private final JobRankService jobRankService;
    private final SchoolService schoolService;
    private final NationalityService nationalityService;
    private final EmployeeTypeService employeeTypeService;
    private final WorkShiftService workShiftService;
    private final WorkShiftGroupService workShiftGroupService;
    private final OtTypeService otTypeService;
    private final WardService wardService;
    private final OldWardService oldWardService;
    private final OldDistrictService oldDistrictService;
    private final OldProvinceCityService oldProvinceCityService;

    public void processMessage(KafkaMessage<?> kafkaMessage, MessageSpec messageSpec) {
        String jobId = kafkaMessage.getJobId();
        log.info("Processing message with spec: {}, jobId: {}, payload size: {}",
                messageSpec, jobId, kafkaMessage.getPayload().size());

        try {
            // Update status to PROCESSING
            jobStatusService.updateToProcessing(jobId);

            // Process based on message spec
            BulkOperationResult<?> result = switch (messageSpec) {
                case EMPLOYEE_UPSERT -> processEmployeeUpsert(kafkaMessage);
                case EMPLOYEE_DELETE -> processEmployeeDelete(kafkaMessage);
                case DEPARTMENT_UPSERT -> processDepartmentUpsert(kafkaMessage);
                case DEPARTMENT_DELETE -> processDepartmentDelete(kafkaMessage);
                case JOB_TITLE_UPSERT -> processJobTitleUpsert(kafkaMessage);
                case JOB_TITLE_DELETE -> processJobTitleDelete(kafkaMessage);
                case JOB_RANK_UPSERT -> processJobRankUpsert(kafkaMessage);
                case JOB_RANK_DELETE -> processJobRankDelete(kafkaMessage);
                case SCHOOL_UPSERT -> processSchoolUpsert(kafkaMessage);
                case SCHOOL_DELETE -> processSchoolDelete(kafkaMessage);
                case NATIONALITY_UPSERT -> processNationalityUpsert(kafkaMessage);
                case NATIONALITY_DELETE -> processNationalityDelete(kafkaMessage);
                case EMPLOYEE_TYPE_UPSERT -> processEmployeeTypeUpsert(kafkaMessage);
                case EMPLOYEE_TYPE_DELETE -> processEmployeeTypeDelete(kafkaMessage);
                case WORK_SHIFT_UPSERT -> processWorkShiftUpsert(kafkaMessage);
                case WORK_SHIFT_DELETE -> processWorkShiftDelete(kafkaMessage);
                case WORK_SHIFT_GROUP_UPSERT -> processWorkShiftGroupUpsert(kafkaMessage);
                case WORK_SHIFT_GROUP_DELETE -> processWorkShiftGroupDelete(kafkaMessage);
                case OT_TYPE_UPSERT -> processOtTypeUpsert(kafkaMessage);
                case OT_TYPE_DELETE -> processOtTypeDelete(kafkaMessage);
                case WARD_UPSERT -> processWardUpsert(kafkaMessage);
                case WARD_DELETE -> processWardDelete(kafkaMessage);
                case OLD_WARD_UPSERT -> processOldWardUpsert(kafkaMessage);
                case OLD_WARD_DELETE -> processOldWardDelete(kafkaMessage);
                case OLD_DISTRICT_UPSERT -> processOldDistrictUpsert(kafkaMessage);
                case OLD_DISTRICT_DELETE -> processOldDistrictDelete(kafkaMessage);
                case OLD_PROVINCE_CITY_UPSERT -> processOldProvinceCityUpsert(kafkaMessage);
                case OLD_PROVINCE_CITY_DELETE -> processOldProvinceCityDelete(kafkaMessage);
                default -> {
                    log.warn("Unknown message spec: {}", messageSpec);
                    yield null;
                }
            };

            // Update job result
            if (result != null) {
                jobStatusService.updateJobResult(jobId, result);
                log.info("Job completed:  jobId={}, success={}, failure={}",
                        jobId, result.getSuccessCount(), result.getFailedCount());
            }

        } catch (DataIntegrityViolationException e) {
            log.error("Database constraint violation - retryable for jobId: {}", jobId, e);
            throw new RetryableException("Database constraint violation", e);
        } catch (Exception e) {
            log.error("Error processing message for jobId: {}", jobId, e);
            jobStatusService.markAsFailed(jobId, e.getMessage());

            if (isRetryable(e)) {
                throw new RetryableException("Transient error occurred", e);
            }
            throw e;
        }
    }

    // ==================== DEPARTMENT ====================
    private BulkOperationResult<?> processDepartmentUpsert(KafkaMessage<?> kafkaMessage) {
        List<DepartmentRequest> requests = convertPayload(kafkaMessage.getPayload(), DepartmentRequest.class);
        return departmentService.bulkUpsertDepartments(requests);
    }

    private BulkOperationResult<?> processDepartmentDelete(KafkaMessage<?> kafkaMessage) {
        List<Long> ids = convertPayload(kafkaMessage.getPayload(), Long.class);
        return departmentService.bulkDeleteDepartments(ids);
    }

    // ==================== EMPLOYEE ====================
    private BulkOperationResult<?> processEmployeeUpsert(KafkaMessage<?> kafkaMessage) {
        List<EmployeeRequest> requests = convertPayload(kafkaMessage.getPayload(), EmployeeRequest.class);
        return employeeService.bulkUpsertEmployees(requests);
    }

    private BulkOperationResult<?> processEmployeeDelete(KafkaMessage<?> kafkaMessage) {
        List<Long> ids = convertPayload(kafkaMessage.getPayload(), Long.class);
        return employeeService.bulkDeleteEmployees(ids);
    }

    // ==================== JOBTITLE ====================
    private BulkOperationResult<?> processJobTitleUpsert(KafkaMessage<?> kafkaMessage) {
        List<JobTitleRequest> requests = convertPayload(kafkaMessage.getPayload(), JobTitleRequest.class);
        return jobTitleService.bulkUpsertJobTitles(requests);
    }

    private BulkOperationResult<?> processJobTitleDelete(KafkaMessage<?> kafkaMessage) {
        List<Long> ids = convertPayload(kafkaMessage.getPayload(), Long.class);
        return jobTitleService.bulkDeleteJobTitles(ids);
    }

    // ==================== JOBRANK ====================
    private BulkOperationResult<?> processJobRankUpsert(KafkaMessage<?> kafkaMessage) {
        List<JobRankRequest> requests = convertPayload(kafkaMessage.getPayload(), JobRankRequest.class);
        return jobRankService.bulkUpsertJobRanks(requests);
    }

    private BulkOperationResult<?> processJobRankDelete(KafkaMessage<?> kafkaMessage) {
        List<Long> ids = convertPayload(kafkaMessage.getPayload(), Long.class);
        return jobRankService.bulkDeleteJobRanks(ids);
    }

    // ==================== SCHOOL ====================
    private BulkOperationResult<?> processSchoolUpsert(KafkaMessage<?> kafkaMessage) {
        List<SchoolRequest> requests = convertPayload(kafkaMessage.getPayload(), SchoolRequest.class);
        return schoolService.bulkUpsertSchools(requests);
    }

    private BulkOperationResult<?> processSchoolDelete(KafkaMessage<?> kafkaMessage) {
        List<Long> ids = convertPayload(kafkaMessage.getPayload(), Long.class);
        return schoolService.bulkDeleteSchools(ids);
    }

    // ==================== NATIONALITY ====================
    private BulkOperationResult<?> processNationalityUpsert(KafkaMessage<?> kafkaMessage) {
        List<NationalityRequest> requests = convertPayload(kafkaMessage.getPayload(), NationalityRequest.class);
        return nationalityService.bulkUpsertNationalities(requests);
    }

    private BulkOperationResult<?> processNationalityDelete(KafkaMessage<?> kafkaMessage) {
        List<Long> ids = convertPayload(kafkaMessage.getPayload(), Long.class);
        return nationalityService.bulkDeleteNationalities(ids);
    }

    // ==================== EMPLOYEETYPE ====================
    private BulkOperationResult<?> processEmployeeTypeUpsert(KafkaMessage<?> kafkaMessage) {
        List<EmployeeTypeRequest> requests = convertPayload(kafkaMessage.getPayload(), EmployeeTypeRequest.class);
        return employeeTypeService.bulkUpsertEmployeeTypes(requests);
    }

    private BulkOperationResult<?> processEmployeeTypeDelete(KafkaMessage<?> kafkaMessage) {
        List<Long> ids = convertPayload(kafkaMessage.getPayload(), Long.class);
        return employeeTypeService.bulkDeleteEmployeeTypes(ids);
    }

    // ==================== WORKSHIFT ====================
    private BulkOperationResult<?> processWorkShiftUpsert(KafkaMessage<?> kafkaMessage) {
        List<WorkShiftRequest> requests = convertPayload(kafkaMessage.getPayload(), WorkShiftRequest.class);
        return workShiftService.bulkUpsertWorkShifts(requests);
    }

    private BulkOperationResult<?> processWorkShiftDelete(KafkaMessage<?> kafkaMessage) {
        List<Long> ids = convertPayload(kafkaMessage.getPayload(), Long.class);
        return workShiftService.bulkDeleteWorkShifts(ids);
    }

    // ==================== WORKSHIFTGROUP ====================
    private BulkOperationResult<?> processWorkShiftGroupUpsert(KafkaMessage<?> kafkaMessage) {
        List<WorkShiftGroupRequest> requests = convertPayload(kafkaMessage.getPayload(), WorkShiftGroupRequest.class);
        return workShiftGroupService.bulkUpsertWorkShiftGroups(requests);
    }

    private BulkOperationResult<?> processWorkShiftGroupDelete(KafkaMessage<?> kafkaMessage) {
        List<Long> ids = convertPayload(kafkaMessage.getPayload(), Long.class);
        return workShiftGroupService.bulkDeleteWorkShiftGroups(ids);
    }

    // ==================== OTTYPE ====================
    private BulkOperationResult<?> processOtTypeUpsert(KafkaMessage<?> kafkaMessage) {
        List<OtTypeRequest> requests = convertPayload(kafkaMessage.getPayload(), OtTypeRequest.class);
        return otTypeService.bulkUpsertOtTypes(requests);
    }

    private BulkOperationResult<?> processOtTypeDelete(KafkaMessage<?> kafkaMessage) {
        List<Long> ids = convertPayload(kafkaMessage.getPayload(), Long.class);
        return otTypeService.bulkDeleteOtTypes(ids);
    }

    // ==================== WARD ====================
    private BulkOperationResult<?> processWardUpsert(KafkaMessage<?> kafkaMessage) {
        List<WardRequest> requests = convertPayload(kafkaMessage.getPayload(), WardRequest.class);
        return wardService.bulkUpsertWards(requests);
    }

    private BulkOperationResult<?> processWardDelete(KafkaMessage<?> kafkaMessage) {
        List<Long> ids = convertPayload(kafkaMessage.getPayload(), Long.class);
        return wardService.bulkDeleteWards(ids);
    }

    // ==================== OLDWARD ====================
    private BulkOperationResult<?> processOldWardUpsert(KafkaMessage<?> kafkaMessage) {
        List<OldWardRequest> requests = convertPayload(kafkaMessage.getPayload(), OldWardRequest.class);
        return oldWardService.bulkUpsertOldWards(requests);
    }

    private BulkOperationResult<?> processOldWardDelete(KafkaMessage<?> kafkaMessage) {
        List<Long> ids = convertPayload(kafkaMessage.getPayload(), Long.class);
        return oldWardService.bulkDeleteOldWards(ids);
    }

    // ==================== OLDDISTRICT ====================
    private BulkOperationResult<?> processOldDistrictUpsert(KafkaMessage<?> kafkaMessage) {
        List<OldDistrictRequest> requests = convertPayload(kafkaMessage.getPayload(), OldDistrictRequest.class);
        return oldDistrictService.bulkUpsertOldDistricts(requests);
    }

    private BulkOperationResult<?> processOldDistrictDelete(KafkaMessage<?> kafkaMessage) {
        List<Long> ids = convertPayload(kafkaMessage.getPayload(), Long.class);
        return oldDistrictService.bulkDeleteOldDistricts(ids);
    }

    // ==================== OLDPROVINCECITY ====================
    private BulkOperationResult<?> processOldProvinceCityUpsert(KafkaMessage<?> kafkaMessage) {
        List<OldProvinceCityRequest> requests = convertPayload(kafkaMessage.getPayload(), OldProvinceCityRequest.class);
        return oldProvinceCityService.bulkUpsertOldProvinceCities(requests);
    }

    private BulkOperationResult<?> processOldProvinceCityDelete(KafkaMessage<?> kafkaMessage) {
        List<Long> ids = convertPayload(kafkaMessage.getPayload(), Long.class);
        return oldProvinceCityService.bulkDeleteOldProvinceCities(ids);
    }

    // ==================== HELPER METHODS ====================
    private <T> List<T> convertPayload(List<?> payload, Class<T> targetClass) {
        return payload.stream()
                .map(item -> objectMapper.convertValue(item, targetClass))
                .toList();
    }

    private boolean isRetryable(Exception e) {
        return e instanceof DataIntegrityViolationException
                || e.getCause() instanceof java.net.SocketTimeoutException
                || e.getCause() instanceof java.sql.SQLTransientException;
    }
}