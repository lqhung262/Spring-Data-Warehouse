package com.example.demo.dto.humanresource.Employee;

import com.example.demo.dto.humanresource.EmployeeAttendanceMachine.EmployeeAttendanceMachineRequest;
import com.example.demo.dto.humanresource.EmployeeDecision.EmployeeDecisionRequest;
import com.example.demo.dto.humanresource.EmployeeEducation.EmployeeEducationRequest;
import com.example.demo.dto.humanresource.EmployeeWorkLocation.EmployeeWorkLocationRequest;
import com.example.demo.dto.humanresource.EmployeeWorkShift.EmployeeWorkShiftRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeRequest {
    @NotBlank
    private String employeeCode;

    @NotBlank
    private String sourceId;

    @NotNull
    private Long businessPartnerId;

    @NotBlank
    private String corporationCode;

    @NotBlank
    private String fullName;

    private String alternateName;

    @NotNull
    private LocalDateTime birthDate;

    @NotNull
    private Long genderId;

    private Long maritalStatusId;

    private String idNumberCmnd;

    private LocalDateTime idIssueDateCmnd;

    private Long idIssuePlaceCmnd;

    private String idNumberCccd;

    private LocalDateTime idIssueDateCccd;

    private Long idIssuePlaceCccd;

    private Long nationalityId;

    @NotNull
    private LocalDateTime startDate;

    private LocalDateTime officialStartDate;

    private LocalDateTime seniorityStartDate;

    private Integer seniorityDeductionDays;

    private Long laborStatusId;

    private String taxCode;

    private Long managerId;

    private Integer entitledLeaveDays;

    private Long graduationSchoolId;

    private Integer graduationYear;

    private Long language1;

    private Long language2;

    private Long language3;

    private Long bloodGroupId;

    private String healthStatus;

    private String passportNumber;

    private LocalDateTime passportExpiryDate;

    private String workPermitNumber;

    private LocalDateTime workPermitStartDate;

    private LocalDateTime workPermitExpiryDate;

    private String temporaryResidenceCard;

    private LocalDateTime tempResidenceStartDate;

    private LocalDateTime tempResidenceExpiryDate;

    private Boolean noSalaryAdvance;

    @NotNull
    private Long sourceSystemId;

    private Long createdBy;
    private Long updatedBy;

    private String currentAddressStreet;

    private Long currentAddressWard;

    @NotBlank
    private String permanentAddressStreet;

    @NotNull
    private Long permanentAddressWard;

    @NotNull
    private Long hometownId;

    @NotNull
    private Long placeOfBirthId;

    private String personalPhone;

    private String homePhone;

    private String companyEmail;

    private String personalEmail;

    private String emergencyContactName;

    private String emergencyContactPhone;

    private String bankAccountNumber;

    private Long bankId;

    private String bankBranch;

    private Boolean taxDeclarationAuthorized;

    private Boolean excludePersonalDeduction;

    private LocalDateTime terminationDate;

    private String socialInsuranceNo;

    private String socialInsuranceCode;

    private String healthInsuranceCard;

    private Long medicalRegistration;

    // Related child objects. These are created/updated only via Employee APIs.
    @Valid
    private Set<EmployeeDecisionRequest> employeeDecisions;

    @Valid
    private Set<EmployeeEducationRequest> employeeEducations;

    @Valid
    private EmployeeWorkShiftRequest employeeWorkShift;

    @Valid
    private Set<EmployeeAttendanceMachineRequest> employeeAttendanceMachines;

    @Valid
    private Set<EmployeeWorkLocationRequest> employeeWorkLocations;
}
