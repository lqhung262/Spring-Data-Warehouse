# -- =====================================================================
# -- V1.5__seed_System_User_tables.sql
# -- Seed dữ liệu RBAC: user_profile tối thiểu, role, permission, role_permission, user_role, user
# -- Yêu cầu: đã chạy V1.4__create_System_User_tables.sql và có các bảng liên quan (user_profile)
# -- =====================================================================
#
# -- Khuyến nghị:
# -- SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;
#
# -- -------------------------------------------------
# -- 0) Tạo user_profile tối thiểu để làm created_by
# -- -------------------------------------------------
# INSERT INTO user_profile (email, full_name)
# VALUES ('seed@system.local', 'Flyway Seed');
# SET @seed_up := LAST_INSERT_ID();
#
# -- (tuỳ chọn) tạo 2 hồ sơ để minh hoạ người vận hành
# INSERT INTO user_profile (email, full_name)
# VALUES ('hr.manager@company.local', 'HR Manager Seed');
# SET @hrm_up := LAST_INSERT_ID();
#
# INSERT INTO user_profile (email, full_name)
# VALUES ('hr.viewer@company.local', 'HR Viewer Seed');
# SET @hrv_up := LAST_INSERT_ID();
#
#
# -- -----------------
# -- 1) ROLE
# -- -----------------
# INSERT INTO role (short_name, description, note) VALUES
#                                                      ('ROLE_DDC_SUPER_ADMIN', 'Toàn quyền hệ thống', 'built-in'),
#                                                      ('ROLE_DDC_HR_ADMIN',   'Quản trị domain Human Resource', NULL),
#                                                      ('ROLE_DDC_HR_MANAGER', 'Quản lý nghiệp vụ HR (không xoá cứng)', NULL),
#                                                      ('ROLE_DDC_HR_VIEWER',  'Xem dữ liệu HR (read-only)', NULL),
#                                                      ('ROLE_DDC_API_CLIENT', 'Client chỉ truy cập endpoint công khai', NULL);
#
# -- Lưu id các role
# SET @r_super  := (SELECT role_id FROM role WHERE short_name='ROLE_DDC_SUPER_ADMIN');
# SET @r_hr_adm := (SELECT role_id FROM role WHERE short_name='ROLE_DDC_HR_ADMIN');
# SET @r_hr_mgr := (SELECT role_id FROM role WHERE short_name='ROLE_DDC_HR_MANAGER');
# SET @r_hr_vwr := (SELECT role_id FROM role WHERE short_name='ROLE_DDC_HR_VIEWER');
# SET @r_api    := (SELECT role_id FROM role WHERE short_name='ROLE_DDC_API_CLIENT');
#
#
# -- -----------------
# -- 2) PERMISSION
# -- -----------------
# -- Domain HR (ví dụ các REST đã có sẵn)
# -- Employees
# INSERT INTO permission (short_name, description, url, method, is_public) VALUES
#                                                                              ('EMPLOYEE_LIST',        'List/Search employees',        '/api/v1/employees',        'GET',  FALSE),
#                                                                              ('EMPLOYEE_CREATE',      'Create employee',              '/api/v1/employees',        'POST', FALSE),
#                                                                              ('EMPLOYEE_UPDATE',      'Update employee',              '/api/v1/employees',        'PUT',  FALSE),
#                                                                              ('EMPLOYEE_PATCH',       'Patch employee',               '/api/v1/employees',        'PATCH',FALSE),
#                                                                              ('EMPLOYEE_DELETE',      'Delete employee',              '/api/v1/employees',        'DELETE',FALSE),
#                                                                              ('EMPLOYEE_GET_BY_ID',   'Get employee by id',           '/api/v1/employees/{id}',   'GET',  FALSE),
#                                                                              ('EMPLOYEE_DELETE_BY_ID','Delete employee by id',        '/api/v1/employees/{id}',   'DELETE',FALSE);
#
# -- Departments
# INSERT INTO permission (short_name, description, url, method, is_public) VALUES
#                                                                              ('DEPT_LIST',   'List/Search departments', '/api/v1/departments',      'GET',   FALSE),
#                                                                              ('DEPT_CREATE', 'Create department',       '/api/v1/departments',      'POST',  FALSE),
#                                                                              ('DEPT_UPDATE', 'Update department',       '/api/v1/departments',      'PUT',   FALSE),
#                                                                              ('DEPT_PATCH',  'Patch department',        '/api/v1/departments',      'PATCH', FALSE),
#                                                                              ('DEPT_DELETE', 'Delete department',       '/api/v1/departments',      'DELETE',FALSE);
#
# -- Employee Decisions
# INSERT INTO permission (short_name, description, url, method, is_public) VALUES
#                                                                              ('ED_LIST',   'List/Search employee decisions', '/api/v1/employee-decisions', 'GET',   FALSE),
#                                                                              ('ED_CREATE', 'Create employee decision',       '/api/v1/employee-decisions', 'POST',  FALSE),
#                                                                              ('ED_UPDATE', 'Update employee decision',       '/api/v1/employee-decisions', 'PUT',   FALSE),
#                                                                              ('ED_PATCH',  'Patch employee decision',        '/api/v1/employee-decisions', 'PATCH', FALSE),
#                                                                              ('ED_DELETE', 'Delete employee decision',       '/api/v1/employee-decisions', 'DELETE',FALSE);
#
# -- Public & Swagger/Springdoc/Health (để truy cập tài liệu và health check)
# INSERT INTO permission (short_name, description, url, method, is_public) VALUES
#                                                                              ('DOCS_JSON',     'OpenAPI JSON',      '/v3/api-docs',     'GET', TRUE),
#                                                                              ('DOCS_JSON_ALL', 'OpenAPI JSON all',  '/v3/api-docs/*',   'GET', TRUE),
#                                                                              ('SWAGGER_UI',    'Swagger UI',        '/swagger-ui/*',    'GET', TRUE),
#                                                                              ('SWAGGER_HTML',  'Swagger UI HTML',   '/swagger-ui.html', 'GET', TRUE),
#                                                                              ('HEALTH',        'Actuator Health',   '/actuator/health', 'GET', TRUE);
#
#
# -- ----------------------------
# -- 3) ROLE_PERMISSION mapping
# -- ----------------------------
#
# -- SUPER_ADMIN: toàn bộ permission
# INSERT INTO role_permission (role_id, permission_id)
# SELECT @r_super, p.permission_id FROM permission p
# ON DUPLICATE KEY UPDATE role_id=VALUES(role_id), permission_id=VALUES(permission_id);
#
# -- HR_ADMIN: tất cả endpoint /api/v1/*
# INSERT INTO role_permission (role_id, permission_id)
# SELECT @r_hr_adm, p.permission_id
# FROM permission p
# WHERE p.url LIKE '/api/v1/%'
# ON DUPLICATE KEY UPDATE role_id=VALUES(role_id), permission_id=VALUES(permission_id);
#
# -- HR_MANAGER: /api/v1/* nhưng KHÔNG DELETE
# INSERT INTO role_permission (role_id, permission_id)
# SELECT @r_hr_mgr, p.permission_id
# FROM permission p
# WHERE p.url LIKE '/api/v1/%'
#   AND p.method IN ('GET','POST','PUT','PATCH')
# ON DUPLICATE KEY UPDATE role_id=VALUES(role_id), permission_id=VALUES(permission_id);
#
# -- HR_VIEWER: chỉ GET trên /api/v1/*
# INSERT INTO role_permission (role_id, permission_id)
# SELECT @r_hr_vwr, p.permission_id
# FROM permission p
# WHERE p.url LIKE '/api/v1/%'
#   AND p.method='GET'
# ON DUPLICATE KEY UPDATE role_id=VALUES(role_id), permission_id=VALUES(permission_id);
#
# -- API_CLIENT: chỉ is_public = TRUE (docs/health,…)
# INSERT INTO role_permission (role_id, permission_id)
# SELECT @r_api, p.permission_id
# FROM permission p
# WHERE p.is_public = TRUE
# ON DUPLICATE KEY UPDATE role_id=VALUES(role_id), permission_id=VALUES(permission_id);
#
#
# -- ---------------
# -- 4) USER
# -- ---------------
# -- admin hệ thống
# INSERT INTO user (authorization_service_user_id, user_name, full_name, email_address, is_enabled, created_by)
# VALUES ('kc-uuid-admin', 'admin', 'System Admin', 'admin@company.local', TRUE, @seed_up);
#
# -- HR manager
# INSERT INTO user (authorization_service_user_id, user_name, full_name, email_address, is_enabled, created_by)
# VALUES ('kc-uuid-hrm', 'hrm', 'HR Manager', 'hr.manager@company.local', TRUE, @hrm_up);
#
# -- HR viewer
# INSERT INTO user (authorization_service_user_id, user_name, full_name, email_address, is_enabled, created_by)
# VALUES ('kc-uuid-hrv', 'hrv', 'HR Viewer', 'hr.viewer@company.local', TRUE, @hrv_up);
#
# -- Lưu id user
# SET @u_admin := (SELECT user_id FROM user WHERE user_name='admin');
# SET @u_hrm   := (SELECT user_id FROM user WHERE user_name='hrm');
# SET @u_hrv   := (SELECT user_id FROM user WHERE user_name='hrv');
#
# -- ----------------
# -- 5) USER_ROLE
# -- ----------------
# -- admin => SUPER_ADMIN
# INSERT INTO user_role (user_id, role_id) VALUES (@u_admin, @r_super)
# ON DUPLICATE KEY UPDATE user_id=VALUES(user_id), role_id=VALUES(role_id);
#
# -- hrm => HR_MANAGER
# INSERT INTO user_role (user_id, role_id) VALUES (@u_hrm, @r_hr_mgr)
# ON DUPLICATE KEY UPDATE user_id=VALUES(user_id), role_id=VALUES(role_id);
#
# -- hrv => HR_VIEWER
# INSERT INTO user_role (user_id, role_id) VALUES (@u_hrv, @r_hr_vwr)
# ON DUPLICATE KEY UPDATE user_id=VALUES(user_id), role_id=VALUES(role_id);
#
