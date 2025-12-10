package com.example.demo.kafka.processor;

import com.example.demo.dto.BulkOperationResult;
import com.example.demo.dto.humanresource.AttendanceMachine.AttendanceMachineRequest;
import com.example.demo.dto.humanresource.AttendanceType.AttendanceTypeRequest;
import com.example.demo.dto.humanresource.Bank.BankRequest;
import com.example.demo.dto.humanresource.BloodGroup.BloodGroupRequest;
import com.example.demo.dto.humanresource.Department.DepartmentRequest;
import com.example.demo.dto.humanresource.EducationLevel.EducationLevelRequest;
import com.example.demo.dto.humanresource.Employee.EmployeeRequest;
import com.example.demo.dto.humanresource.EmployeeType.EmployeeTypeRequest;
import com.example.demo.dto.humanresource.ExpenseType.ExpenseTypeRequest;
import com.example.demo.dto.humanresource.IdentityIssuingAuthority.IdentityIssuingAuthorityRequest;
import com.example.demo.dto.humanresource.JobPosition.JobPositionRequest;
import com.example.demo.dto.humanresource.JobRank.JobRankRequest;
import com.example.demo.dto.humanresource.JobTitle.JobTitleRequest;
import com.example.demo.dto.humanresource.LaborStatus.LaborStatusRequest;
import com.example.demo.dto.humanresource.Language.LanguageRequest;
import com.example.demo.dto.humanresource.Major.MajorRequest;
import com.example.demo.dto.humanresource.MaritalStatus.MaritalStatusRequest;
import com.example.demo.dto.humanresource.MedicalFacility.MedicalFacilityRequest;
import com.example.demo.dto.humanresource.Nationality.NationalityRequest;
import com.example.demo.dto.humanresource.OldDistrict.OldDistrictRequest;
import com.example.demo.dto.humanresource.OldProvinceCity.OldProvinceCityRequest;
import com.example.demo.dto.humanresource.OldWard.OldWardRequest;
import com.example.demo.dto.humanresource.OtType.OtTypeRequest;
import com.example.demo.dto.humanresource.ProvinceCity.ProvinceCityRequest;
import com.example.demo.dto.humanresource.School.SchoolRequest;
import com.example.demo.dto.humanresource.Specialization.SpecializationRequest;
import com.example.demo.dto.humanresource.Ward.WardRequest;
import com.example.demo.dto.humanresource.WorkLocation.WorkLocationRequest;
import com.example.demo.dto.humanresource.WorkShift.WorkShiftRequest;
import com.example.demo.dto.humanresource.WorkShiftGroup.WorkShiftGroupRequest;
import com.example.demo.kafka.enums.MessageSpec;
import com.example.demo.service.humanresource.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class BulkOperationHandlerRegistry {

    // All services
    private final EmployeeService employeeService;
    private final DepartmentService departmentService;
    private final JobTitleService jobTitleService;
    private final JobRankService jobRankService;
    private final JobPositionService jobPositionService;
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
    private final LanguageService languageService;
    private final MaritalStatusService maritalStatusService;
    private final BankService bankService;
    private final MajorService majorService;
    private final SpecializationService specializationService;
    private final EducationLevelService educationLevelService;
    private final ProvinceCityService provinceCityService;
    private final IdentityIssuingAuthorityService identityIssuingAuthorityService;
    private final AttendanceMachineService attendanceMachineService;
    private final AttendanceTypeService attendanceTypeService;
    private final MedicalFacilityService medicalFacilityService;
    private final BloodGroupService bloodGroupService;
    private final LaborStatusService laborStatusService;
    private final WorkLocationService workLocationService;
    private final ExpenseTypeService expenseTypeService;

    private final Map<MessageSpec, BulkOperationHandlerConfig<?>> handlerMap = new HashMap<>();

    public void init() {
        // Employee
        registerHandler(MessageSpec.EMPLOYEE_UPSERT, MessageSpec.EMPLOYEE_DELETE,
                EmployeeRequest.class, employeeService::bulkUpsertEmployees, employeeService::bulkDeleteEmployees);

        // Department
        registerHandler(MessageSpec.DEPARTMENT_UPSERT, MessageSpec.DEPARTMENT_DELETE,
                DepartmentRequest.class, departmentService::bulkUpsertDepartments, departmentService::bulkDeleteDepartments);

        // Job Title
        registerHandler(MessageSpec.JOB_TITLE_UPSERT, MessageSpec.JOB_TITLE_DELETE,
                JobTitleRequest.class, jobTitleService::bulkUpsertJobTitles, jobTitleService::bulkDeleteJobTitles);

        // Job Rank
        registerHandler(MessageSpec.JOB_RANK_UPSERT, MessageSpec.JOB_RANK_DELETE,
                JobRankRequest.class, jobRankService::bulkUpsertJobRanks, jobRankService::bulkDeleteJobRanks);

        // Job Position
        registerHandler(MessageSpec.JOB_POSITION_UPSERT, MessageSpec.JOB_POSITION_DELETE,
                JobPositionRequest.class, jobPositionService::bulkUpsertJobPositions, jobPositionService::bulkDeleteJobPositions);

        // School
        registerHandler(MessageSpec.SCHOOL_UPSERT, MessageSpec.SCHOOL_DELETE,
                SchoolRequest.class, schoolService::bulkUpsertSchools, schoolService::bulkDeleteSchools);

        // Nationality
        registerHandler(MessageSpec.NATIONALITY_UPSERT, MessageSpec.NATIONALITY_DELETE,
                NationalityRequest.class, nationalityService::bulkUpsertNationalities, nationalityService::bulkDeleteNationalities);

        // Employee Type
        registerHandler(MessageSpec.EMPLOYEE_TYPE_UPSERT, MessageSpec.EMPLOYEE_TYPE_DELETE,
                EmployeeTypeRequest.class, employeeTypeService::bulkUpsertEmployeeTypes, employeeTypeService::bulkDeleteEmployeeTypes);

        // Work Shift
        registerHandler(MessageSpec.WORK_SHIFT_UPSERT, MessageSpec.WORK_SHIFT_DELETE,
                WorkShiftRequest.class, workShiftService::bulkUpsertWorkShifts, workShiftService::bulkDeleteWorkShifts);

        // Work Shift Group
        registerHandler(MessageSpec.WORK_SHIFT_GROUP_UPSERT, MessageSpec.WORK_SHIFT_GROUP_DELETE,
                WorkShiftGroupRequest.class, workShiftGroupService::bulkUpsertWorkShiftGroups, workShiftGroupService::bulkDeleteWorkShiftGroups);

        // OT Type
        registerHandler(MessageSpec.OT_TYPE_UPSERT, MessageSpec.OT_TYPE_DELETE,
                OtTypeRequest.class, otTypeService::bulkUpsertOtTypes, otTypeService::bulkDeleteOtTypes);

        // Ward
        registerHandler(MessageSpec.WARD_UPSERT, MessageSpec.WARD_DELETE,
                WardRequest.class, wardService::bulkUpsertWards, wardService::bulkDeleteWards);

        // Old Ward
        registerHandler(MessageSpec.OLD_WARD_UPSERT, MessageSpec.OLD_WARD_DELETE,
                OldWardRequest.class, oldWardService::bulkUpsertOldWards, oldWardService::bulkDeleteOldWards);

        // Old District
        registerHandler(MessageSpec.OLD_DISTRICT_UPSERT, MessageSpec.OLD_DISTRICT_DELETE,
                OldDistrictRequest.class, oldDistrictService::bulkUpsertOldDistricts, oldDistrictService::bulkDeleteOldDistricts);

        // Old Province City
        registerHandler(MessageSpec.OLD_PROVINCE_CITY_UPSERT, MessageSpec.OLD_PROVINCE_CITY_DELETE,
                OldProvinceCityRequest.class, oldProvinceCityService::bulkUpsertOldProvinceCities, oldProvinceCityService::bulkDeleteOldProvinceCities);

        // Language
        registerHandler(MessageSpec.LANGUAGE_UPSERT, MessageSpec.LANGUAGE_DELETE,
                LanguageRequest.class, languageService::bulkUpsertLanguages, languageService::bulkDeleteLanguages);

        // Marital Status
        registerHandler(MessageSpec.MARITAL_STATUS_UPSERT, MessageSpec.MARITAL_STATUS_DELETE,
                MaritalStatusRequest.class, maritalStatusService::bulkUpsertMaritalStatuses, maritalStatusService::bulkDeleteMaritalStatuses);

        // Bank
        registerHandler(MessageSpec.BANK_UPSERT, MessageSpec.BANK_DELETE,
                BankRequest.class, bankService::bulkUpsertBanks, bankService::bulkDeleteBanks);

        // Major
        registerHandler(MessageSpec.MAJOR_UPSERT, MessageSpec.MAJOR_DELETE,
                MajorRequest.class, majorService::bulkUpsertMajors, majorService::bulkDeleteMajors);

        // Specialization
        registerHandler(MessageSpec.SPECIALIZATION_UPSERT, MessageSpec.SPECIALIZATION_DELETE,
                SpecializationRequest.class, specializationService::bulkUpsertSpecializations, specializationService::bulkDeleteSpecializations);

        // Education Level
        registerHandler(MessageSpec.EDUCATION_LEVEL_UPSERT, MessageSpec.EDUCATION_LEVEL_DELETE,
                EducationLevelRequest.class, educationLevelService::bulkUpsertEducationLevels, educationLevelService::bulkDeleteEducationLevels);

        // Province City
        registerHandler(MessageSpec.PROVINCE_CITY_UPSERT, MessageSpec.PROVINCE_CITY_DELETE,
                ProvinceCityRequest.class, provinceCityService::bulkUpsertProvinceCities, provinceCityService::bulkDeleteProvinceCities);

        // Identity Issuing Authority
        registerHandler(MessageSpec.IDENTITY_ISSUING_AUTHORITY_UPSERT, MessageSpec.IDENTITY_ISSUING_AUTHORITY_DELETE,
                IdentityIssuingAuthorityRequest.class, identityIssuingAuthorityService::bulkUpsertIdentityIssuingAuthorities, identityIssuingAuthorityService::bulkDeleteIdentityIssuingAuthorities);

        // Attendance Machine
        registerHandler(MessageSpec.ATTENDANCE_MACHINE_UPSERT, MessageSpec.ATTENDANCE_MACHINE_DELETE,
                AttendanceMachineRequest.class, attendanceMachineService::bulkUpsertAttendanceMachines, attendanceMachineService::bulkDeleteAttendanceMachines);

        // Attendance Type
        registerHandler(MessageSpec.ATTENDANCE_TYPE_UPSERT, MessageSpec.ATTENDANCE_TYPE_DELETE,
                AttendanceTypeRequest.class, attendanceTypeService::bulkUpsertAttendanceTypes, attendanceTypeService::bulkDeleteAttendanceTypes);

        // Medical Facility
        registerHandler(MessageSpec.MEDICAL_FACILITY_UPSERT, MessageSpec.MEDICAL_FACILITY_DELETE,
                MedicalFacilityRequest.class, medicalFacilityService::bulkUpsertMedicalFacilities, medicalFacilityService::bulkDeleteMedicalFacilities);

        // Blood Group
        registerHandler(MessageSpec.BLOOD_GROUP_UPSERT, MessageSpec.BLOOD_GROUP_DELETE,
                BloodGroupRequest.class, bloodGroupService::bulkUpsertBloodGroups, bloodGroupService::bulkDeleteBloodGroups);

        // Labor Status
        registerHandler(MessageSpec.LABOR_STATUS_UPSERT, MessageSpec.LABOR_STATUS_DELETE,
                LaborStatusRequest.class, laborStatusService::bulkUpsertLaborStatuses, laborStatusService::bulkDeleteLaborStatuses);

        // Work Location
        registerHandler(MessageSpec.WORK_LOCATION_UPSERT, MessageSpec.WORK_LOCATION_DELETE,
                WorkLocationRequest.class, workLocationService::bulkUpsertWorkLocations, workLocationService::bulkDeleteWorkLocations);

        // Expense Type
        registerHandler(MessageSpec.EXPENSE_TYPE_UPSERT, MessageSpec.EXPENSE_TYPE_DELETE,
                ExpenseTypeRequest.class, expenseTypeService::bulkUpsertExpenseTypes, expenseTypeService::bulkDeleteExpenseTypes);
    }

    private <T> void registerHandler(MessageSpec upsertSpec, MessageSpec deleteSpec,
                                     Class<T> requestClass,
                                     Function<List<T>, BulkOperationResult<?>> upsertFunction,
                                     Function<List<Long>, BulkOperationResult<?>> deleteFunction) {
        BulkOperationHandlerConfig<T> config = new
                BulkOperationHandlerConfig<>(requestClass, upsertFunction, deleteFunction);
        handlerMap.put(upsertSpec, config);
        handlerMap.put(deleteSpec, config);
    }

    public BulkOperationHandlerConfig<?> getHandler(MessageSpec messageSpec) {
        return handlerMap.get(messageSpec);
    }

    public boolean isUpsertOperation(MessageSpec messageSpec) {
        return messageSpec.getValue().endsWith("_UPSERT");
    }

    public boolean isDeleteOperation(MessageSpec messageSpec) {
        return messageSpec.getValue().endsWith("_DELETE");
    }
}

