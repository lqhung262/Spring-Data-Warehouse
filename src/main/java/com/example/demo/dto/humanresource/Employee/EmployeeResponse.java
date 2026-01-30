package com.example.demo.dto.humanresource.Employee;

import com.example.demo.dto.common.RefDto;
import com.example.demo.dto.humanresource.EmployeeAttendanceMachine.EmployeeAttendanceMachineResponse;
import com.example.demo.dto.humanresource.EmployeeDecision.EmployeeDecisionResponse;
import com.example.demo.dto.humanresource.EmployeeEducation.EmployeeEducationResponse;
import com.example.demo.dto.humanresource.EmployeeWorkLocation.EmployeeWorkLocationResponse;
import com.example.demo.dto.humanresource.EmployeeWorkShift.EmployeeWorkShiftResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Response DTO for Employee.
 * 
 * Uses RefDto for related entities to provide clean, structured data.
 * Each RefDto contains only id and name - the essential information.
 * 
 * Excludes internal fields (sourceId, sourceSystemId, audit fields)
 * that are not relevant for API consumers.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeResponse {
    
    Long id;
    
    // Basic info
    String employeeCode;
    String fullName;
    String alternateName;
    LocalDateTime birthDate;
    
    // Reference data using RefDto (cleaner than id + name pairs)
    RefDto gender;
    RefDto maritalStatus;
    RefDto nationality;
    RefDto laborStatus;
    RefDto bloodGroup;
    
    // Identity documents
    String idNumberCmnd;
    LocalDateTime idIssueDateCmnd;
    RefDto idIssuePlaceCmnd;
    String idNumberCccd;
    LocalDateTime idIssueDateCccd;
    RefDto idIssuePlaceCccd;
    
    // Work info
    LocalDateTime startDate;
    LocalDateTime officialStartDate;
    LocalDateTime seniorityStartDate;
    Integer seniorityDeductionDays;
    String taxCode;
    RefDto manager;
    Integer entitledLeaveDays;
    
    // Education and language
    RefDto graduationSchool;
    Integer graduationYear;
    RefDto language1;
    RefDto language2;
    RefDto language3;
    
    // Health
    String healthStatus;
    
    // Passport and permits
    String passportNumber;
    LocalDateTime passportExpiryDate;
    String workPermitNumber;
    LocalDateTime workPermitStartDate;
    LocalDateTime workPermitExpiryDate;
    String temporaryResidenceCard;
    LocalDateTime tempResidenceStartDate;
    LocalDateTime tempResidenceExpiryDate;
    
    // Salary
    Boolean noSalaryAdvance;
    
    // Address info
    String currentAddressStreet;
    RefDto currentAddressWard;
    String permanentAddressStreet;
    RefDto permanentAddressWard;
    RefDto hometown;
    RefDto placeOfBirth;
    
    // Contact
    String personalPhone;
    String homePhone;
    String companyEmail;
    String personalEmail;
    String emergencyContactName;
    String emergencyContactPhone;
    
    // Bank info
    String bankAccountNumber;
    RefDto bank;
    String bankBranch;
    
    // Tax
    Boolean taxDeclarationAuthorized;
    Boolean excludePersonalDeduction;
    
    // Termination
    LocalDateTime terminationDate;
    
    // Insurance
    String socialInsuranceNo;
    String socialInsuranceCode;
    String healthInsuranceCard;
    RefDto medicalRegistration;

    // Child collections
    Set<EmployeeDecisionResponse> employeeDecisions;
    Set<EmployeeEducationResponse> employeeEducations;
    EmployeeWorkShiftResponse employeeWorkShift;
    Set<EmployeeAttendanceMachineResponse> employeeAttendanceMachines;
    Set<EmployeeWorkLocationResponse> employeeWorkLocations;
}
