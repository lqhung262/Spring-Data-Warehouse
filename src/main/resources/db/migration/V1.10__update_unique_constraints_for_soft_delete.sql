-- V1.10__update_unique_constraints_for_soft_delete.sql

/*
 Sử dụng Functional Index của MySQL 8.0+.
 Cú pháp: UNIQUE (column_name, (IF(is_deleted, NULL, 1)))
 - Nếu is_deleted = 1 -> Giá trị thứ 2 là NULL (MySQL cho phép trùng nhiều NULL trong Unique).
 - Nếu is_deleted = 0 -> Giá trị thứ 2 là 1 (Bắt buộc Unique).
*/

-- ==========================================================
-- 1. ATTENDANCE GROUP
-- ==========================================================
-- Table: attendance_machine
ALTER TABLE attendance_machine
    DROP INDEX attendance_machine_code;
ALTER TABLE attendance_machine
    DROP INDEX source_id;
ALTER TABLE attendance_machine
    DROP INDEX name;

ALTER TABLE attendance_machine
    ADD UNIQUE INDEX uq_am_code_active (attendance_machine_code, (IF(is_deleted, NULL, 1)));
ALTER TABLE attendance_machine
    ADD UNIQUE INDEX uq_am_source_active (source_id, (IF(is_deleted, NULL, 1)));
ALTER TABLE attendance_machine
    ADD UNIQUE INDEX uq_am_name_active (name, (IF(is_deleted, NULL, 1)));

-- Table: attendance_type
ALTER TABLE attendance_type
    DROP INDEX attendance_type_code;
ALTER TABLE attendance_type
    DROP INDEX source_id;
ALTER TABLE attendance_type
    DROP INDEX name;

ALTER TABLE attendance_type
    ADD UNIQUE INDEX uq_at_code_active (attendance_type_code, (IF(is_deleted, NULL, 1)));
ALTER TABLE attendance_type
    ADD UNIQUE INDEX uq_at_source_active (source_id, (IF(is_deleted, NULL, 1)));
ALTER TABLE attendance_type
    ADD UNIQUE INDEX uq_at_name_active (name, (IF(is_deleted, NULL, 1)));


-- ==========================================================
-- 2. CORE CATEGORIES (Bank, Country, Etc.)
-- ==========================================================
-- Table: bank
ALTER TABLE bank
    DROP INDEX bank_code;
ALTER TABLE bank
    DROP INDEX source_id;
ALTER TABLE bank
    DROP INDEX name;
ALTER TABLE bank
    DROP INDEX short_name;

ALTER TABLE bank
    ADD UNIQUE INDEX uq_bank_code_active (bank_code, (IF(is_deleted, NULL, 1)));
ALTER TABLE bank
    ADD UNIQUE INDEX uq_bank_source_active (source_id, (IF(is_deleted, NULL, 1)));
ALTER TABLE bank
    ADD UNIQUE INDEX uq_bank_name_active (name, (IF(is_deleted, NULL, 1)));
ALTER TABLE bank
    ADD UNIQUE INDEX uq_bank_short_active (short_name, (IF(is_deleted, NULL, 1)));

-- Table: blood_group
ALTER TABLE blood_group
    DROP INDEX blood_group_code;
ALTER TABLE blood_group
    DROP INDEX source_id;
ALTER TABLE blood_group
    DROP INDEX name;

ALTER TABLE blood_group
    ADD UNIQUE INDEX uq_bg_code_active (blood_group_code, (IF(is_deleted, NULL, 1)));
ALTER TABLE blood_group
    ADD UNIQUE INDEX uq_bg_source_active (source_id, (IF(is_deleted, NULL, 1)));
ALTER TABLE blood_group
    ADD UNIQUE INDEX uq_bg_name_active (name, (IF(is_deleted, NULL, 1)));

-- Table: country
ALTER TABLE country
    DROP INDEX country_code;
ALTER TABLE country
    DROP INDEX source_id;

ALTER TABLE country
    ADD UNIQUE INDEX uq_country_code_active (country_code, (IF(is_deleted, NULL, 1)));
ALTER TABLE country
    ADD UNIQUE INDEX uq_country_source_active (source_id, (IF(is_deleted, NULL, 1)));
ALTER TABLE country
    ADD UNIQUE INDEX uq_country_name_active (name, (IF(is_deleted, NULL, 1)));

-- Table: nationality
ALTER TABLE nationality
    DROP INDEX nationality_code;
ALTER TABLE nationality
    DROP INDEX source_id;
ALTER TABLE nationality
    DROP INDEX name;

ALTER TABLE nationality
    ADD UNIQUE INDEX uq_nat_code_active (nationality_code, (IF(is_deleted, NULL, 1)));
ALTER TABLE nationality
    ADD UNIQUE INDEX uq_nat_source_active (source_id, (IF(is_deleted, NULL, 1)));
ALTER TABLE nationality
    ADD UNIQUE INDEX uq_nat_name_active (name, (IF(is_deleted, NULL, 1)));


-- ==========================================================
-- 3. ORGANIZATION & PARTNERS
-- ==========================================================
-- Table: business_partner
ALTER TABLE business_partner
    DROP INDEX business_partner_code;
ALTER TABLE business_partner
    DROP INDEX source_id;
ALTER TABLE business_partner
    DROP INDEX short_name;

ALTER TABLE business_partner
    ADD UNIQUE INDEX uq_bp_code_active (business_partner_code, (IF(is_deleted, NULL, 1)));
ALTER TABLE business_partner
    ADD UNIQUE INDEX uq_bp_source_active (source_id, (IF(is_deleted, NULL, 1)));
ALTER TABLE business_partner
    ADD UNIQUE INDEX uq_bp_short_active (short_name, (IF(is_deleted, NULL, 1)));

-- Table: business_partner_group
ALTER TABLE business_partner_group
    DROP INDEX business_partner_group_code;
ALTER TABLE business_partner_group
    DROP INDEX source_id;

ALTER TABLE business_partner_group
    ADD UNIQUE INDEX uq_bpg_code_active (business_partner_group_code, (IF(is_deleted, NULL, 1)));
ALTER TABLE business_partner_group
    ADD UNIQUE INDEX uq_bpg_source_active (source_id, (IF(is_deleted, NULL, 1)));
ALTER TABLE business_partner_group
    ADD UNIQUE INDEX uq_bpg_name_active (name, (IF(is_deleted, NULL, 1)));

-- Table: department
ALTER TABLE department
    DROP INDEX department_code;
ALTER TABLE department
    DROP INDEX source_id;
ALTER TABLE department
    DROP INDEX name;

ALTER TABLE department
    ADD UNIQUE INDEX uq_dept_code_active (department_code, (IF(is_deleted, NULL, 1)));
ALTER TABLE department
    ADD UNIQUE INDEX uq_dept_source_active (source_id, (IF(is_deleted, NULL, 1)));
ALTER TABLE department
    ADD UNIQUE INDEX uq_dept_name_active (name, (IF(is_deleted, NULL, 1)));


-- ==========================================================
-- 4. EMPLOYEE & HR ATTRIBUTES
-- ==========================================================
-- Table: employee
-- Employee table has many sensitive unique keys that need this logic
ALTER TABLE employee
    DROP INDEX employee_code;
ALTER TABLE employee
    DROP INDEX source_id;
ALTER TABLE employee
    DROP INDEX corporation_code;
ALTER TABLE employee
    DROP INDEX tax_code;
ALTER TABLE employee
    DROP INDEX social_insurance_no;
ALTER TABLE employee
    DROP INDEX social_insurance_code;
ALTER TABLE employee
    DROP INDEX health_insurance_card;

ALTER TABLE employee
    ADD UNIQUE INDEX uq_emp_code_active (employee_code, (IF(is_deleted, NULL, 1)));
ALTER TABLE employee
    ADD UNIQUE INDEX uq_emp_source_active (source_id, (IF(is_deleted, NULL, 1)));
ALTER TABLE employee
    ADD UNIQUE INDEX uq_emp_corp_code_active (corporation_code, (IF(is_deleted, NULL, 1)));
ALTER TABLE employee
    ADD UNIQUE INDEX uq_emp_tax_active (tax_code, (IF(is_deleted, NULL, 1)));
ALTER TABLE employee
    ADD UNIQUE INDEX uq_emp_social_no_active (social_insurance_no, (IF(is_deleted, NULL, 1)));
ALTER TABLE employee
    ADD UNIQUE INDEX uq_emp_social_code_active (social_insurance_code, (IF(is_deleted, NULL, 1)));
ALTER TABLE employee
    ADD UNIQUE INDEX uq_emp_health_card_active (health_insurance_card, (IF(is_deleted, NULL, 1)));

-- Table: employee_type
ALTER TABLE employee_type
    DROP INDEX employee_type_code;
ALTER TABLE employee_type
    DROP INDEX source_id;
ALTER TABLE employee_type
    DROP INDEX name;

ALTER TABLE employee_type
    ADD UNIQUE INDEX uq_empt_code_active (employee_type_code, (IF(is_deleted, NULL, 1)));
ALTER TABLE employee_type
    ADD UNIQUE INDEX uq_empt_source_active (source_id, (IF(is_deleted, NULL, 1)));
ALTER TABLE employee_type
    ADD UNIQUE INDEX uq_empt_name_active (name, (IF(is_deleted, NULL, 1)));

-- Table: labor_status
ALTER TABLE labor_status
    DROP INDEX labor_status_code;
ALTER TABLE labor_status
    DROP INDEX source_id;
ALTER TABLE labor_status
    DROP INDEX name;

ALTER TABLE labor_status
    ADD UNIQUE INDEX uq_ls_code_active (labor_status_code, (IF(is_deleted, NULL, 1)));
ALTER TABLE labor_status
    ADD UNIQUE INDEX uq_ls_source_active (source_id, (IF(is_deleted, NULL, 1)));
ALTER TABLE labor_status
    ADD UNIQUE INDEX uq_ls_name_active (name, (IF(is_deleted, NULL, 1)));

-- Table: marital_status
ALTER TABLE marital_status
    DROP INDEX marital_status_code;
ALTER TABLE marital_status
    DROP INDEX source_id;
ALTER TABLE marital_status
    DROP INDEX name;

ALTER TABLE marital_status
    ADD UNIQUE INDEX uq_ms_code_active (marital_status_code, (IF(is_deleted, NULL, 1)));
ALTER TABLE marital_status
    ADD UNIQUE INDEX uq_ms_source_active (source_id, (IF(is_deleted, NULL, 1)));
ALTER TABLE marital_status
    ADD UNIQUE INDEX uq_ms_name_active (name, (IF(is_deleted, NULL, 1)));


-- ==========================================================
-- 5. JOB & EDUCATION
-- ==========================================================
-- Table: job_position
ALTER TABLE job_position
    DROP INDEX position_code;
ALTER TABLE job_position
    DROP INDEX source_id;
ALTER TABLE job_position
    DROP INDEX name;

ALTER TABLE job_position
    ADD UNIQUE INDEX uq_jpos_code_active (position_code, (IF(is_deleted, NULL, 1)));
ALTER TABLE job_position
    ADD UNIQUE INDEX uq_jpos_source_active (source_id, (IF(is_deleted, NULL, 1)));
ALTER TABLE job_position
    ADD UNIQUE INDEX uq_jpos_name_active (name, (IF(is_deleted, NULL, 1)));

-- Table: job_rank
ALTER TABLE job_rank
    DROP INDEX job_rank_code;
ALTER TABLE job_rank
    DROP INDEX source_id;
ALTER TABLE job_rank
    DROP INDEX name;

ALTER TABLE job_rank
    ADD UNIQUE INDEX uq_jrank_code_active (job_rank_code, (IF(is_deleted, NULL, 1)));
ALTER TABLE job_rank
    ADD UNIQUE INDEX uq_jrank_source_active (source_id, (IF(is_deleted, NULL, 1)));
ALTER TABLE job_rank
    ADD UNIQUE INDEX uq_jrank_name_active (name, (IF(is_deleted, NULL, 1)));

-- Table: job_title
ALTER TABLE job_title
    DROP INDEX job_title_code;
ALTER TABLE job_title
    DROP INDEX source_id;
ALTER TABLE job_title
    DROP INDEX name;

ALTER TABLE job_title
    ADD UNIQUE INDEX uq_jtitle_code_active (job_title_code, (IF(is_deleted, NULL, 1)));
ALTER TABLE job_title
    ADD UNIQUE INDEX uq_jtitle_source_active (source_id, (IF(is_deleted, NULL, 1)));
ALTER TABLE job_title
    ADD UNIQUE INDEX uq_jtitle_name_active (name, (IF(is_deleted, NULL, 1)));

-- Table: education_level
ALTER TABLE education_level
    DROP INDEX education_level_code;
ALTER TABLE education_level
    DROP INDEX source_id;
ALTER TABLE education_level
    DROP INDEX name;

ALTER TABLE education_level
    ADD UNIQUE INDEX uq_edu_code_active (education_level_code, (IF(is_deleted, NULL, 1)));
ALTER TABLE education_level
    ADD UNIQUE INDEX uq_edu_source_active (source_id, (IF(is_deleted, NULL, 1)));
ALTER TABLE education_level
    ADD UNIQUE INDEX uq_edu_name_active (name, (IF(is_deleted, NULL, 1)));

-- Table: major
ALTER TABLE major
    DROP INDEX major_code;
ALTER TABLE major
    DROP INDEX source_id;
ALTER TABLE major
    DROP INDEX name;

ALTER TABLE major
    ADD UNIQUE INDEX uq_major_code_active (major_code, (IF(is_deleted, NULL, 1)));
ALTER TABLE major
    ADD UNIQUE INDEX uq_major_source_active (source_id, (IF(is_deleted, NULL, 1)));
ALTER TABLE major
    ADD UNIQUE INDEX uq_major_name_active (name, (IF(is_deleted, NULL, 1)));

-- Table: specialization
ALTER TABLE specialization
    DROP INDEX specialization_code;
ALTER TABLE specialization
    DROP INDEX source_id;
ALTER TABLE specialization
    DROP INDEX name;

ALTER TABLE specialization
    ADD UNIQUE INDEX uq_spec_code_active (specialization_code, (IF(is_deleted, NULL, 1)));
ALTER TABLE specialization
    ADD UNIQUE INDEX uq_spec_source_active (source_id, (IF(is_deleted, NULL, 1)));
ALTER TABLE specialization
    ADD UNIQUE INDEX uq_spec_name_active (name, (IF(is_deleted, NULL, 1)));

-- Table: school
ALTER TABLE school
    DROP INDEX school_code;
ALTER TABLE school
    DROP INDEX source_id;
ALTER TABLE school
    DROP INDEX name;

ALTER TABLE school
    ADD UNIQUE INDEX uq_school_code_active (school_code, (IF(is_deleted, NULL, 1)));
ALTER TABLE school
    ADD UNIQUE INDEX uq_school_source_active (source_id, (IF(is_deleted, NULL, 1)));
ALTER TABLE school
    ADD UNIQUE INDEX uq_school_name_active (name, (IF(is_deleted, NULL, 1)));

-- Table: language
ALTER TABLE language
    DROP INDEX language_code;
ALTER TABLE language
    DROP INDEX source_id;
ALTER TABLE language
    DROP INDEX name;

ALTER TABLE language
    ADD UNIQUE INDEX uq_lang_code_active (language_code, (IF(is_deleted, NULL, 1)));
ALTER TABLE language
    ADD UNIQUE INDEX uq_lang_source_active (source_id, (IF(is_deleted, NULL, 1)));
ALTER TABLE language
    ADD UNIQUE INDEX uq_lang_name_active (name, (IF(is_deleted, NULL, 1)));


-- ==========================================================
-- 6. LOCATION & ADMINISTRATIVE
-- ==========================================================
-- Table: province_city
ALTER TABLE province_city
    DROP INDEX source_id;
ALTER TABLE province_city
    DROP INDEX name;

ALTER TABLE province_city
    ADD UNIQUE INDEX uq_pc_source_active (source_id, (IF(is_deleted, NULL, 1)));
ALTER TABLE province_city
    ADD UNIQUE INDEX uq_pc_name_active (name, (IF(is_deleted, NULL, 1)));

-- Table: old_province_city
ALTER TABLE old_province_city
    DROP INDEX source_id;
ALTER TABLE old_province_city
    DROP INDEX name;

ALTER TABLE old_province_city
    ADD UNIQUE INDEX uq_oldpc_source_active (source_id, (IF(is_deleted, NULL, 1)));
ALTER TABLE old_province_city
    ADD UNIQUE INDEX uq_oldpc_name_active (name, (IF(is_deleted, NULL, 1)));

-- Table: ward
ALTER TABLE ward
    DROP INDEX source_id;
ALTER TABLE ward
    DROP INDEX name;

ALTER TABLE ward
    ADD UNIQUE INDEX uq_ward_source_active (source_id, (IF(is_deleted, NULL, 1)));
ALTER TABLE ward
    ADD UNIQUE INDEX uq_ward_name_active (name, (IF(is_deleted, NULL, 1)));

-- Table: old_ward
ALTER TABLE old_ward
    DROP INDEX source_id;
ALTER TABLE old_ward
    DROP INDEX name;

ALTER TABLE old_ward
    ADD UNIQUE INDEX uq_oldward_source_active (source_id, (IF(is_deleted, NULL, 1)));
ALTER TABLE old_ward
    ADD UNIQUE INDEX uq_oldward_name_active (name, (IF(is_deleted, NULL, 1)));

-- Table: old_district
ALTER TABLE old_district
    DROP INDEX source_id;
ALTER TABLE old_district
    DROP INDEX name;

ALTER TABLE old_district
    ADD UNIQUE INDEX uq_olddist_source_active (source_id, (IF(is_deleted, NULL, 1)));
ALTER TABLE old_district
    ADD UNIQUE INDEX uq_olddist_name_active (name, (IF(is_deleted, NULL, 1)));

-- Table: work_location
ALTER TABLE work_location
    DROP INDEX work_location_code;
ALTER TABLE work_location
    DROP INDEX source_id;
ALTER TABLE work_location
    DROP INDEX name;

ALTER TABLE work_location
    ADD UNIQUE INDEX uq_wl_code_active (work_location_code, (IF(is_deleted, NULL, 1)));
ALTER TABLE work_location
    ADD UNIQUE INDEX uq_wl_source_active (source_id, (IF(is_deleted, NULL, 1)));
ALTER TABLE work_location
    ADD UNIQUE INDEX uq_wl_name_active (name, (IF(is_deleted, NULL, 1)));


-- ==========================================================
-- 7. MISC TYPES & CONFIGURATIONS
-- ==========================================================
-- Table: expense_type
ALTER TABLE expense_type
    DROP INDEX expense_type_code;
ALTER TABLE expense_type
    DROP INDEX source_id;
ALTER TABLE expense_type
    DROP INDEX name;

ALTER TABLE expense_type
    ADD UNIQUE INDEX uq_exp_code_active (expense_type_code, (IF(is_deleted, NULL, 1)));
ALTER TABLE expense_type
    ADD UNIQUE INDEX uq_exp_source_active (source_id, (IF(is_deleted, NULL, 1)));
ALTER TABLE expense_type
    ADD UNIQUE INDEX uq_exp_name_active (name, (IF(is_deleted, NULL, 1)));

-- Table: identity_issuing_authority
ALTER TABLE identity_issuing_authority
    DROP INDEX source_id;

ALTER TABLE identity_issuing_authority
    ADD UNIQUE INDEX uq_iia_source_active (source_id, (IF(is_deleted, NULL, 1)));
ALTER TABLE identity_issuing_authority
    ADD UNIQUE INDEX uq_iia_name_active (name, (IF(is_deleted, NULL, 1)));

-- Table: medical_facility
ALTER TABLE medical_facility
    DROP INDEX medical_facility_code;
ALTER TABLE medical_facility
    DROP INDEX source_id;
ALTER TABLE medical_facility
    DROP INDEX name;

ALTER TABLE medical_facility
    ADD UNIQUE INDEX uq_mf_code_active (medical_facility_code, (IF(is_deleted, NULL, 1)));
ALTER TABLE medical_facility
    ADD UNIQUE INDEX uq_mf_source_active (source_id, (IF(is_deleted, NULL, 1)));
ALTER TABLE medical_facility
    ADD UNIQUE INDEX uq_mf_name_active (name, (IF(is_deleted, NULL, 1)));

-- Table: ot_type
ALTER TABLE ot_type
    DROP INDEX ot_type_code;
ALTER TABLE ot_type
    DROP INDEX source_id;
ALTER TABLE ot_type
    DROP INDEX name;

ALTER TABLE ot_type
    ADD UNIQUE INDEX uq_ott_code_active (ot_type_code, (IF(is_deleted, NULL, 1)));
ALTER TABLE ot_type
    ADD UNIQUE INDEX uq_ott_source_active (source_id, (IF(is_deleted, NULL, 1)));
ALTER TABLE ot_type
    ADD UNIQUE INDEX uq_ott_name_active (name, (IF(is_deleted, NULL, 1)));

-- Table: work_shift
ALTER TABLE work_shift
    DROP INDEX work_shift_code;
ALTER TABLE work_shift
    DROP INDEX source_id;
ALTER TABLE work_shift
    DROP INDEX name;

ALTER TABLE work_shift
    ADD UNIQUE INDEX uq_ws_code_active (work_shift_code, (IF(is_deleted, NULL, 1)));
ALTER TABLE work_shift
    ADD UNIQUE INDEX uq_ws_source_active (source_id, (IF(is_deleted, NULL, 1)));
ALTER TABLE work_shift
    ADD UNIQUE INDEX uq_ws_name_active (name, (IF(is_deleted, NULL, 1)));

-- Table: work_shift_group
ALTER TABLE work_shift_group
    DROP INDEX work_shift_group_code;
ALTER TABLE work_shift_group
    DROP INDEX source_id;
ALTER TABLE work_shift_group
    DROP INDEX name;

ALTER TABLE work_shift_group
    ADD UNIQUE INDEX uq_wsg_code_active (work_shift_group_code, (IF(is_deleted, NULL, 1)));
ALTER TABLE work_shift_group
    ADD UNIQUE INDEX uq_wsg_source_active (source_id, (IF(is_deleted, NULL, 1)));
ALTER TABLE work_shift_group
    ADD UNIQUE INDEX uq_wsg_name_active (name, (IF(is_deleted, NULL, 1)));