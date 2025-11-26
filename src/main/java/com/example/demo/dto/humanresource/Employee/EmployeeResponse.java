package com.example.demo.dto.humanresource.Employee;

import com.example.demo.dto.humanresource.EmployeeAttendanceMachine.EmployeeAttendanceMachineResponse;
import com.example.demo.dto.humanresource.EmployeeDecision.EmployeeDecisionResponse;
import com.example.demo.dto.humanresource.EmployeeEducation.EmployeeEducationResponse;
import com.example.demo.dto.humanresource.EmployeeWorkLocation.EmployeeWorkLocationResponse;
import com.example.demo.dto.humanresource.EmployeeWorkShift.EmployeeWorkShiftResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeResponse {
    Long id;
    String employeeCode;
    String sourceId;
    Long businessPartnerId;
    String corporationCode;
    String fullName;
    String alternateName;
    LocalDateTime birthDate;
    Long genderId;
    String genderName;
    Long maritalStatusId;
    String maritalStatusName;
    String idNumberCmnd;
    LocalDateTime idIssueDateCmnd;
    Long idIssuePlaceCmnd;
    String idIssuePlaceCmndName;
    String idNumberCccd;
    LocalDateTime idIssueDateCccd;
    Long idIssuePlaceCccd;
    String idIssuePlaceCccdName;
    Long nationalityId;
    String nationalityName;
    LocalDateTime startDate;
    LocalDateTime officialStartDate;
    LocalDateTime seniorityStartDate;
    Integer seniorityDeductionDays;
    Long laborStatusId;
    String laborStatusName;
    String taxCode;
    Long managerId;
    String managerName;
    Integer entitledLeaveDays;
    Long graduationSchoolId;
    String graduationSchoolName;
    Integer graduationYear;
    Long language1;
    String language1Name;
    Long language2;
    String language2Name;
    Long language3;
    String language3Name;
    Long bloodGroupId;
    String bloodGroupName;
    String healthStatus;
    String passportNumber;
    LocalDateTime passportExpiryDate;
    String workPermitNumber;
    LocalDateTime workPermitStartDate;
    LocalDateTime workPermitExpiryDate;
    String temporaryResidenceCard;
    LocalDateTime tempResidenceStartDate;
    LocalDateTime tempResidenceExpiryDate;
    Boolean noSalaryAdvance;
    Long sourceSystemId;
    String currentAddressStreet;
    Long currentAddressWard;
    String currentAddressWardName;
    String permanentAddressStreet;
    Long permanentAddressWard;
    String permanentAddressWardName;
    Long hometownId;
    String hometownName;
    Long placeOfBirthId;
    String placeOfBirthName;
    String personalPhone;
    String homePhone;
    String companyEmail;
    String personalEmail;
    String emergencyContactName;
    String emergencyContactPhone;
    String bankAccountNumber;
    Long bankId;
    String bankName;
    String bankBranch;
    Boolean taxDeclarationAuthorized;
    Boolean excludePersonalDeduction;
    LocalDateTime terminationDate;
    String socialInsuranceNo;
    String socialInsuranceCode;
    String healthInsuranceCard;
    Long medicalRegistration;
    String medicalRegistrationName;

    // child responses
    Set<EmployeeDecisionResponse> employeeDecisions;
    Set<EmployeeEducationResponse> employeeEducations;
    EmployeeWorkShiftResponse employeeWorkShift;
    Set<EmployeeAttendanceMachineResponse> employeeAttendanceMachines;
    Set<EmployeeWorkLocationResponse> employeeWorkLocations;
}
