-- =====================================================================
-- MIGRATION V1.9: Add CASCADE DELETE and RESTRICT Constraints
-- Purpose: Enforce referential integrity at database level
-- =====================================================================

-- =====================================================================
-- PART 1: EMPLOYEE CHILDREN - ON DELETE CASCADE
-- When Employee is deleted, automatically delete all related records
-- =====================================================================

-- 1. Employee → EmployeeDecision
ALTER TABLE employee_decision
    DROP FOREIGN KEY fk_ed_employee;

ALTER TABLE employee_decision
    ADD CONSTRAINT fk_ed_employee
        FOREIGN KEY (employee_id) REFERENCES employee (employee_id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT;

-- 2. Employee → EmployeeEducation
ALTER TABLE employee_education
    DROP FOREIGN KEY fk_eedu_emp;

ALTER TABLE employee_education
    ADD CONSTRAINT fk_eedu_emp
        FOREIGN KEY (employee_id) REFERENCES employee (employee_id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT;

-- 3. Employee → EmployeeWorkShift
ALTER TABLE employee_work_shift
    DROP FOREIGN KEY fk_ews_emp;

ALTER TABLE employee_work_shift
    ADD CONSTRAINT fk_ews_emp
        FOREIGN KEY (employee_id) REFERENCES employee (employee_id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT;

-- 4. Employee → EmployeeAttendanceMachine
ALTER TABLE employee_attendance_machine
    DROP FOREIGN KEY fk_eam_emp;

ALTER TABLE employee_attendance_machine
    ADD CONSTRAINT fk_eam_emp
        FOREIGN KEY (employee_id) REFERENCES employee (employee_id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT;

-- 5. Employee → EmployeeWorkLocation
ALTER TABLE employee_work_location
    DROP FOREIGN KEY fk_ewl_emp;

ALTER TABLE employee_work_location
    ADD CONSTRAINT fk_ewl_emp
        FOREIGN KEY (employee_id) REFERENCES employee (employee_id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT;

-- =====================================================================
-- PART 2: LOCATION HIERARCHY - ON DELETE RESTRICT
-- Prevent deletion of location if still referenced
-- =====================================================================

-- ProvinceCity → Ward
ALTER TABLE ward
    DROP FOREIGN KEY fk_ward_pc;

ALTER TABLE ward
    ADD CONSTRAINT fk_ward_pc
        FOREIGN KEY (province_city_id) REFERENCES province_city (province_city_id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;

-- ProvinceCity → OldProvinceCity
ALTER TABLE old_province_city
    DROP FOREIGN KEY fk_old_pc_pc;

ALTER TABLE old_province_city
    ADD CONSTRAINT fk_old_pc_pc
        FOREIGN KEY (province_city_id) REFERENCES province_city (province_city_id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;

-- ProvinceCity → MedicalFacility
ALTER TABLE medical_facility
    DROP FOREIGN KEY fk_mf_pc;

ALTER TABLE medical_facility
    ADD CONSTRAINT fk_mf_pc
        FOREIGN KEY (province_city_id) REFERENCES province_city (province_city_id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;

-- ProvinceCity → Employee (hometown)
ALTER TABLE employee
    DROP FOREIGN KEY fk_emp_hometown;

ALTER TABLE employee
    ADD CONSTRAINT fk_emp_hometown
        FOREIGN KEY (hometown_id) REFERENCES province_city (province_city_id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;

-- ProvinceCity → Employee (place_of_birth)
ALTER TABLE employee
    DROP FOREIGN KEY fk_emp_birth_place;

ALTER TABLE employee
    ADD CONSTRAINT fk_emp_birth_place
        FOREIGN KEY (place_of_birth_id) REFERENCES province_city (province_city_id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;

-- Ward → OldWard
ALTER TABLE old_ward
    DROP FOREIGN KEY fk_old_ward_ward;

ALTER TABLE old_ward
    ADD CONSTRAINT fk_old_ward_ward
        FOREIGN KEY (ward_id) REFERENCES ward (ward_id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;

-- Ward → OldDistrict
ALTER TABLE old_district
    DROP FOREIGN KEY fk_old_dist_ward;

ALTER TABLE old_district
    ADD CONSTRAINT fk_old_dist_ward
        FOREIGN KEY (ward_id) REFERENCES ward (ward_id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;

-- Ward → Employee (current_address)
ALTER TABLE employee
    DROP FOREIGN KEY fk_emp_current_ward;

ALTER TABLE employee
    ADD CONSTRAINT fk_emp_current_ward
        FOREIGN KEY (current_address_ward) REFERENCES ward (ward_id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;

-- Ward → Employee (permanent_address)
ALTER TABLE employee
    DROP FOREIGN KEY fk_emp_perm_ward;

ALTER TABLE employee
    ADD CONSTRAINT fk_emp_perm_ward
        FOREIGN KEY (permanent_address_ward) REFERENCES ward (ward_id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;

-- OldProvinceCity → OldDistrict
ALTER TABLE old_district
    DROP FOREIGN KEY fk_old_dist_old_pc;

ALTER TABLE old_district
    ADD CONSTRAINT fk_old_dist_old_pc
        FOREIGN KEY (old_province_city_id) REFERENCES old_province_city (old_province_city_id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;

-- OldDistrict → OldWard
ALTER TABLE old_ward
    DROP FOREIGN KEY fk_old_ward_district;

ALTER TABLE old_ward
    ADD CONSTRAINT fk_old_ward_district
        FOREIGN KEY (old_district_id) REFERENCES old_district (old_district_id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;

-- =====================================================================
-- PART 3: MASTER DATA - ON DELETE RESTRICT
-- Prevent deletion of master data if in use
-- =====================================================================

-- Department → EmployeeDecision
ALTER TABLE employee_decision
    DROP FOREIGN KEY fk_ed_department;

ALTER TABLE employee_decision
    ADD CONSTRAINT fk_ed_department
        FOREIGN KEY (department_id) REFERENCES department (department_id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;

-- EmployeeType → EmployeeDecision
ALTER TABLE employee_decision
    DROP FOREIGN KEY fk_ed_employee_type;

ALTER TABLE employee_decision
    ADD CONSTRAINT fk_ed_employee_type
        FOREIGN KEY (employee_type_id) REFERENCES employee_type (employee_type_id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;

-- JobPosition → EmployeeDecision
ALTER TABLE employee_decision
    DROP FOREIGN KEY fk_ed_position;

ALTER TABLE employee_decision
    ADD CONSTRAINT fk_ed_position
        FOREIGN KEY (job_position_id) REFERENCES job_position (position_id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;

-- JobTitle → EmployeeDecision
ALTER TABLE employee_decision
    DROP FOREIGN KEY fk_ed_title;

ALTER TABLE employee_decision
    ADD CONSTRAINT fk_ed_title
        FOREIGN KEY (job_title_id) REFERENCES job_title (job_title_id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;

-- JobRank → EmployeeDecision
ALTER TABLE employee_decision
    DROP FOREIGN KEY fk_ed_rank;

ALTER TABLE employee_decision
    ADD CONSTRAINT fk_ed_rank
        FOREIGN KEY (job_rank_id) REFERENCES job_rank (job_rank_id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;

-- ExpenseType → EmployeeDecision (cost_category_level_1)
ALTER TABLE employee_decision
    DROP FOREIGN KEY fk_ed_costlvl1;

ALTER TABLE employee_decision
    ADD CONSTRAINT fk_ed_costlvl1
        FOREIGN KEY (cost_category_level_1) REFERENCES expense_type (expense_type_id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;

-- ExpenseType → EmployeeDecision (cost_category_level_2)
ALTER TABLE employee_decision
    DROP FOREIGN KEY fk_ed_costlvl2;

ALTER TABLE employee_decision
    ADD CONSTRAINT fk_ed_costlvl2
        FOREIGN KEY (cost_category_level_2) REFERENCES expense_type (expense_type_id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;

-- DecisionType → EmployeeDecision
ALTER TABLE employee_decision
    DROP FOREIGN KEY fk_ed_decision_type_id;

ALTER TABLE employee_decision
    ADD CONSTRAINT fk_ed_decision_type_id
        FOREIGN KEY (decision_type_id) REFERENCES decision_type (decision_type_id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;

-- Major → EmployeeEducation
ALTER TABLE employee_education
    DROP FOREIGN KEY fk_eedu_major;

ALTER TABLE employee_education
    ADD CONSTRAINT fk_eedu_major
        FOREIGN KEY (major_id) REFERENCES major (major_id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;

-- Specialization → EmployeeEducation
ALTER TABLE employee_education
    DROP FOREIGN KEY fk_eedu_spec;

ALTER TABLE employee_education
    ADD CONSTRAINT fk_eedu_spec
        FOREIGN KEY (specialization_id) REFERENCES specialization (specialization_id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;

-- EducationLevel → EmployeeEducation
ALTER TABLE employee_education
    DROP FOREIGN KEY fk_eedu_edlvl;

ALTER TABLE employee_education
    ADD CONSTRAINT fk_eedu_edlvl
        FOREIGN KEY (education_level_id) REFERENCES education_level (education_level_id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;

-- School → EmployeeEducation
ALTER TABLE employee_education
    DROP FOREIGN KEY fk_eedu_school;

ALTER TABLE employee_education
    ADD CONSTRAINT fk_eedu_school
        FOREIGN KEY (school_id) REFERENCES school (school_id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;

-- Bank → Employee
ALTER TABLE employee
    DROP FOREIGN KEY fk_emp_bank;

ALTER TABLE employee
    ADD CONSTRAINT fk_emp_bank
        FOREIGN KEY (bank_id) REFERENCES bank (bank_id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;

-- Gender → Employee
ALTER TABLE employee
    DROP FOREIGN KEY fk_emp_gender;

ALTER TABLE employee
    ADD CONSTRAINT fk_emp_gender
        FOREIGN KEY (gender_id) REFERENCES gender (gender_id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;

-- MaritalStatus → Employee
ALTER TABLE employee
    DROP FOREIGN KEY fk_emp_marital_status;

ALTER TABLE employee
    ADD CONSTRAINT fk_emp_marital_status
        FOREIGN KEY (marital_status_id) REFERENCES marital_status (marital_status_id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;

-- Nationality → Employee
ALTER TABLE employee
    DROP FOREIGN KEY fk_emp_nat;

ALTER TABLE employee
    ADD CONSTRAINT fk_emp_nat
        FOREIGN KEY (nationality_id) REFERENCES nationality (nationality_id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;

-- LaborStatus → Employee
ALTER TABLE employee
    DROP FOREIGN KEY fk_emp_labor_status;

ALTER TABLE employee
    ADD CONSTRAINT fk_emp_labor_status
        FOREIGN KEY (labor_status_id) REFERENCES labor_status (labor_status_id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;

-- School → Employee (graduation_school)
ALTER TABLE employee
    DROP FOREIGN KEY fk_emp_grad_school;

ALTER TABLE employee
    ADD CONSTRAINT fk_emp_grad_school
        FOREIGN KEY (graduation_school_id) REFERENCES school (school_id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;

-- Language → Employee (language_1)
ALTER TABLE employee
    DROP FOREIGN KEY fk_emp_lang1;

ALTER TABLE employee
    ADD CONSTRAINT fk_emp_lang1
        FOREIGN KEY (language_1) REFERENCES language (language_id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;

-- Language → Employee (language_2)
ALTER TABLE employee
    DROP FOREIGN KEY fk_emp_lang2;

ALTER TABLE employee
    ADD CONSTRAINT fk_emp_lang2
        FOREIGN KEY (language_2) REFERENCES language (language_id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;

-- Language → Employee (language_3)
ALTER TABLE employee
    DROP FOREIGN KEY fk_emp_lang3;

ALTER TABLE employee
    ADD CONSTRAINT fk_emp_lang3
        FOREIGN KEY (language_3) REFERENCES language (language_id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;

-- BloodGroup → Employee
ALTER TABLE employee
    DROP FOREIGN KEY fk_emp_blood_group;

ALTER TABLE employee
    ADD CONSTRAINT fk_emp_blood_group
        FOREIGN KEY (blood_group_id) REFERENCES blood_group (blood_group_id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;

-- MedicalFacility → Employee
ALTER TABLE employee
    DROP FOREIGN KEY fk_emp_med_reg;

ALTER TABLE employee
    ADD CONSTRAINT fk_emp_med_reg
        FOREIGN KEY (medical_registration) REFERENCES medical_facility (medical_facility_id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;

-- IdentityIssuingAuthority → Employee (id_issue_place_cmnd)
ALTER TABLE employee
    DROP FOREIGN KEY fk_emp_iia_cmnd;

ALTER TABLE employee
    ADD CONSTRAINT fk_emp_iia_cmnd
        FOREIGN KEY (id_issue_place_cmnd) REFERENCES identity_issuing_authority (identity_issuing_authority_id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;

-- IdentityIssuingAuthority → Employee (id_issue_place_cccd)
ALTER TABLE employee
    DROP FOREIGN KEY fk_emp_iia_cccd;

ALTER TABLE employee
    ADD CONSTRAINT fk_emp_iia_cccd
        FOREIGN KEY (id_issue_place_cccd) REFERENCES identity_issuing_authority (identity_issuing_authority_id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;

-- WorkShift → EmployeeWorkShift
ALTER TABLE employee_work_shift
    DROP FOREIGN KEY fk_ews_ws;

ALTER TABLE employee_work_shift
    ADD CONSTRAINT fk_ews_ws
        FOREIGN KEY (work_shift_id) REFERENCES work_shift (work_shift_id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;

-- WorkShiftGroup → EmployeeWorkShift
ALTER TABLE employee_work_shift
    DROP FOREIGN KEY fk_ews_wsg;

ALTER TABLE employee_work_shift
    ADD CONSTRAINT fk_ews_wsg
        FOREIGN KEY (work_shift_group_id) REFERENCES work_shift_group (work_shift_group_id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;

-- AttendanceType → EmployeeWorkShift
ALTER TABLE employee_work_shift
    DROP FOREIGN KEY fk_ews_att_type;

ALTER TABLE employee_work_shift
    ADD CONSTRAINT fk_ews_att_type
        FOREIGN KEY (attendance_type_id) REFERENCES attendance_type (attendance_type_id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;

-- OtType → EmployeeWorkShift
ALTER TABLE employee_work_shift
    DROP FOREIGN KEY fk_ews_ot;

ALTER TABLE employee_work_shift
    ADD CONSTRAINT fk_ews_ot
        FOREIGN KEY (ot_type_id) REFERENCES ot_type (ot_type_id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;

-- AttendanceMachine → EmployeeAttendanceMachine
ALTER TABLE employee_attendance_machine
    DROP FOREIGN KEY fk_eam_mach;

ALTER TABLE employee_attendance_machine
    ADD CONSTRAINT fk_eam_mach
        FOREIGN KEY (machine_id) REFERENCES attendance_machine (attendance_machine_id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;

-- WorkLocation → EmployeeWorkLocation
ALTER TABLE employee_work_location
    DROP FOREIGN KEY fk_ewl_wl;

ALTER TABLE employee_work_location
    ADD CONSTRAINT fk_ewl_wl
        FOREIGN KEY (work_location_id) REFERENCES work_location (work_location_id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;

-- DocumentType → IdentityIssuingAuthority
ALTER TABLE identity_issuing_authority
    DROP FOREIGN KEY fk_iia_doc_type;

ALTER TABLE identity_issuing_authority
    ADD CONSTRAINT fk_iia_doc_type
        FOREIGN KEY (document_type_id) REFERENCES document_type (document_type_id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;

-- =====================================================================
-- PART 4: SPECIAL CASES - SET NULL
-- Allow deletion but keep historical records
-- =====================================================================

-- Employee (Manager) → Employee (SET NULL when manager deleted)
ALTER TABLE employee
    DROP FOREIGN KEY fk_emp_mgr;

ALTER TABLE employee
    ADD CONSTRAINT fk_emp_mgr
        FOREIGN KEY (manager_id) REFERENCES employee (employee_id)
            ON DELETE SET NULL
            ON UPDATE RESTRICT;

-- Employee → UserProfile (SET NULL when employee deleted)
ALTER TABLE user_profile
    DROP FOREIGN KEY fk_user_profile_employee;

ALTER TABLE user_profile
    ADD CONSTRAINT fk_user_profile_employee
        FOREIGN KEY (employee_id) REFERENCES employee (employee_id)
            ON DELETE SET NULL
            ON UPDATE RESTRICT;

-- =====================================================================
-- END OF MIGRATION
-- =====================================================================
-- Summary:
-- - 5 CASCADE constraints (Employee children)
-- - 11 RESTRICT constraints (Location hierarchy)
-- - 32 RESTRICT constraints (Master data)
-- - 2 SET NULL constraints (Special cases)
-- TOTAL: 50 constraints updated
-- =====================================================================

