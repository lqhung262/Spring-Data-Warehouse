SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

INSERT INTO source_system (source_system_id, name, description)
VALUES (1, 'SAP', 'SAP data source'),
       (2, 'SPM', 'SPM data source'),
       (3, 'P6', 'P6 data source'),
       (4, 'HRM', 'HRM data source');

INSERT INTO user_profile (user_profile_id, employee_id, email, full_name, created_at, updated_at)
VALUES (1, NULL, 'system@local', 'System Seeder', NOW(), NOW());

-- 2) Countries
INSERT INTO country (country_id, country_code, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                     created_at, updated_at)
VALUES (1, 'VN', 'CTRY-VN', 'Việt Nam', 4, FALSE, 1, 1, NOW(), NOW()),
       (2, 'US', 'CTRY-US', 'Hoa Kì', 4, FALSE, 1, 1, NOW(), NOW()),
       (3, 'KR', 'CTRY-KR', 'Hàn Quốc', 4, FALSE, 1, 1, NOW(), NOW()),
       (4, 'JP', 'CTRY-JP', 'Nhật Bản', 4, FALSE, 1, 1, NOW(), NOW()),
       (5, 'CN', 'CTRY-CN', 'Trung Quốc', 4, FALSE, 1, 1, NOW(), NOW());

-- 3) Gender
INSERT INTO gender (gender_id, name)
VALUES (1, 'Nam');
INSERT INTO gender (gender_id, name)
VALUES (2, 'Nữ');
INSERT INTO gender (gender_id, name)
VALUES (3, 'Khác');

-- 4) Language
INSERT INTO language (language_id, language_code, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                      created_at, updated_at)
VALUES (1, 'VI', 'LANG-VI', 'Tiếng Việt', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO language (language_id, language_code, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                      created_at, updated_at)
VALUES (2, 'EN', 'LANG-EN', 'English', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO language (language_id, language_code, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                      created_at, updated_at)
VALUES (3, 'JA', 'LANG-JA', '日本語', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO language (language_id, language_code, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                      created_at, updated_at)
VALUES (4, 'KO', 'LANG-KO', '한국어', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO language (language_id, language_code, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                      created_at, updated_at)
VALUES (5, 'ZH', 'LANG-ZH', '中文', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO language (language_id, language_code, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                      created_at, updated_at)
VALUES (6, 'FR', 'LANG-FR', 'Français', 4, FALSE, 1, 1, NOW(), NOW());

-- 5) Nationality
INSERT INTO nationality (nationality_id, nationality_code, source_id, name, source_system_id, is_deleted, created_by,
                         updated_by, created_at, updated_at)
VALUES (1, 'VN', 'NAT-VN', 'Việt Nam', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO nationality (nationality_id, nationality_code, source_id, name, source_system_id, is_deleted, created_by,
                         updated_by, created_at, updated_at)
VALUES (2, 'US', 'NAT-US', 'Hoa Kỳ', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO nationality (nationality_id, nationality_code, source_id, name, source_system_id, is_deleted, created_by,
                         updated_by, created_at, updated_at)
VALUES (3, 'JP', 'NAT-JP', 'Nhật Bản', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO nationality (nationality_id, nationality_code, source_id, name, source_system_id, is_deleted, created_by,
                         updated_by, created_at, updated_at)
VALUES (4, 'KR', 'NAT-KR', 'Hàn Quốc', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO nationality (nationality_id, nationality_code, source_id, name, source_system_id, is_deleted, created_by,
                         updated_by, created_at, updated_at)
VALUES (5, 'CN', 'NAT-CN', 'Trung Quốc', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO nationality (nationality_id, nationality_code, source_id, name, source_system_id, is_deleted, created_by,
                         updated_by, created_at, updated_at)
VALUES (6, 'FR', 'NAT-FR', 'Pháp', 4, FALSE, 1, 1, NOW(), NOW());

-- 6) Marital Status
INSERT INTO marital_status (marital_status_id, marital_status_code, source_id, name, source_system_id, is_deleted,
                            created_by, updated_by, created_at, updated_at)
VALUES (1, 'SINGLE', 'MS-SINGLE', 'Độc thân', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO marital_status (marital_status_id, marital_status_code, source_id, name, source_system_id, is_deleted,
                            created_by, updated_by, created_at, updated_at)
VALUES (2, 'MARRIED', 'MS-MARRIED', 'Kết hôn', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO marital_status (marital_status_id, marital_status_code, source_id, name, source_system_id, is_deleted,
                            created_by, updated_by, created_at, updated_at)
VALUES (3, 'DIVORCED', 'MS-DIVORCED', 'Ly hôn', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO marital_status (marital_status_id, marital_status_code, source_id, name, source_system_id, is_deleted,
                            created_by, updated_by, created_at, updated_at)
VALUES (4, 'WIDOWED', 'MS-WIDOWED', 'Góa', 4, FALSE, 1, 1, NOW(), NOW());

-- 7) Labor Status
INSERT INTO labor_status (labor_status_id, labor_status_code, source_id, name, source_system_id, is_deleted, created_by,
                          updated_by, created_at, updated_at)
VALUES (1, 'ACTIVE', 'LS-ACTIVE', 'Đang làm', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO labor_status (labor_status_id, labor_status_code, source_id, name, source_system_id, is_deleted, created_by,
                          updated_by, created_at, updated_at)
VALUES (2, 'ON_LEAVE', 'LS-ON_LEAVE', 'Tạm nghỉ', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO labor_status (labor_status_id, labor_status_code, source_id, name, source_system_id, is_deleted, created_by,
                          updated_by, created_at, updated_at)
VALUES (3, 'RESIGNED', 'LS-RESIGNED', 'Nghỉ việc', 4, FALSE, 1, 1, NOW(), NOW());
-- INSERT INTO labor_status (labor_status_id, labor_status_code, source_id, name, source_system_id, is_deleted, created_by, updated_by, created_at, updated_at)
-- VALUES (4, 'INTERN', 'LS-INTERN', 'Thực tập', 4, FALSE, 1, 1, NOW(), NOW());

-- 8) Employee Type
INSERT INTO employee_type (employee_type_id, employee_type_code, source_id, name, source_system_id, is_deleted,
                           created_by, updated_by, created_at, updated_at)
VALUES (1, 'FULLTIME', 'EMPTYPE-FULLTIME', 'Chính thức', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO employee_type (employee_type_id, employee_type_code, source_id, name, source_system_id, is_deleted,
                           created_by, updated_by, created_at, updated_at)
VALUES (2, 'CONTRACT', 'EMPTYPE-CONTRACT', 'Hợp đồng', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO employee_type (employee_type_id, employee_type_code, source_id, name, source_system_id, is_deleted,
                           created_by, updated_by, created_at, updated_at)
VALUES (3, 'TEMP', 'EMPTYPE-TEMP', 'Thời vụ', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO employee_type (employee_type_id, employee_type_code, source_id, name, source_system_id, is_deleted,
                           created_by, updated_by, created_at, updated_at)
VALUES (4, 'INTERN', 'EMPTYPE-INTERN', 'Thực tập', 4, FALSE, 1, 1, NOW(), NOW());

-- 9) Blood Group
INSERT INTO blood_group (blood_group_id, blood_group_code, source_id, name, source_system_id, is_deleted, created_by,
                         updated_by, created_at, updated_at)
VALUES (1, 'A', 'BG-A', 'A', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO blood_group (blood_group_id, blood_group_code, source_id, name, source_system_id, is_deleted, created_by,
                         updated_by, created_at, updated_at)
VALUES (2, 'B', 'BG-B', 'B', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO blood_group (blood_group_id, blood_group_code, source_id, name, source_system_id, is_deleted, created_by,
                         updated_by, created_at, updated_at)
VALUES (3, 'AB', 'BG-AB', 'AB', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO blood_group (blood_group_id, blood_group_code, source_id, name, source_system_id, is_deleted, created_by,
                         updated_by, created_at, updated_at)
VALUES (4, 'O', 'BG-O', 'O', 4, FALSE, 1, 1, NOW(), NOW());

-- 10) Education Level
INSERT INTO education_level (education_level_id, education_level_code, source_id, name, source_system_id, is_deleted,
                             created_by, updated_by, created_at, updated_at)
VALUES (1, 'CD', 'EDU-CD', 'Cao đẳng', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO education_level (education_level_id, education_level_code, source_id, name, source_system_id, is_deleted,
                             created_by, updated_by, created_at, updated_at)
VALUES (2, 'DH', 'EDU-DH', 'Đại học', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO education_level (education_level_id, education_level_code, source_id, name, source_system_id, is_deleted,
                             created_by, updated_by, created_at, updated_at)
VALUES (3, 'THS', 'EDU-THS', 'Thạc sĩ', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO education_level (education_level_id, education_level_code, source_id, name, source_system_id, is_deleted,
                             created_by, updated_by, created_at, updated_at)
VALUES (4, 'TS', 'EDU-TS', 'Tiến sĩ', 4, FALSE, 1, 1, NOW(), NOW());

-- 11) School
INSERT INTO school (school_id, school_code, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                    created_at, updated_at)
VALUES (1, 'HUST', 'SCH-HUST', 'Đại học Bách khoa Hà Nội', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO school (school_id, school_code, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                    created_at, updated_at)
VALUES (2, 'NEU', 'SCH-NEU', 'Đại học Kinh tế Quốc dân', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO school (school_id, school_code, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                    created_at, updated_at)
VALUES (3, 'HCMUT', 'SCH-HCMUT', 'Đại học Bách khoa TP.HCM', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO school (school_id, school_code, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                    created_at, updated_at)
VALUES (4, 'HCMUS', 'SCH-HCMUS', 'Đại học Khoa học Tự Nhiên', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO school (school_id, school_code, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                    created_at, updated_at)
VALUES (5, 'UIT', 'SCH-UIT', 'Đại học Công nghệ Thông Tin', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO school (school_id, school_code, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                    created_at, updated_at)
VALUES (6, 'IU', 'SCH-IU', 'Đại học Quốc Tế', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO school (school_id, school_code, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                    created_at, updated_at)
VALUES (7, 'UTE', 'SCH-HCMUTE', 'Đại học sư phạm kĩ thuật', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO school (school_id, school_code, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                    created_at, updated_at)
VALUES (8, 'NLU', 'SCH-NLU', 'Đại học Nông Lâm', 4, FALSE, 1, 1, NOW(), NOW());

-- 12) Major
INSERT INTO major (major_id, major_code, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                   created_at, updated_at)
VALUES (1, 'IT', 'MAJ-IT', 'Công nghệ thông tin', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO major (major_id, major_code, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                   created_at, updated_at)
VALUES (2, 'BA', 'MAJ-BA', 'Quản trị kinh doanh', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO major (major_id, major_code, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                   created_at, updated_at)
VALUES (3, 'FIN', 'MAJ-FIN', 'Tài chính', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO major (major_id, major_code, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                   created_at, updated_at)
VALUES (4, 'ACC', 'MAJ-ACC', 'Kế toán', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO major (major_id, major_code, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                   created_at, updated_at)
VALUES (5, 'MKT', 'MAJ-MKT', 'Marketing', 4, FALSE, 1, 1, NOW(), NOW());

-- 13) Specialization
INSERT INTO specialization (specialization_id, specialization_code, source_id, name, source_system_id, is_deleted,
                            created_by, updated_by, created_at, updated_at)
VALUES (1, 'IS', 'SPEC-IS', 'Hệ thống thông tin', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO specialization (specialization_id, specialization_code, source_id, name, source_system_id, is_deleted,
                            created_by, updated_by, created_at, updated_at)
VALUES (2, 'SE', 'SPEC-SE', 'Kỹ thuật phần mềm', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO specialization (specialization_id, specialization_code, source_id, name, source_system_id, is_deleted,
                            created_by, updated_by, created_at, updated_at)
VALUES (3, 'DS', 'SPEC-DS', 'Khoa học dữ liệu', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO specialization (specialization_id, specialization_code, source_id, name, source_system_id, is_deleted,
                            created_by, updated_by, created_at, updated_at)
VALUES (4, 'AI', 'SPEC-AI', 'Trí tuệ nhân tạo', 4, FALSE, 1, 1, NOW(), NOW());

-- 14) Job Rank
INSERT INTO job_rank (job_rank_id, job_rank_code, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                      created_at, updated_at)
VALUES (1, 'INTERN', 'JRANK-INTERN', 'Intern', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO job_rank (job_rank_id, job_rank_code, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                      created_at, updated_at)
VALUES (2, 'JR', 'JRANK-JR', 'Junior', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO job_rank (job_rank_id, job_rank_code, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                      created_at, updated_at)
VALUES (3, 'MID', 'JRANK-MID', 'Middle', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO job_rank (job_rank_id, job_rank_code, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                      created_at, updated_at)
VALUES (4, 'SR', 'JRANK-SR', 'Senior', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO job_rank (job_rank_id, job_rank_code, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                      created_at, updated_at)
VALUES (5, 'LEAD', 'JRANK-LEAD', 'Lead', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO job_rank (job_rank_id, job_rank_code, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                      created_at, updated_at)
VALUES (6, 'MGR', 'JRANK-MGR', 'Manager', 4, FALSE, 1, 1, NOW(), NOW());

-- 15) Job Title
INSERT INTO job_title (job_title_id, job_title_code, source_id, name, source_system_id, is_deleted, created_by,
                       updated_by, created_at, updated_at)
VALUES (1, 'DEV', 'JTITLE-DEV', 'Lập trình viên', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO job_title (job_title_id, job_title_code, source_id, name, source_system_id, is_deleted, created_by,
                       updated_by, created_at, updated_at)
VALUES (2, 'QA', 'JTITLE-QA', 'Kiểm thử', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO job_title (job_title_id, job_title_code, source_id, name, source_system_id, is_deleted, created_by,
                       updated_by, created_at, updated_at)
VALUES (3, 'BA', 'JTITLE-BA', 'Chuyên viên phân tích nghiệp vụ', 1, FALSE, 1, 1, NOW(), NOW());
INSERT INTO job_title (job_title_id, job_title_code, source_id, name, source_system_id, is_deleted, created_by,
                       updated_by, created_at, updated_at)
VALUES (4, 'PM', 'JTITLE-PM', 'Quản lý dự án', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO job_title (job_title_id, job_title_code, source_id, name, source_system_id, is_deleted, created_by,
                       updated_by, created_at, updated_at)
VALUES (5, 'HR', 'JTITLE-HR', 'Nhân sự', 4, FALSE, 1, 1, NOW(), NOW());

-- 16) Job Position
INSERT INTO job_position (position_id, position_code, source_id, name, source_system_id, is_deleted, created_by,
                          updated_by, created_at, updated_at)
VALUES (1, 'STAFF', 'JPOS-STAFF', 'Nhân viên', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO job_position (position_id, position_code, source_id, name, source_system_id, is_deleted, created_by,
                          updated_by, created_at, updated_at)
VALUES (2, 'SPEC', 'JPOS-SPEC', 'Chuyên viên', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO job_position (position_id, position_code, source_id, name, source_system_id, is_deleted, created_by,
                          updated_by, created_at, updated_at)
VALUES (3, 'TL', 'JPOS-TL', 'Trưởng nhóm', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO job_position (position_id, position_code, source_id, name, source_system_id, is_deleted, created_by,
                          updated_by, created_at, updated_at)
VALUES (4, 'MGR', 'JPOS-MGR', 'Trưởng phòng', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO job_position (position_id, position_code, source_id, name, source_system_id, is_deleted, created_by,
                          updated_by, created_at, updated_at)
VALUES (5, 'DIR', 'JPOS-DIR', 'Giám đốc', 4, FALSE, 1, 1, NOW(), NOW());

-- 17) Work Shift Group
INSERT INTO work_shift_group (work_shift_group_id, work_shift_group_code, source_id, name, source_system_id, is_deleted,
                              created_by, updated_by, created_at, updated_at)
VALUES (1, 'HC', 'WSG-HC', 'Hành chính', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO work_shift_group (work_shift_group_id, work_shift_group_code, source_id, name, source_system_id, is_deleted,
                              created_by, updated_by, created_at, updated_at)
VALUES (2, 'SX', 'WSG-SX', 'Sản xuất', 4, FALSE, 1, 1, NOW(), NOW());

-- 18) Work Shift
INSERT INTO work_shift (work_shift_id, work_shift_code, source_id, name, source_system_id, is_deleted, created_by,
                        updated_by, created_at, updated_at)
VALUES (1, 'HC1', 'WS-HC1', 'Hành chính 1', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO work_shift (work_shift_id, work_shift_code, source_id, name, source_system_id, is_deleted, created_by,
                        updated_by, created_at, updated_at)
VALUES (2, 'HC2', 'WS-HC2', 'Hành chính 2', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO work_shift (work_shift_id, work_shift_code, source_id, name, source_system_id, is_deleted, created_by,
                        updated_by, created_at, updated_at)
VALUES (3, 'CA1', 'WS-CA1', 'Ca 1 (6h-14h)', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO work_shift (work_shift_id, work_shift_code, source_id, name, source_system_id, is_deleted, created_by,
                        updated_by, created_at, updated_at)
VALUES (4, 'CA2', 'WS-CA2', 'Ca 2 (14h-22h)', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO work_shift (work_shift_id, work_shift_code, source_id, name, source_system_id, is_deleted, created_by,
                        updated_by, created_at, updated_at)
VALUES (5, 'CA3', 'WS-CA3', 'Ca 3 (22h-6h)', 4, FALSE, 1, 1, NOW(), NOW());

-- 19) Province / City
INSERT INTO province_city (province_city_id, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                           created_at, updated_at)
VALUES (1, 'PC-TP-HANOI', 'Hà Nội', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO province_city (province_city_id, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                           created_at, updated_at)
VALUES (2, 'PC-TP-HUE', 'Huế', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO province_city (province_city_id, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                           created_at, updated_at)
VALUES (3, 'PC-PR-LAICHAU', 'Lai Châu', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO province_city (province_city_id, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                           created_at, updated_at)
VALUES (4, 'PC-PR-DIENBIEN', 'Điện Biên', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO province_city (province_city_id, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                           created_at, updated_at)
VALUES (5, 'PC-PR-SONLA', 'Sơn La', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO province_city (province_city_id, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                           created_at, updated_at)
VALUES (6, 'PC-PR-CAOBANG', 'Cao Bằng', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO province_city (province_city_id, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                           created_at, updated_at)
VALUES (7, 'PC-PR-LANGSON', 'Lạng Sơn', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO province_city (province_city_id, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                           created_at, updated_at)
VALUES (8, 'PC-PR-QUANGNINH', 'Quảng Ninh', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO province_city (province_city_id, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                           created_at, updated_at)
VALUES (9, 'PC-PR-THANHHOA', 'Thanh Hóa', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO province_city (province_city_id, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                           created_at, updated_at)
VALUES (10, 'PC-PR-NGHEAN', 'Nghệ An', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO province_city (province_city_id, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                           created_at, updated_at)
VALUES (11, 'PC-PR-HATINH', 'Hà Tĩnh', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO province_city (province_city_id, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                           created_at, updated_at)
VALUES (12, 'PC-PR-TUYENQUANG', 'Tuyên Quang', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO province_city (province_city_id, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                           created_at, updated_at)
VALUES (13, 'PC-PR-LAOCAI', 'Lào Cai', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO province_city (province_city_id, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                           created_at, updated_at)
VALUES (14, 'PC-PR-THAINGUYEN', 'Thái Nguyên', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO province_city (province_city_id, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                           created_at, updated_at)
VALUES (15, 'PC-PR-PHUTHO', 'Phú Thọ', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO province_city (province_city_id, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                           created_at, updated_at)
VALUES (16, 'PC-PR-BACNINH', 'Bắc Ninh', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO province_city (province_city_id, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                           created_at, updated_at)
VALUES (17, 'PC-PR-HUNGYEN', 'Hưng Yên', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO province_city (province_city_id, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                           created_at, updated_at)
VALUES (18, 'PC-TP-HAIPHONG', 'Hải Phòng', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO province_city (province_city_id, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                           created_at, updated_at)
VALUES (19, 'PC-PR-NINHBINH', 'Ninh Bình', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO province_city (province_city_id, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                           created_at, updated_at)
VALUES (20, 'PC-PR-QUANGTRI', 'Quảng Trị', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO province_city (province_city_id, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                           created_at, updated_at)
VALUES (21, 'PC-TP-DANANG', 'Đà Nẵng', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO province_city (province_city_id, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                           created_at, updated_at)
VALUES (22, 'PC-PR-QUANGNGAI', 'Quảng Ngãi', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO province_city (province_city_id, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                           created_at, updated_at)
VALUES (23, 'PC-PR-GIALAI', 'Gia Lai', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO province_city (province_city_id, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                           created_at, updated_at)
VALUES (24, 'PC-PR-KHANHHOA', 'Khánh Hòa', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO province_city (province_city_id, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                           created_at, updated_at)
VALUES (25, 'PC-PR-LAMDONG', 'Lâm Đồng', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO province_city (province_city_id, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                           created_at, updated_at)
VALUES (26, 'PC-PR-DAKLAK', 'Đắk Lắk', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO province_city (province_city_id, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                           created_at, updated_at)
VALUES (27, 'PC-TP-HCMC', 'Thành phố Hồ Chí Minh', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO province_city (province_city_id, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                           created_at, updated_at)
VALUES (28, 'PC-PR-DONGNAI', 'Đồng Nai', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO province_city (province_city_id, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                           created_at, updated_at)
VALUES (29, 'PC-PR-TAYNINH', 'Tây Ninh', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO province_city (province_city_id, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                           created_at, updated_at)
VALUES (30, 'PC-TP-CANTHO', 'Cần Thơ', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO province_city (province_city_id, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                           created_at, updated_at)
VALUES (31, 'PC-PR-VINHLONG', 'Vĩnh Long', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO province_city (province_city_id, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                           created_at, updated_at)
VALUES (32, 'PC-PR-DONGTHAP', 'Đồng Tháp', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO province_city (province_city_id, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                           created_at, updated_at)
VALUES (33, 'PC-PR-CAMAU', 'Cà Mau', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO province_city (province_city_id, source_id, name, source_system_id, is_deleted, created_by, updated_by,
                           created_at, updated_at)
VALUES (34, 'PC-PR-ANGIANG', 'An Giang', 4, FALSE, 1, 1, NOW(), NOW());

-- 20) Ward
INSERT INTO ward (ward_id, source_id, province_city_id, name, source_system_id, is_deleted, created_by, updated_by,
                  created_at, updated_at)
VALUES (1, 'WARD-001', 27, 'Bến Nghé', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO ward (ward_id, source_id, province_city_id, name, source_system_id, is_deleted, created_by, updated_by,
                  created_at, updated_at)
VALUES (2, 'WARD-002', 1, 'Liên Ninh', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO ward (ward_id, source_id, province_city_id, name, source_system_id, is_deleted, created_by, updated_by,
                  created_at, updated_at)
VALUES (3, 'WARD-003', 21, 'Hải Châu I', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO ward (ward_id, source_id, province_city_id, name, source_system_id, is_deleted, created_by, updated_by,
                  created_at, updated_at)
VALUES (4, 'WARD-004', 30, 'An Khánh', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO ward (ward_id, source_id, province_city_id, name, source_system_id, is_deleted, created_by, updated_by,
                  created_at, updated_at)
VALUES (5, 'WARD-005', 18, 'Trần Phú', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO ward (ward_id, source_id, province_city_id, name, source_system_id, is_deleted, created_by, updated_by,
                  created_at, updated_at)
VALUES (6, 'WARD-006', 2, 'Phú Hội', 4, FALSE, 1, 1, NOW(), NOW());

-- 21) Old Province / City
INSERT INTO old_province_city (old_province_city_id, province_city_id, source_id, name, source_system_id, is_deleted,
                               created_by, updated_by, created_at, updated_at)
VALUES (1, 13, 'OLDPC-YENBAI', 'Yên Bái', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO old_province_city (old_province_city_id, province_city_id, source_id, name, source_system_id, is_deleted,
                               created_by, updated_by, created_at, updated_at)
VALUES (2, 12, 'OLDPC-HAGIANG', 'Hà Giang', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO old_province_city (old_province_city_id, province_city_id, source_id, name, source_system_id, is_deleted,
                               created_by, updated_by, created_at, updated_at)
VALUES (3, 16, 'OLDPC-BACGIANG', 'Bắc Giang', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO old_province_city (old_province_city_id, province_city_id, source_id, name, source_system_id, is_deleted,
                               created_by, updated_by, created_at, updated_at)
VALUES (4, 17, 'OLDPC-THAIBINH', 'Thái Bình', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO old_province_city (old_province_city_id, province_city_id, source_id, name, source_system_id, is_deleted,
                               created_by, updated_by, created_at, updated_at)
VALUES (5, 19, 'OLDPC-HANAM', 'Hà Nam', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO old_province_city (old_province_city_id, province_city_id, source_id, name, source_system_id, is_deleted,
                               created_by, updated_by, created_at, updated_at)
VALUES (6, 19, 'OLDPC-NAMDINH', 'Nam Định', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO old_province_city (old_province_city_id, province_city_id, source_id, name, source_system_id, is_deleted,
                               created_by, updated_by, created_at, updated_at)
VALUES (7, 20, 'OLDPC-QUANGBINH', 'Quảng Bình', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO old_province_city (old_province_city_id, province_city_id, source_id, name, source_system_id, is_deleted,
                               created_by, updated_by, created_at, updated_at)
VALUES (8, 21, 'OLDPC-QUANGNAM', 'Quảng Nam', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO old_province_city (old_province_city_id, province_city_id, source_id, name, source_system_id, is_deleted,
                               created_by, updated_by, created_at, updated_at)
VALUES (9, 22, 'OLDPC-KONTUM', 'Kon Tum', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO old_province_city (old_province_city_id, province_city_id, source_id, name, source_system_id, is_deleted,
                               created_by, updated_by, created_at, updated_at)
VALUES (10, 23, 'OLDPC-BINHDINH', 'Bình Định', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO old_province_city (old_province_city_id, province_city_id, source_id, name, source_system_id, is_deleted,
                               created_by, updated_by, created_at, updated_at)
VALUES (11, 24, 'OLDPC-NINHTHUAN', 'Ninh Thuận', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO old_province_city (old_province_city_id, province_city_id, source_id, name, source_system_id, is_deleted,
                               created_by, updated_by, created_at, updated_at)
VALUES (12, 25, 'OLDPC-DAKNONG', 'Đắk Nông', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO old_province_city (old_province_city_id, province_city_id, source_id, name, source_system_id, is_deleted,
                               created_by, updated_by, created_at, updated_at)
VALUES (13, 25, 'OLDPC-BINHTHUAN', 'Bình Thuận', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO old_province_city (old_province_city_id, province_city_id, source_id, name, source_system_id, is_deleted,
                               created_by, updated_by, created_at, updated_at)
VALUES (14, 26, 'OLDPC-PHUYEN', 'Phú Yên', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO old_province_city (old_province_city_id, province_city_id, source_id, name, source_system_id, is_deleted,
                               created_by, updated_by, created_at, updated_at)
VALUES (15, 27, 'OLDPC-BRVT', 'Bà Rịa - Vũng Tàu', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO old_province_city (old_province_city_id, province_city_id, source_id, name, source_system_id, is_deleted,
                               created_by, updated_by, created_at, updated_at)
VALUES (16, 27, 'OLDPC-BINHDUONG', 'Bình Dương', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO old_province_city (old_province_city_id, province_city_id, source_id, name, source_system_id, is_deleted,
                               created_by, updated_by, created_at, updated_at)
VALUES (17, 28, 'OLDPC-BINHPHUOC', 'Bình Phước', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO old_province_city (old_province_city_id, province_city_id, source_id, name, source_system_id, is_deleted,
                               created_by, updated_by, created_at, updated_at)
VALUES (18, 29, 'OLDPC-LONGAN', 'Long An', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO old_province_city (old_province_city_id, province_city_id, source_id, name, source_system_id, is_deleted,
                               created_by, updated_by, created_at, updated_at)
VALUES (19, 30, 'OLDPC-SOCTRANG', 'Sóc Trăng', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO old_province_city (old_province_city_id, province_city_id, source_id, name, source_system_id, is_deleted,
                               created_by, updated_by, created_at, updated_at)
VALUES (20, 30, 'OLDPC-HAUGIANG', 'Hậu Giang', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO old_province_city (old_province_city_id, province_city_id, source_id, name, source_system_id, is_deleted,
                               created_by, updated_by, created_at, updated_at)
VALUES (21, 31, 'OLDPC-BENTRE', 'Bến Tre', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO old_province_city (old_province_city_id, province_city_id, source_id, name, source_system_id, is_deleted,
                               created_by, updated_by, created_at, updated_at)
VALUES (22, 31, 'OLDPC-TRAVINH', 'Trà Vinh', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO old_province_city (old_province_city_id, province_city_id, source_id, name, source_system_id, is_deleted,
                               created_by, updated_by, created_at, updated_at)
VALUES (23, 32, 'OLDPC-TIENGIANG', 'Tiền Giang', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO old_province_city (old_province_city_id, province_city_id, source_id, name, source_system_id, is_deleted,
                               created_by, updated_by, created_at, updated_at)
VALUES (24, 33, 'OLDPC-BACLIEU', 'Bạc Liêu', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO old_province_city (old_province_city_id, province_city_id, source_id, name, source_system_id, is_deleted,
                               created_by, updated_by, created_at, updated_at)
VALUES (25, 34, 'OLDPC-KIENGIANG', 'Kiên Giang', 4, FALSE, 1, 1, NOW(), NOW());

-- 22) Old District
INSERT INTO old_district (old_district_id, ward_id, source_id, old_province_city_id, name, source_system_id, is_deleted,
                          created_by, updated_by, created_at, updated_at)
VALUES (1, 1, 'OLDDIST-0001', 1, 'Quận cũ A', 4, FALSE, 1, 1, NOW(), NOW());

-- 23) Old Ward
INSERT INTO old_ward (old_ward_id, ward_id, source_id, old_district_id, name, source_system_id, is_deleted, created_by,
                      updated_by, created_at, updated_at)
VALUES (1, 1, 'OLDWARD-0001', 1, 'Phường cũ A1', 4, FALSE, 1, 1, NOW(), NOW());

-- 24) Identity Issuing Authority
INSERT INTO identity_issuing_authority (identity_issuing_authority_id, source_id, document_type_id, name,
                                        source_system_id, is_deleted, created_by, updated_by, created_at, updated_at)
VALUES (1, 'C06-BOCA', NULL, 'Cục CSQL về TTXH (C06) - Bộ Công an', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO identity_issuing_authority (identity_issuing_authority_id, source_id, document_type_id, name,
                                        source_system_id, is_deleted, created_by, updated_by, created_at, updated_at)
VALUES (2, 'CA-HANOI', NULL, 'Công an TP Hà Nội', 4, FALSE, 1, 1, NOW(), NOW());
INSERT INTO identity_issuing_authority (identity_issuing_authority_id, source_id, document_type_id, name,
                                        source_system_id, is_deleted, created_by, updated_by, created_at, updated_at)
VALUES (3, 'CA-HCM', NULL, 'Công an TP Hồ Chí Minh', 4, FALSE, 1, 1, NOW(), NOW());
SET FOREIGN_KEY_CHECKS = 1;