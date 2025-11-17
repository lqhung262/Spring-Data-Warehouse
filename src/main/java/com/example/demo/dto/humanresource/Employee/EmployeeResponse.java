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
    Long maritalStatusId;
    String idNumberCmnd;
    LocalDateTime idIssueDateCmnd;
    Long idIssuePlaceCmnd; // FK -> identity_issuing_authority
    String idNumberCccd;
    LocalDateTime idIssueDateCccd;
    Long idIssuePlaceCccd;
    Long nationalityId;
    LocalDateTime startDate;
    LocalDateTime officialStartDate;
    LocalDateTime seniorityStartDate;
    Integer seniorityDeductionDays;
    Long laborStatusId;
    String taxCode;
    Long managerId;
    Integer entitledLeaveDays;
    Long graduationSchoolId;
    Integer graduationYear;
    Long language1;
    Long language2;
    Long language3;
    Long bloodGroupId;
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
    String permanentAddressStreet;
    Long permanentAddressWard;
    Long hometownId;
    Long placeOfBirthId;
    String personalPhone;
    String homePhone;
    String companyEmail;
    String personalEmail;
    String emergencyContactName;
    String emergencyContactPhone;
    String bankAccountNumber;
    Long bankId;
    String bankBranch;
    Boolean taxDeclarationAuthorized;
    Boolean excludePersonalDeduction;
    LocalDateTime terminationDate;
    String socialInsuranceNo;
    String socialInsuranceCode;
    String healthInsuranceCard;
    Long medicalRegistration;

    // child responses
    Set<EmployeeDecisionResponse> employeeDecisions;
    Set<EmployeeEducationResponse> employeeEducations;
    EmployeeWorkShiftResponse employeeWorkShift;
    Set<EmployeeAttendanceMachineResponse> employeeAttendanceMachines;
    Set<EmployeeWorkLocationResponse> employeeWorkLocations;
}
