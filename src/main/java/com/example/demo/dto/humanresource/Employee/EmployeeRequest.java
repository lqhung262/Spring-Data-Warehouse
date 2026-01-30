package com.example.demo.dto.humanresource.Employee;

import com.example.demo.dto.common.BaseRequest;
import com.example.demo.dto.humanresource.EmployeeAttendanceMachine.EmployeeAttendanceMachineRequest;
import com.example.demo.dto.humanresource.EmployeeDecision.EmployeeDecisionRequest;
import com.example.demo.dto.humanresource.EmployeeEducation.EmployeeEducationRequest;
import com.example.demo.dto.humanresource.EmployeeWorkLocation.EmployeeWorkLocationRequest;
import com.example.demo.dto.humanresource.EmployeeWorkShift.EmployeeWorkShiftRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Request DTO for Employee operations.
 * Extends BaseRequest to inherit sourceId and sourceSystemId fields.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class EmployeeRequest extends BaseRequest {
    
    @NotBlank(message = "Employee code is required")
    private String employeeCode;

    @NotNull(message = "Business partner ID is required")
    private Long businessPartnerId;

    @NotBlank(message = "Corporation code is required")
    private String corporationCode;

    @NotBlank(message = "Full name is required")
    private String fullName;

    private String alternateName;

    @NotNull(message = "Birth date is required")
    private LocalDateTime birthDate;

    @NotNull(message = "Gender ID is required")
    private Long genderId;

    private Long maritalStatusId;

    private String idNumberCmnd;
    private LocalDateTime idIssueDateCmnd;
    private Long idIssuePlaceCmnd;

    private String idNumberCccd;
    private LocalDateTime idIssueDateCccd;
    private Long idIssuePlaceCccd;

    private Long nationalityId;

    @NotNull(message = "Start date is required")
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

    private String currentAddressStreet;
    private Long currentAddressWard;

    @NotBlank(message = "Permanent address street is required")
    private String permanentAddressStreet;

    @NotNull(message = "Permanent address ward is required")
    private Long permanentAddressWard;

    @NotNull(message = "Hometown ID is required")
    private Long hometownId;

    @NotNull(message = "Place of birth ID is required")
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

    // Related child objects - created/updated only via Employee APIs
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
