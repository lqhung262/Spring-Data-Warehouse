package com.example.demo.entity.humanresource;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "employee")
@Data
@SQLDelete(sql = "UPDATE employee SET is_deleted = true WHERE employee_id = ?")
@SQLRestriction("is_deleted = false")
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


    @NotNull
    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    @Column(name = "alternate_name", length = 255)
    private String alternateName;

    @NotNull
    @Column(name = "birth_date", nullable = false)
    private LocalDateTime birthDate;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "official_start_date")
    private LocalDateTime officialStartDate;

    @Column(name = "seniority_start_date")
    private LocalDateTime seniorityStartDate;

    @Column(name = "seniority_deduction_days")
    private Integer seniorityDeductionDays;


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

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;


    @NotNull
    @Column(name = "permanent_address_street", nullable = false, length = 255)
    private String permanentAddressStreet;

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

    @Column(name = "bank_branch", length = 255)
    private String bankBranch;

    @Column(name = "tax_declaration_authorized")
    private Boolean taxDeclarationAuthorized;

    @Column(name = "exclude_personal_deduction")
    private Boolean excludePersonalDeduction;

    @Column(name = "termination_date")
    private LocalDateTime terminationDate;

    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
    private Set<EmployeeDecision> employeeDecisionList;

    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
    private Set<EmployeeEducation> employeeEducationList;

    @OneToOne(mappedBy = "employee")
    private EmployeeWorkShift employeeWorkShift;

    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
    private Set<EmployeeAttendanceMachine> employeeAttendanceMachineList;

    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
    private Set<EmployeeWorkLocation> employeeWorkLocationList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_id")
    private Bank bank;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marital_status_id")
    private MaritalStatus maritalStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gender_id")
    private Gender gender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nationality_id")
    private Nationality nationality;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "labor_status_id")
    private LaborStatus laborStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Employee manager;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "graduation_school_id")
    private School graduationSchool;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_1")
    private Language language1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_2")
    private Language language2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_3")
    private Language language3;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blood_group_id")
    private BloodGroup bloodGroup;

    @Size(max = 255)
    @Column(name = "current_address_street")
    private String currentAddressStreet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_address_ward")
    private Ward currentAddressWard;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "permanent_address_ward", nullable = false)
    private Ward permanentAddressWard;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hometown_id", nullable = false)
    private ProvinceCity hometown;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "place_of_birth_id", nullable = false)
    private ProvinceCity placeOfBirth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_registration")
    private MedicalFacility medicalRegistration;

    @Column(name = "business_partner_id")
    private Long businessPartnerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_issue_place_cmnd")
    private IdentityIssuingAuthority idIssuePlaceCmnd;


    @Size(max = 20)
    @Column(name = "id_number_cmnd", length = 20)
    private String idNumberCmnd;

    @Column(name = "id_issue_date_cmnd")
    private LocalDateTime idIssueDateCmnd;

    @Size(max = 20)
    @Column(name = "id_number_cccd", length = 20)
    private String idNumberCccd;

    @Column(name = "id_issue_date_cccd")
    private LocalDateTime idIssueDateCccd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_issue_place_cccd")
    private IdentityIssuingAuthority idIssuePlaceCccd;


    @Column(name = "entitled_leave_days")
    private Integer entitledLeaveDays;

    @Column(name = "graduation_year")
    private Integer graduationYear;

    @Size(max = 255)
    @Column(name = "health_status")
    private String healthStatus;

    @Size(max = 100)
    @NotNull
    @Column(name = "corporation_code", nullable = false, length = 100, unique = true)
    private String corporationCode;

    @Size(max = 50)
    @NotNull
    @Column(name = "tax_code", length = 50, nullable = false, unique = true)
    private String taxCode;

    @Size(max = 50)
    @NotNull
    @Column(name = "social_insurance_no", length = 50, nullable = false, unique = true)
    private String socialInsuranceNo;

    @Size(max = 50)
    @NotNull
    @Column(name = "social_insurance_code", length = 50, nullable = false, unique = true)
    private String socialInsuranceCode;

    @Size(max = 50)
    @NotNull
    @Column(name = "health_insurance_card", length = 50, nullable = false, unique = true)
    private String healthInsuranceCard;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return id != null && Objects.equals(id, employee.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
