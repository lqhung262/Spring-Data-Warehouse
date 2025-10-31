package com.example.demo.entity.humanresource;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.SoftDelete;

import java.time.LocalDateTime;

@Entity
@Table(name = "employee")
@Data
@SoftDelete(columnName = "is_deleted")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Long id;

    @NotNull
    @Column(name = "employee_code", nullable = false, unique = true, length = 100)
    private String employeeCode;

    @NotNull
    @Column(name = "source_id", nullable = false, unique = true, length = 100)
    private String sourceId;

    @Column(name = "business_partner_id")
    private Long businessPartnerId; // để ID cho nhẹ (nếu chưa build entity BP)

    @NotNull
    @Column(name = "corporation_code", nullable = false, unique = true, length = 100)
    private String corporationCode;

    @NotNull
    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    @Column(name = "alternate_name", length = 255)
    private String alternateName;

    @NotNull
    @Column(name = "birth_date", nullable = false)
    private LocalDateTime birthDate;

    @Column(name = "gender_id")
    private Long genderId;

    @Column(name = "marital_status_id")
    private Long maritalStatusId;

    @Column(name = "id_number_cmnd", length = 20)
    private String idNumberCmnd;

    @Column(name = "id_issue_date_cmnd")
    private LocalDateTime idIssueDateCmnd;

    @Column(name = "id_issue_place_cmnd")
    private Long idIssuePlaceCmnd; // FK -> identity_issuing_authority

    @Column(name = "id_number_cccd", length = 20)
    private String idNumberCccd;

    @Column(name = "id_issue_date_cccd")
    private LocalDateTime idIssueDateCccd;

    @Column(name = "id_issue_place_cccd")
    private Long idIssuePlaceCccd;

    @Column(name = "nationality_id")
    private Long nationalityId;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "official_start_date")
    private LocalDateTime officialStartDate;

    @Column(name = "seniority_start_date")
    private LocalDateTime seniorityStartDate;

    @Column(name = "seniority_deduction_days")
    private Integer seniorityDeductionDays;

    @Column(name = "labor_status_id")
    private Long laborStatusId;

    @Column(name = "tax_code", unique = true, length = 50)
    private String taxCode;

    @Column(name = "manager_id")
    private Long managerId; // self-reference

    @Column(name = "entitled_leave_days")
    private Integer entitledLeaveDays;

    @Column(name = "graduation_school_id")
    private Long graduationSchoolId;

    @Column(name = "graduation_year")
    private Integer graduationYear;

    @Column(name = "language_1")
    private Long language1;

    @Column(name = "language_2")
    private Long language2;

    @Column(name = "language_3")
    private Long language3;

    @Column(name = "blood_group_id")
    private Long bloodGroupId;

    @Column(name = "health_status", length = 255)
    private String healthStatus;

    @Column(name = "passport_number", length = 50)
    private String passportNumber;

    @Column(name = "passport_expiry_date")
    private LocalDateTime passportExpiryDate;

    @Column(name = "work_permit_number", length = 50)
    private String workPermitNumber;

    @Column(name = "work_permit_start_date")
    private LocalDateTime workPermitStartDate;

    @Column(name = "work_permit_expiry_date")
    private LocalDateTime workPermitExpiryDate;

    @Column(name = "temporary_residence_card", length = 50)
    private String temporaryResidenceCard;

    @Column(name = "temp_residence_start_date")
    private LocalDateTime tempResidenceStartDate;

    @Column(name = "temp_residence_expiry_date")
    private LocalDateTime tempResidenceExpiryDate;

    @Column(name = "no_salary_advance")
    private Boolean noSalaryAdvance;

    @NotNull
    @Column(name = "source_system_id", nullable = false)
    private Long sourceSystemId;

    @NotNull
    @Column(name = "created_by", nullable = false)
    private Long createdBy = 1L;

    @NotNull
    @Column(name = "updated_by", nullable = false)
    private Long updatedBy = 1L;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;


    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @Column(name = "current_address_street", length = 255)
    private String currentAddressStreet;

    @Column(name = "current_address_ward")
    private Long currentAddressWard;

    @NotNull
    @Column(name = "permanent_address_street", nullable = false, length = 255)
    private String permanentAddressStreet;

    @NotNull
    @Column(name = "permanent_address_ward", nullable = false)
    private Long permanentAddressWard;

    @NotNull
    @Column(name = "hometown_id", nullable = false)
    private Long hometownId;

    @NotNull
    @Column(name = "place_of_birth_id", nullable = false)
    private Long placeOfBirthId;

    @Column(name = "personal_phone", length = 20)
    private String personalPhone;

    @Column(name = "home_phone", length = 20)
    private String homePhone;

    @Column(name = "company_email", length = 255)
    private String companyEmail;

    @Column(name = "personal_email", length = 255)
    private String personalEmail;

    @Column(name = "emergency_contact_name", length = 255)
    private String emergencyContactName;

    @Column(name = "emergency_contact_phone", length = 20)
    private String emergencyContactPhone;

    @Column(name = "bank_account_number", length = 50)
    private String bankAccountNumber;

    @Column(name = "bank_id")
    private Long bankId;

    @Column(name = "bank_branch", length = 255)
    private String bankBranch;

    @Column(name = "tax_declaration_authorized")
    private Boolean taxDeclarationAuthorized;

    @Column(name = "exclude_personal_deduction")
    private Boolean excludePersonalDeduction;

    @Column(name = "termination_date")
    private LocalDateTime terminationDate;

    @Column(name = "social_insurance_no", unique = true, length = 50)
    private String socialInsuranceNo;

    @Column(name = "social_insurance_code", unique = true, length = 50)
    private String socialInsuranceCode;

    @Column(name = "health_insurance_card", unique = true, length = 50)
    private String healthInsuranceCard;

    @Column(name = "medical_registration")
    private Long medicalRegistration;

}
