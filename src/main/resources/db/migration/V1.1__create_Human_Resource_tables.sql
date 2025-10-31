-- =====================================================================
-- DOMAIN: GENERAL
-- =====================================================================

CREATE TABLE source_system
(
    source_system_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name             VARCHAR(100) NOT NULL UNIQUE,
    description      VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE user_profile
(
    user_profile_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id     BIGINT, -- FK tới employee (tạo sau, tùy nhu cầu)
    email           VARCHAR(150) NOT NULL,
    full_name       VARCHAR(150),
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE country
(
    country_id       BIGINT PRIMARY KEY AUTO_INCREMENT,
    country_code     VARCHAR(50) UNIQUE,
    source_id        VARCHAR(100) UNIQUE,
    name             VARCHAR(255) NOT NULL,
    source_system_id BIGINT       NOT NULL,
    is_deleted       BOOLEAN  DEFAULT FALSE,
    created_by       BIGINT,
    updated_by       BIGINT,
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_country_source_system
        FOREIGN KEY (source_system_id) REFERENCES source_system (source_system_id),
    CONSTRAINT fk_country_created_by
        FOREIGN KEY (created_by) REFERENCES user_profile (user_profile_id),
    CONSTRAINT fk_country_updated_by
        FOREIGN KEY (updated_by) REFERENCES user_profile (user_profile_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================================
-- VALUE LISTS (bảng mã)
-- =====================================================================

CREATE TABLE decision_type
(
    decision_type_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code             VARCHAR(10)  NOT NULL UNIQUE,
    name             VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO decision_type(code, name)
VALUES ('TD', 'Tuyển dụng'),
       ('BN', 'Bổ nhiệm'),
       ('DC', 'Điều chuyển');

CREATE TABLE document_type
(
    document_type_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code             VARCHAR(20)  NOT NULL UNIQUE,
    name             VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO document_type(code, name)
VALUES ('CMND', 'Chứng minh nhân dân'),
       ('CCCD', 'Căn cước công dân'),
       ('PASSPORT', 'Hộ chiếu');


CREATE TABLE tax_number_category
(
    tax_number_category_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code                   VARCHAR(20)  NOT NULL UNIQUE,
    name                   VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO tax_number_category(code, name)
VALUES ('VN0', 'MST'),
       ('VN1', 'CMND/ CCCD');
-- =====================================================================
-- DOMAIN: HUMAN RESOURCE - DANH MỤC CƠ BẢN
-- =====================================================================

-- Một số bảng tham chiếu chung
CREATE TABLE gender
(
    gender_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name      VARCHAR(20) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE province_city
(
    province_city_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    source_id        VARCHAR(100) UNIQUE,
    name             VARCHAR(255) NOT NULL UNIQUE,
    source_system_id BIGINT       NOT NULL,
    is_deleted       BOOLEAN  DEFAULT FALSE,
    created_by       BIGINT,
    updated_by       BIGINT,
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_pc_source_system
        FOREIGN KEY (source_system_id) REFERENCES source_system (source_system_id),
    CONSTRAINT fk_pc_created_by
        FOREIGN KEY (created_by) REFERENCES user_profile (user_profile_id),
    CONSTRAINT fk_pc_updated_by
        FOREIGN KEY (updated_by) REFERENCES user_profile (user_profile_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE ward
(
    ward_id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    source_id        VARCHAR(100) UNIQUE,
    province_city_id BIGINT       NOT NULL,
    name             VARCHAR(255) NOT NULL UNIQUE,
    source_system_id BIGINT       NOT NULL,
    is_deleted       BOOLEAN  DEFAULT FALSE,
    created_by       BIGINT,
    updated_by       BIGINT,
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_ward_pc
        FOREIGN KEY (province_city_id) REFERENCES province_city (province_city_id),
    CONSTRAINT fk_ward_source_system
        FOREIGN KEY (source_system_id) REFERENCES source_system (source_system_id),
    CONSTRAINT fk_ward_created_by
        FOREIGN KEY (created_by) REFERENCES user_profile (user_profile_id),
    CONSTRAINT fk_ward_updated_by
        FOREIGN KEY (updated_by) REFERENCES user_profile (user_profile_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE bank
(
    bank_id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    bank_code        VARCHAR(50) UNIQUE,
    source_id        VARCHAR(100) UNIQUE,
    name             VARCHAR(255) NOT NULL UNIQUE,
    short_name       VARCHAR(100) UNIQUE,
    source_system_id BIGINT       NOT NULL,
    is_deleted       BOOLEAN  DEFAULT FALSE,
    created_by       BIGINT,
    updated_by       BIGINT,
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_bank_source_system
        FOREIGN KEY (source_system_id) REFERENCES source_system (source_system_id),
    CONSTRAINT fk_bank_created_by
        FOREIGN KEY (created_by) REFERENCES user_profile (user_profile_id),
    CONSTRAINT fk_bank_updated_by
        FOREIGN KEY (updated_by) REFERENCES user_profile (user_profile_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE blood_group
(
    blood_group_id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    blood_group_code VARCHAR(50) UNIQUE,
    source_id        VARCHAR(100) UNIQUE,
    name             VARCHAR(50) NOT NULL UNIQUE,
    source_system_id BIGINT      NOT NULL,
    is_deleted       BOOLEAN  DEFAULT FALSE,
    created_by       BIGINT,
    updated_by       BIGINT,
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_bg_source_system
        FOREIGN KEY (source_system_id) REFERENCES source_system (source_system_id),
    CONSTRAINT fk_bg_created_by
        FOREIGN KEY (created_by) REFERENCES user_profile (user_profile_id),
    CONSTRAINT fk_bg_updated_by
        FOREIGN KEY (updated_by) REFERENCES user_profile (user_profile_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE language
(
    language_id      BIGINT PRIMARY KEY AUTO_INCREMENT,
    language_code    VARCHAR(50)  NOT NULL UNIQUE,
    source_id        VARCHAR(100) UNIQUE,
    name             VARCHAR(255) NOT NULL UNIQUE,
    source_system_id BIGINT       NOT NULL,
    is_deleted       BOOLEAN  DEFAULT FALSE,
    created_by       BIGINT,
    updated_by       BIGINT,
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_lang_source_system
        FOREIGN KEY (source_system_id) REFERENCES source_system (source_system_id),
    CONSTRAINT fk_lang_created_by
        FOREIGN KEY (created_by) REFERENCES user_profile (user_profile_id),
    CONSTRAINT fk_lang_updated_by
        FOREIGN KEY (updated_by) REFERENCES user_profile (user_profile_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE nationality
(
    nationality_id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    nationality_code VARCHAR(50) UNIQUE,
    source_id        VARCHAR(100) UNIQUE,
    name             VARCHAR(100) NOT NULL UNIQUE,
    source_system_id BIGINT       NOT NULL,
    is_deleted       BOOLEAN  DEFAULT FALSE,
    created_by       BIGINT,
    updated_by       BIGINT,
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_nat_source_system
        FOREIGN KEY (source_system_id) REFERENCES source_system (source_system_id),
    CONSTRAINT fk_nat_created_by
        FOREIGN KEY (created_by) REFERENCES user_profile (user_profile_id),
    CONSTRAINT fk_nat_updated_by
        FOREIGN KEY (updated_by) REFERENCES user_profile (user_profile_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE marital_status
(
    marital_status_id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    marital_status_code VARCHAR(50) UNIQUE,
    source_id           VARCHAR(100) UNIQUE,
    name                VARCHAR(100) NOT NULL UNIQUE,
    source_system_id    BIGINT       NOT NULL,
    is_deleted          BOOLEAN  DEFAULT FALSE,
    created_by          BIGINT,
    updated_by          BIGINT,
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_ms_source_system
        FOREIGN KEY (source_system_id) REFERENCES source_system (source_system_id),
    CONSTRAINT fk_ms_created_by
        FOREIGN KEY (created_by) REFERENCES user_profile (user_profile_id),
    CONSTRAINT fk_ms_updated_by
        FOREIGN KEY (updated_by) REFERENCES user_profile (user_profile_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE labor_status
(
    labor_status_id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    labor_status_code VARCHAR(50) UNIQUE,
    source_id         VARCHAR(100) UNIQUE,
    name              VARCHAR(255) NOT NULL UNIQUE,
    source_system_id  BIGINT       NOT NULL,
    is_deleted        BOOLEAN  DEFAULT FALSE,
    created_by        BIGINT,
    updated_by        BIGINT,
    created_at        DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_ls_source_system
        FOREIGN KEY (source_system_id) REFERENCES source_system (source_system_id),
    CONSTRAINT fk_ls_created_by
        FOREIGN KEY (created_by) REFERENCES user_profile (user_profile_id),
    CONSTRAINT fk_ls_updated_by
        FOREIGN KEY (updated_by) REFERENCES user_profile (user_profile_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE job_rank
(
    job_rank_id      BIGINT PRIMARY KEY AUTO_INCREMENT,
    job_rank_code    VARCHAR(50) UNIQUE,
    source_id        VARCHAR(100) UNIQUE,
    name             VARCHAR(255) NOT NULL UNIQUE,
    source_system_id BIGINT       NOT NULL,
    is_deleted       BOOLEAN  DEFAULT FALSE,
    created_by       BIGINT,
    updated_by       BIGINT,
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_jr_source_system
        FOREIGN KEY (source_system_id) REFERENCES source_system (source_system_id),
    CONSTRAINT fk_jr_created_by
        FOREIGN KEY (created_by) REFERENCES user_profile (user_profile_id),
    CONSTRAINT fk_jr_updated_by
        FOREIGN KEY (updated_by) REFERENCES user_profile (user_profile_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE job_title
(
    job_title_id     BIGINT PRIMARY KEY AUTO_INCREMENT,
    job_title_code   VARCHAR(50) UNIQUE,
    source_id        VARCHAR(100) UNIQUE,
    name             VARCHAR(255) NOT NULL UNIQUE,
    source_system_id BIGINT       NOT NULL,
    is_deleted       BOOLEAN  DEFAULT FALSE,
    created_by       BIGINT,
    updated_by       BIGINT,
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_jt_source_system
        FOREIGN KEY (source_system_id) REFERENCES source_system (source_system_id),
    CONSTRAINT fk_jt_created_by
        FOREIGN KEY (created_by) REFERENCES user_profile (user_profile_id),
    CONSTRAINT fk_jt_updated_by
        FOREIGN KEY (updated_by) REFERENCES user_profile (user_profile_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE department
(
    department_id    BIGINT PRIMARY KEY AUTO_INCREMENT,
    department_code  VARCHAR(50)  NOT NULL UNIQUE,
    source_id        VARCHAR(100) UNIQUE,
    name             VARCHAR(255) NOT NULL UNIQUE,
    source_system_id BIGINT       NOT NULL,
    is_deleted       BOOLEAN  DEFAULT FALSE,
    created_by       BIGINT,
    updated_by       BIGINT,
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_dept_source_system
        FOREIGN KEY (source_system_id) REFERENCES source_system (source_system_id),
    CONSTRAINT fk_dept_created_by
        FOREIGN KEY (created_by) REFERENCES user_profile (user_profile_id),
    CONSTRAINT fk_dept_updated_by
        FOREIGN KEY (updated_by) REFERENCES user_profile (user_profile_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE job_position
(
    position_id      BIGINT PRIMARY KEY AUTO_INCREMENT,
    position_code    VARCHAR(50) UNIQUE,
    source_id        VARCHAR(100) UNIQUE,
    name             VARCHAR(255) NOT NULL UNIQUE,
    source_system_id BIGINT       NOT NULL,
    is_deleted       BOOLEAN  DEFAULT FALSE,
    created_by       BIGINT,
    updated_by       BIGINT,
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_pos_source_system
        FOREIGN KEY (source_system_id) REFERENCES source_system (source_system_id),
    CONSTRAINT fk_pos_created_by
        FOREIGN KEY (created_by) REFERENCES user_profile (user_profile_id),
    CONSTRAINT fk_pos_updated_by
        FOREIGN KEY (updated_by) REFERENCES user_profile (user_profile_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE education_level
(
    education_level_id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    education_level_code VARCHAR(50) UNIQUE,
    source_id            VARCHAR(100) UNIQUE,
    name                 VARCHAR(255) NOT NULL UNIQUE,
    source_system_id     BIGINT       NOT NULL,
    is_deleted           BOOLEAN  DEFAULT FALSE,
    created_by           BIGINT,
    updated_by           BIGINT,
    created_at           DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at           DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_edlvl_source_system
        FOREIGN KEY (source_system_id) REFERENCES source_system (source_system_id),
    CONSTRAINT fk_edlvl_created_by
        FOREIGN KEY (created_by) REFERENCES user_profile (user_profile_id),
    CONSTRAINT fk_edlvl_updated_by
        FOREIGN KEY (updated_by) REFERENCES user_profile (user_profile_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE school
(
    school_id        BIGINT PRIMARY KEY AUTO_INCREMENT,
    school_code      VARCHAR(50) UNIQUE,
    source_id        VARCHAR(100) UNIQUE,
    name             VARCHAR(255) NOT NULL UNIQUE,
    source_system_id BIGINT       NOT NULL,
    is_deleted       BOOLEAN  DEFAULT FALSE,
    created_by       BIGINT,
    updated_by       BIGINT,
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_school_source_system
        FOREIGN KEY (source_system_id) REFERENCES source_system (source_system_id),
    CONSTRAINT fk_school_created_by
        FOREIGN KEY (created_by) REFERENCES user_profile (user_profile_id),
    CONSTRAINT fk_school_updated_by
        FOREIGN KEY (updated_by) REFERENCES user_profile (user_profile_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE major
(
    major_id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    major_code       VARCHAR(50) UNIQUE,
    source_id        VARCHAR(100) UNIQUE,
    name             VARCHAR(255) NOT NULL UNIQUE,
    source_system_id BIGINT       NOT NULL,
    is_deleted       BOOLEAN  DEFAULT FALSE,
    created_by       BIGINT,
    updated_by       BIGINT,
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_major_source_system
        FOREIGN KEY (source_system_id) REFERENCES source_system (source_system_id),
    CONSTRAINT fk_major_created_by
        FOREIGN KEY (created_by) REFERENCES user_profile (user_profile_id),
    CONSTRAINT fk_major_updated_by
        FOREIGN KEY (updated_by) REFERENCES user_profile (user_profile_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE specialization
(
    specialization_id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    specialization_code VARCHAR(50) UNIQUE,
    source_id           VARCHAR(100) UNIQUE,
    name                VARCHAR(255) NOT NULL UNIQUE,
    source_system_id    BIGINT       NOT NULL,
    is_deleted          BOOLEAN  DEFAULT FALSE,
    created_by          BIGINT,
    updated_by          BIGINT,
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_spec_source_system
        FOREIGN KEY (source_system_id) REFERENCES source_system (source_system_id),
    CONSTRAINT fk_spec_created_by
        FOREIGN KEY (created_by) REFERENCES user_profile (user_profile_id),
    CONSTRAINT fk_spec_updated_by
        FOREIGN KEY (updated_by) REFERENCES user_profile (user_profile_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE work_shift_group
(
    work_shift_group_id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    work_shift_group_code VARCHAR(50) UNIQUE,
    source_id             VARCHAR(100) UNIQUE,
    name                  VARCHAR(255) NOT NULL UNIQUE,
    source_system_id      BIGINT       NOT NULL,
    is_deleted            BOOLEAN  DEFAULT FALSE,
    created_by            BIGINT,
    updated_by            BIGINT,
    created_at            DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at            DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_wsg_source_system
        FOREIGN KEY (source_system_id) REFERENCES source_system (source_system_id),
    CONSTRAINT fk_wsg_created_by
        FOREIGN KEY (created_by) REFERENCES user_profile (user_profile_id),
    CONSTRAINT fk_wsg_updated_by
        FOREIGN KEY (updated_by) REFERENCES user_profile (user_profile_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE work_shift
(
    work_shift_id    BIGINT PRIMARY KEY AUTO_INCREMENT,
    work_shift_code  VARCHAR(50) UNIQUE,
    source_id        VARCHAR(100) UNIQUE,
    name             VARCHAR(255) NOT NULL UNIQUE,
    source_system_id BIGINT       NOT NULL,
    is_deleted       BOOLEAN  DEFAULT FALSE,
    created_by       BIGINT,
    updated_by       BIGINT,
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_ws_source_system
        FOREIGN KEY (source_system_id) REFERENCES source_system (source_system_id),
    CONSTRAINT fk_ws_created_by
        FOREIGN KEY (created_by) REFERENCES user_profile (user_profile_id),
    CONSTRAINT fk_ws_updated_by
        FOREIGN KEY (updated_by) REFERENCES user_profile (user_profile_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE attendance_type
(
    attendance_type_id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    attendance_type_code VARCHAR(50) UNIQUE,
    source_id            VARCHAR(100) UNIQUE,
    name                 VARCHAR(255) NOT NULL UNIQUE,
    source_system_id     BIGINT       NOT NULL,
    is_deleted           BOOLEAN  DEFAULT FALSE,
    created_by           BIGINT,
    updated_by           BIGINT,
    created_at           DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at           DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_att_type_source_system
        FOREIGN KEY (source_system_id) REFERENCES source_system (source_system_id),
    CONSTRAINT fk_att_type_created_by
        FOREIGN KEY (created_by) REFERENCES user_profile (user_profile_id),
    CONSTRAINT fk_att_type_updated_by
        FOREIGN KEY (updated_by) REFERENCES user_profile (user_profile_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE ot_type
(
    ot_type_id       BIGINT PRIMARY KEY AUTO_INCREMENT,
    ot_type_code     VARCHAR(50) UNIQUE,
    source_id        VARCHAR(100) UNIQUE,
    name             VARCHAR(255) NOT NULL UNIQUE,
    source_system_id BIGINT       NOT NULL,
    is_deleted       BOOLEAN  DEFAULT FALSE,
    created_by       BIGINT,
    updated_by       BIGINT,
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_ot_source_system
        FOREIGN KEY (source_system_id) REFERENCES source_system (source_system_id),
    CONSTRAINT fk_ot_created_by
        FOREIGN KEY (created_by) REFERENCES user_profile (user_profile_id),
    CONSTRAINT fk_ot_updated_by
        FOREIGN KEY (updated_by) REFERENCES user_profile (user_profile_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE attendance_machine
(
    attendance_machine_id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    attendance_machine_code VARCHAR(50) UNIQUE,
    source_id               VARCHAR(100) UNIQUE,
    name                    VARCHAR(255) NOT NULL UNIQUE,
    source_system_id        BIGINT       NOT NULL,
    is_deleted              BOOLEAN  DEFAULT FALSE,
    created_by              BIGINT,
    updated_by              BIGINT,
    created_at              DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at              DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_am_source_system
        FOREIGN KEY (source_system_id) REFERENCES source_system (source_system_id),
    CONSTRAINT fk_am_created_by
        FOREIGN KEY (created_by) REFERENCES user_profile (user_profile_id),
    CONSTRAINT fk_am_updated_by
        FOREIGN KEY (updated_by) REFERENCES user_profile (user_profile_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE work_location
(
    work_location_id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    work_location_code VARCHAR(50) UNIQUE,
    name               VARCHAR(255) NOT NULL UNIQUE,
    source_system_id   BIGINT       NOT NULL,
    is_deleted         BOOLEAN  DEFAULT FALSE,
    created_by         BIGINT,
    updated_by         BIGINT,
    created_at         DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at         DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_wl_source_system
        FOREIGN KEY (source_system_id) REFERENCES source_system (source_system_id),
    CONSTRAINT fk_wl_created_by
        FOREIGN KEY (created_by) REFERENCES user_profile (user_profile_id),
    CONSTRAINT fk_wl_updated_by
        FOREIGN KEY (updated_by) REFERENCES user_profile (user_profile_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE expense_type
(
    expense_type_id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    expense_type_code VARCHAR(50) UNIQUE,
    name              VARCHAR(255) NOT NULL UNIQUE,
    source_system_id  BIGINT       NOT NULL,
    is_deleted        BOOLEAN  DEFAULT FALSE,
    created_by        BIGINT,
    updated_by        BIGINT,
    created_at        DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_exp_type_source_system
        FOREIGN KEY (source_system_id) REFERENCES source_system (source_system_id),
    CONSTRAINT fk_exp_type_created_by
        FOREIGN KEY (created_by) REFERENCES user_profile (user_profile_id),
    CONSTRAINT fk_exp_type_updated_by
        FOREIGN KEY (updated_by) REFERENCES user_profile (user_profile_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE employee_type
(
    employee_type_id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_type_code VARCHAR(50) UNIQUE,
    source_id          VARCHAR(100) UNIQUE,
    name               VARCHAR(255) NOT NULL UNIQUE,
    source_system_id   BIGINT       NOT NULL,
    is_deleted         BOOLEAN  DEFAULT FALSE,
    created_by         BIGINT,
    updated_by         BIGINT,
    created_at         DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at         DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_emp_type_source_system
        FOREIGN KEY (source_system_id) REFERENCES source_system (source_system_id),
    CONSTRAINT fk_emp_type_created_by
        FOREIGN KEY (created_by) REFERENCES user_profile (user_profile_id),
    CONSTRAINT fk_emp_type_updated_by
        FOREIGN KEY (updated_by) REFERENCES user_profile (user_profile_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE medical_facility
(
    medical_facility_id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    medical_facility_code VARCHAR(50) UNIQUE,
    source_id             VARCHAR(100) UNIQUE,
    name                  VARCHAR(255) NOT NULL UNIQUE,
    province_city_id      BIGINT,
    source_system_id      BIGINT       NOT NULL,
    is_deleted            BOOLEAN  DEFAULT FALSE,
    created_by            BIGINT,
    updated_by            BIGINT,
    created_at            DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at            DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_mf_pc
        FOREIGN KEY (province_city_id) REFERENCES province_city (province_city_id),
    CONSTRAINT fk_mf_source_system
        FOREIGN KEY (source_system_id) REFERENCES source_system (source_system_id),
    CONSTRAINT fk_mf_created_by
        FOREIGN KEY (created_by) REFERENCES user_profile (user_profile_id),
    CONSTRAINT fk_mf_updated_by
        FOREIGN KEY (updated_by) REFERENCES user_profile (user_profile_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE identity_issuing_authority
(
    identity_issuing_authority_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    source_id                     VARCHAR(100) UNIQUE,
    document_type_id              BIGINT,
    name                          VARCHAR(255),
    source_system_id              BIGINT NOT NULL,
    is_deleted                    BOOLEAN  DEFAULT FALSE,
    created_by                    BIGINT,
    updated_by                    BIGINT,
    created_at                    DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at                    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_iia_doc_type
        FOREIGN KEY (document_type_id) REFERENCES document_type (document_type_id),
    CONSTRAINT fk_iia_source_system
        FOREIGN KEY (source_system_id) REFERENCES source_system (source_system_id),
    CONSTRAINT fk_iia_created_by
        FOREIGN KEY (created_by) REFERENCES user_profile (user_profile_id),
    CONSTRAINT fk_iia_updated_by
        FOREIGN KEY (updated_by) REFERENCES user_profile (user_profile_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE old_province_city
(
    old_province_city_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    province_city_id     BIGINT,
    source_id            VARCHAR(100) UNIQUE,
    name                 VARCHAR(255) NOT NULL UNIQUE,
    source_system_id     BIGINT       NOT NULL,
    is_deleted           BOOLEAN  DEFAULT FALSE,
    created_by           BIGINT,
    updated_by           BIGINT,
    created_at           DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at           DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_old_pc_pc
        FOREIGN KEY (province_city_id) REFERENCES province_city (province_city_id),
    CONSTRAINT fk_old_pc_source_system
        FOREIGN KEY (source_system_id) REFERENCES source_system (source_system_id),
    CONSTRAINT fk_old_pc_created_by
        FOREIGN KEY (created_by) REFERENCES user_profile (user_profile_id),
    CONSTRAINT fk_old_pc_updated_by
        FOREIGN KEY (updated_by) REFERENCES user_profile (user_profile_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE old_district
(
    old_district_id      BIGINT PRIMARY KEY AUTO_INCREMENT,
    ward_id              BIGINT, -- mô tả yêu cầu: tham chiếu ward (giữ cột/ FK)
    source_id            VARCHAR(100) UNIQUE,
    old_province_city_id BIGINT,
    name                 VARCHAR(255) NOT NULL UNIQUE,
    source_system_id     BIGINT       NOT NULL,
    is_deleted           BOOLEAN  DEFAULT FALSE,
    created_by           BIGINT,
    updated_by           BIGINT,
    created_at           DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at           DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_old_dist_ward
        FOREIGN KEY (ward_id) REFERENCES ward (ward_id),
    CONSTRAINT fk_old_dist_old_pc
        FOREIGN KEY (old_province_city_id) REFERENCES old_province_city (old_province_city_id),
    CONSTRAINT fk_old_dist_source_system
        FOREIGN KEY (source_system_id) REFERENCES source_system (source_system_id),
    CONSTRAINT fk_old_dist_created_by
        FOREIGN KEY (created_by) REFERENCES user_profile (user_profile_id),
    CONSTRAINT fk_old_dist_updated_by
        FOREIGN KEY (updated_by) REFERENCES user_profile (user_profile_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE old_ward
(
    old_ward_id      BIGINT PRIMARY KEY AUTO_INCREMENT,
    ward_id          BIGINT, -- tham chiếu ward mới
    source_id        VARCHAR(100) UNIQUE,
    old_district_id  BIGINT, -- đặc tả: tham chiếu “old_district”
    name             VARCHAR(255) NOT NULL UNIQUE,
    source_system_id BIGINT       NOT NULL,
    is_deleted       BOOLEAN  DEFAULT FALSE,
    created_by       BIGINT,
    updated_by       BIGINT,
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_old_ward_ward
        FOREIGN KEY (ward_id) REFERENCES ward (ward_id),
    CONSTRAINT fk_old_ward_district
        FOREIGN KEY (old_district_id) REFERENCES old_district (old_district_id),
    CONSTRAINT fk_old_ward_source_system
        FOREIGN KEY (source_system_id) REFERENCES source_system (source_system_id),
    CONSTRAINT fk_old_ward_created_by
        FOREIGN KEY (created_by) REFERENCES user_profile (user_profile_id),
    CONSTRAINT fk_old_ward_updated_by
        FOREIGN KEY (updated_by) REFERENCES user_profile (user_profile_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE business_partner_group
(
    business_partner_group_id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    business_partner_group_code VARCHAR(50) UNIQUE,
    source_id                   VARCHAR(100) NOT NULL UNIQUE,
    name                        VARCHAR(255) NOT NULL,
    description                 VARCHAR(255),
    source_system_id            BIGINT       NOT NULL,
    is_deleted                  BOOLEAN  DEFAULT FALSE,
    created_by                  BIGINT,
    updated_by                  BIGINT,
    created_at                  DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at                  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_bpg_source_system
        FOREIGN KEY (source_system_id) REFERENCES source_system (source_system_id),
    CONSTRAINT fk_bpg_created_by
        FOREIGN KEY (created_by) REFERENCES user_profile (user_profile_id),
    CONSTRAINT fk_bpg_updated_by
        FOREIGN KEY (updated_by) REFERENCES user_profile (user_profile_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE business_partner
(
    business_partner_id       BIGINT PRIMARY KEY AUTO_INCREMENT,
    business_partner_code     VARCHAR(100) NOT NULL UNIQUE,
    source_id                 VARCHAR(100) NOT NULL UNIQUE,
    business_partner_group_id BIGINT,
    short_name                VARCHAR(255) NOT NULL UNIQUE,
    full_name                 VARCHAR(255),
    title                     VARCHAR(100),
    search_term               VARCHAR(255),
    address                   VARCHAR(255),
    region_id                 BIGINT,
    country_id                BIGINT,
    phone_number              VARCHAR(50),
    mobile_phone_number       VARCHAR(50),
    fax_no                    VARCHAR(50),
    email                     VARCHAR(150),
    tax_number_category_id    BIGINT,
    tax_number                VARCHAR(100),
    id_card_issue_date        DATETIME,
    id_card_issue_place       BIGINT, -- FK -> identity_issuing_authority
    is_employee               BOOLEAN  DEFAULT FALSE,
    is_customer               BOOLEAN  DEFAULT FALSE,
    is_vendor                 BOOLEAN  DEFAULT FALSE,
    is_counterparty           BOOLEAN  DEFAULT FALSE,
    source_system_id          BIGINT       NOT NULL,
    is_deleted                BOOLEAN  DEFAULT FALSE,
    created_by                BIGINT,
    updated_by                BIGINT,
    created_at                DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at                DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_bp_group
        FOREIGN KEY (business_partner_group_id) REFERENCES business_partner_group (business_partner_group_id),
    CONSTRAINT fk_bp_region
        FOREIGN KEY (region_id) REFERENCES province_city (province_city_id),
    CONSTRAINT fk_bp_country
        FOREIGN KEY (country_id) REFERENCES country (country_id),
    CONSTRAINT fk_bp_issue_place
        FOREIGN KEY (id_card_issue_place) REFERENCES identity_issuing_authority (identity_issuing_authority_id),
    CONSTRAINT fk_bp_tax_cat
        FOREIGN KEY (tax_number_category_id) REFERENCES tax_number_category (tax_number_category_id),
    CONSTRAINT fk_bp_source_system
        FOREIGN KEY (source_system_id) REFERENCES source_system (source_system_id),
    CONSTRAINT fk_bp_created_by
        FOREIGN KEY (created_by) REFERENCES user_profile (user_profile_id),
    CONSTRAINT fk_bp_updated_by
        FOREIGN KEY (updated_by) REFERENCES user_profile (user_profile_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- =====================================================================
-- DOMAIN: HUMAN RESOURCE - BẢNG NGHIỆP VỤ
-- =====================================================================

CREATE TABLE employee
(
    employee_id                BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_code              VARCHAR(100) NOT NULL UNIQUE,
    source_id                  VARCHAR(100) NOT NULL UNIQUE,
    business_partner_id        BIGINT,
    corporation_code           VARCHAR(100) NOT NULL UNIQUE,
    full_name                  VARCHAR(255) NOT NULL,
    alternate_name             VARCHAR(255),
    birth_date                 DATETIME     NOT NULL,
    gender_id                  BIGINT,
    marital_status_id          BIGINT,
    id_number_cmnd             VARCHAR(20),
    id_issue_date_cmnd         DATETIME,
    id_issue_place_cmnd        BIGINT,
    id_number_cccd             VARCHAR(20),
    id_issue_date_cccd         DATETIME,
    id_issue_place_cccd        BIGINT,
    nationality_id             BIGINT,
    start_date                 DATETIME     NOT NULL,
    official_start_date        DATETIME,
    seniority_start_date       DATETIME,
    seniority_deduction_days   INT,
    labor_status_id            BIGINT,
    tax_code                   VARCHAR(50) UNIQUE,
    manager_id                 BIGINT,
    entitled_leave_days        INT,
    graduation_school_id       BIGINT,
    graduation_year            INT,
    language_1                 BIGINT,
    language_2                 BIGINT,
    language_3                 BIGINT,
    blood_group_id             BIGINT,
    health_status              VARCHAR(255),
    passport_number            VARCHAR(50),
    passport_expiry_date       DATETIME,
    work_permit_number         VARCHAR(50),
    work_permit_start_date     DATETIME,
    work_permit_expiry_date    DATETIME,
    temporary_residence_card   VARCHAR(50),
    temp_residence_start_date  DATETIME,
    temp_residence_expiry_date DATETIME,
    no_salary_advance          BOOLEAN               DEFAULT FALSE,
    source_system_id           BIGINT       NOT NULL,
    is_deleted                 BOOLEAN               DEFAULT FALSE,
    created_by                 BIGINT       NOT NULL,
    updated_by                 BIGINT       NOT NULL,
    created_at                 DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                 DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    current_address_street     VARCHAR(255),
    current_address_ward       BIGINT,
    permanent_address_street   VARCHAR(255) NOT NULL,
    permanent_address_ward     BIGINT       NOT NULL,
    hometown_id                BIGINT       NOT NULL,
    place_of_birth_id          BIGINT       NOT NULL,
    personal_phone             VARCHAR(20),
    home_phone                 VARCHAR(20),
    company_email              VARCHAR(255),
    personal_email             VARCHAR(255),
    emergency_contact_name     VARCHAR(255),
    emergency_contact_phone    VARCHAR(20),
    bank_account_number        VARCHAR(50),
    bank_id                    BIGINT,
    bank_branch                VARCHAR(255),
    tax_declaration_authorized BOOLEAN               DEFAULT FALSE,
    exclude_personal_deduction BOOLEAN               DEFAULT FALSE,
    termination_date           DATETIME,
    social_insurance_no        VARCHAR(50) UNIQUE,
    social_insurance_code      VARCHAR(50) UNIQUE,
    health_insurance_card      VARCHAR(50) UNIQUE,
    medical_registration       BIGINT, -- tham chiếu nơi KCB (medical_facility_id)
    CONSTRAINT fk_emp_gender FOREIGN KEY (gender_id) REFERENCES gender (gender_id),
    CONSTRAINT fk_emp_marital_status FOREIGN KEY (marital_status_id) REFERENCES marital_status (marital_status_id),
    CONSTRAINT fk_emp_nat FOREIGN KEY (nationality_id) REFERENCES nationality (nationality_id),
    CONSTRAINT fk_emp_labor_status FOREIGN KEY (labor_status_id) REFERENCES labor_status (labor_status_id),
    CONSTRAINT fk_emp_grad_school FOREIGN KEY (graduation_school_id) REFERENCES school (school_id),
    CONSTRAINT fk_emp_lang1 FOREIGN KEY (language_1) REFERENCES language (language_id),
    CONSTRAINT fk_emp_lang2 FOREIGN KEY (language_2) REFERENCES language (language_id),
    CONSTRAINT fk_emp_lang3 FOREIGN KEY (language_3) REFERENCES language (language_id),
    CONSTRAINT fk_emp_blood_group FOREIGN KEY (blood_group_id) REFERENCES blood_group (blood_group_id),
    CONSTRAINT fk_emp_source_system FOREIGN KEY (source_system_id) REFERENCES source_system (source_system_id),
    CONSTRAINT fk_emp_created_by FOREIGN KEY (created_by) REFERENCES user_profile (user_profile_id),
    CONSTRAINT fk_emp_updated_by FOREIGN KEY (updated_by) REFERENCES user_profile (user_profile_id),
    CONSTRAINT fk_emp_current_ward FOREIGN KEY (current_address_ward) REFERENCES ward (ward_id),
    CONSTRAINT fk_emp_perm_ward FOREIGN KEY (permanent_address_ward) REFERENCES ward (ward_id),
    CONSTRAINT fk_emp_hometown FOREIGN KEY (hometown_id) REFERENCES province_city (province_city_id),
    CONSTRAINT fk_emp_birth_place FOREIGN KEY (place_of_birth_id) REFERENCES province_city (province_city_id),
    CONSTRAINT fk_emp_bank FOREIGN KEY (bank_id) REFERENCES bank (bank_id),
    CONSTRAINT fk_emp_mgr FOREIGN KEY (manager_id) REFERENCES employee (employee_id),
    CONSTRAINT fk_emp_iia_cmnd FOREIGN KEY (id_issue_place_cmnd) REFERENCES identity_issuing_authority (identity_issuing_authority_id),
    CONSTRAINT fk_emp_iia_cccd FOREIGN KEY (id_issue_place_cccd) REFERENCES identity_issuing_authority (identity_issuing_authority_id),
    CONSTRAINT fk_emp_med_reg FOREIGN KEY (medical_registration) REFERENCES medical_facility (medical_facility_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE employee_decision
(
    employee_decision_id  BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id           BIGINT       NOT NULL,
    decision_no           VARCHAR(100) NOT NULL UNIQUE,
    decision_date         DATETIME,
    department_id         BIGINT       NOT NULL,
    employee_type_id      BIGINT,
    job_position_id       BIGINT       NOT NULL,
    job_title_id          BIGINT       NOT NULL,
    job_rank_id           BIGINT       NOT NULL,
    cost_category_level_1 BIGINT,
    cost_category_level_2 BIGINT,
    decision_type_id      BIGINT, -- map tới decision_type_id
    is_active             BOOLEAN DEFAULT 1,
    effective_at          DATETIME     NOT NULL,
    CONSTRAINT fk_ed_employee FOREIGN KEY (employee_id) REFERENCES employee (employee_id),
    CONSTRAINT fk_ed_department FOREIGN KEY (department_id) REFERENCES department (department_id),
    CONSTRAINT fk_ed_costlvl1 FOREIGN KEY (cost_category_level_1) REFERENCES expense_type (expense_type_id),
    CONSTRAINT fk_ed_costlvl2 FOREIGN KEY (cost_category_level_2) REFERENCES expense_type (expense_type_id),
    CONSTRAINT fk_ed_employee_type FOREIGN KEY (employee_type_id) REFERENCES employee_type (employee_type_id),
    CONSTRAINT fk_ed_position FOREIGN KEY (job_position_id) REFERENCES job_position (position_id),
    CONSTRAINT fk_ed_title FOREIGN KEY (job_title_id) REFERENCES job_title (job_title_id),
    CONSTRAINT fk_ed_rank FOREIGN KEY (job_rank_id) REFERENCES job_rank (job_rank_id),
    CONSTRAINT fk_ed_decision_type_id FOREIGN KEY (decision_type_id) REFERENCES decision_type (decision_type_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE employee_education
(
    employee_education_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id           BIGINT NOT NULL,
    major_id              BIGINT,
    specialization_id     BIGINT,
    education_level_id    BIGINT,
    school_id             BIGINT,
    CONSTRAINT fk_eedu_emp FOREIGN KEY (employee_id) REFERENCES employee (employee_id),
    CONSTRAINT fk_eedu_major FOREIGN KEY (major_id) REFERENCES major (major_id),
    CONSTRAINT fk_eedu_spec FOREIGN KEY (specialization_id) REFERENCES specialization (specialization_id),
    CONSTRAINT fk_eedu_edlvl FOREIGN KEY (education_level_id) REFERENCES education_level (education_level_id),
    CONSTRAINT fk_eedu_school FOREIGN KEY (school_id) REFERENCES school (school_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE employee_work_shift
(
    employee_work_shift_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id            BIGINT NOT NULL,
    attendance_code        VARCHAR(100),
    work_shift_id          BIGINT,
    work_shift_group_id    BIGINT,
    attendance_type_id     BIGINT,
    saturday_full          BOOLEAN DEFAULT FALSE,
    ot_type_id             BIGINT,
    CONSTRAINT fk_ews_emp FOREIGN KEY (employee_id) REFERENCES employee (employee_id),
    CONSTRAINT fk_ews_ws FOREIGN KEY (work_shift_id) REFERENCES work_shift (work_shift_id),
    CONSTRAINT fk_ews_wsg FOREIGN KEY (work_shift_group_id) REFERENCES work_shift_group (work_shift_group_id),
    CONSTRAINT fk_ews_att_type FOREIGN KEY (attendance_type_id) REFERENCES attendance_type (attendance_type_id),
    CONSTRAINT fk_ews_ot FOREIGN KEY (ot_type_id) REFERENCES ot_type (ot_type_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE employee_attendance_machine
(
    employee_attendance_machine_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id                    BIGINT NOT NULL,
    machine_id                     BIGINT NOT NULL,
    CONSTRAINT fk_eam_emp FOREIGN KEY (employee_id) REFERENCES employee (employee_id),
    CONSTRAINT fk_eam_mach FOREIGN KEY (machine_id) REFERENCES attendance_machine (attendance_machine_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE employee_work_location
(
    employee_work_location_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id               BIGINT NOT NULL,
    work_location_id          BIGINT NOT NULL,
    CONSTRAINT fk_ewl_emp FOREIGN KEY (employee_id) REFERENCES employee (employee_id),
    CONSTRAINT fk_ewl_wl FOREIGN KEY (work_location_id) REFERENCES work_location (work_location_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

ALTER TABLE user_profile
    ADD CONSTRAINT fk_user_profile_employee
        FOREIGN KEY (employee_id) REFERENCES employee (employee_id)