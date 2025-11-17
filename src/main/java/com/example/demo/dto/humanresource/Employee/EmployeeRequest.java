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

    @NotBlank
    private String alternateName;

    @NotNull
    private LocalDateTime birthDate;

    @NotNull
    private Long genderId;

    @NotNull
    private Long maritalStatusId;

    @NotBlank
    private String idNumberCmnd;

    @NotNull
    private LocalDateTime idIssueDateCmnd;

    @NotNull
    private Long idIssuePlaceCmnd;

    @NotBlank
    private String idNumberCccd;

    @NotNull
    private LocalDateTime idIssueDateCccd;

    @NotNull
    private Long idIssuePlaceCccd;

    @NotNull
    private Long nationalityId;

    @NotNull
    private LocalDateTime startDate;

    @NotNull
    private LocalDateTime officialStartDate;

    @NotNull
    private LocalDateTime seniorityStartDate;

    @NotNull
    private Integer seniorityDeductionDays;

    @NotNull
    private Long laborStatusId;

    @NotBlank
    private String taxCode;

    @NotNull
    private Long managerId;

    @NotNull
    private Integer entitledLeaveDays;

    @NotNull
    private Long graduationSchoolId;

    @NotNull
    private Integer graduationYear;

    @NotNull
    private Long language1;

    @NotNull
    private Long language2;

    @NotNull
    private Long language3;

    @NotNull
    private Long bloodGroupId;

    @NotBlank
    private String healthStatus;

    @NotBlank
    private String passportNumber;

    @NotNull
    private LocalDateTime passportExpiryDate;

    @NotBlank
    private String workPermitNumber;

    @NotNull
    private LocalDateTime workPermitStartDate;

    @NotNull
    private LocalDateTime workPermitExpiryDate;

    @NotBlank
    private String temporaryResidenceCard;

    @NotNull
    private LocalDateTime tempResidenceStartDate;

    @NotNull
    private LocalDateTime tempResidenceExpiryDate;

    @NotNull
    private Boolean noSalaryAdvance;

    @NotNull
    private Long sourceSystemId;

    private Long createdBy;
    private Long updatedBy;

    @NotBlank
    private String currentAddressStreet;

    @NotNull
    private Long currentAddressWard;

    @NotBlank
    private String permanentAddressStreet;

    @NotNull
    private Long permanentAddressWard;

    @NotNull
    private Long hometownId;

    @NotNull
    private Long placeOfBirthId;

    @NotBlank
    private String personalPhone;

    @NotBlank
    private String homePhone;

    @NotBlank
    private String companyEmail;

    @NotBlank
    private String personalEmail;

    @NotBlank
    private String emergencyContactName;

    @NotBlank
    private String emergencyContactPhone;

    @NotBlank
    private String bankAccountNumber;

    @NotNull
    private Long bankId;

    @NotBlank
    private String bankBranch;

    @NotNull
    private Boolean taxDeclarationAuthorized;

    @NotNull
    private Boolean excludePersonalDeduction;

    @NotNull
    private LocalDateTime terminationDate;

    @NotBlank
    private String socialInsuranceNo;

    @NotBlank
    private String socialInsuranceCode;

    @NotBlank
    private String healthInsuranceCard;

    @NotNull
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
