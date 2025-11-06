-- V1.6__seed_permissions_and_role_permissions.sql
-- Seed permissions for controller base paths (match @RequestMapping values)
-- Idempotent via INSERT IGNORE (MySQL). If you use a different DB adjust accordingly.

INSERT IGNORE INTO permission (short_name, description, url, method, is_public)
VALUES ('ROLE_LIST', 'List roles', '/roles', 'GET', FALSE),
       ('ROLE_CREATE', 'Create role', '/roles', 'POST', FALSE),
       ('ROLE_UPDATE', 'Update role', '/roles', 'PUT', FALSE),
       ('ROLE_DELETE', 'Delete role', '/roles', 'DELETE', FALSE),

       ('ROLE_PERMISSION_LIST', 'List role-permissions', '/role-permissions', 'GET', FALSE),
       ('ROLE_PERMISSION_CREATE', 'Create role-permission', '/role-permissions', 'POST', FALSE),
       ('ROLE_PERMISSION_UPDATE', 'Update role-permission', '/role-permissions', 'PUT', FALSE),
       ('ROLE_PERMISSION_DELETE', 'Delete role-permission', '/role-permissions', 'DELETE', FALSE),

       ('PERMISSION_LIST', 'List permissions', '/permissions', 'GET', FALSE),
       ('PERMISSION_CREATE', 'Create permission', '/permissions', 'POST', FALSE),
       ('PERMISSION_UPDATE', 'Update permission', '/permissions', 'PUT', FALSE),
       ('PERMISSION_DELETE', 'Delete permission', '/permissions', 'DELETE', FALSE),

       ('USER_LIST', 'List users', '/users', 'GET', FALSE),
       ('USER_CREATE', 'Create user', '/users', 'POST', FALSE),
       ('USER_UPDATE', 'Update user', '/users', 'PUT', FALSE),
       ('USER_DELETE', 'Delete user', '/users', 'DELETE', FALSE),

       ('USER_ROLE_LIST', 'List user roles', '/user-roles', 'GET', FALSE),
       ('USER_ROLE_CREATE', 'Create user role', '/user-roles', 'POST', FALSE),
       ('USER_ROLE_UPDATE', 'Update user role', '/user-roles', 'PUT', FALSE),
       ('USER_ROLE_DELETE', 'Delete user role', '/user-roles', 'DELETE', FALSE),

       ('PERM_ASSIGN_LIST', 'List role-permissions', '/role-permissions', 'GET', FALSE),

       ('USERPROFILE_LIST', 'List user profiles', '/user-profiles', 'GET', FALSE),
       ('USERPROFILE_CREATE', 'Create user profile', '/user-profiles', 'POST', FALSE),
       ('USERPROFILE_UPDATE', 'Update user profile', '/user-profiles', 'PUT', FALSE),
       ('USERPROFILE_DELETE', 'Delete user profile', '/user-profiles', 'DELETE', FALSE),

       ('SOURCE_SYSTEM_LIST', 'List source systems', '/source-systems', 'GET', FALSE),
       ('SOURCE_SYSTEM_CREATE', 'Create source system', '/source-systems', 'POST', FALSE),
       ('SOURCE_SYSTEM_UPDATE', 'Update source system', '/source-systems', 'PUT', FALSE),
       ('SOURCE_SYSTEM_DELETE', 'Delete source system', '/source-systems', 'DELETE', FALSE),

       ('COUNTRY_LIST', 'List countries', '/countries', 'GET', FALSE),
       ('COUNTRY_CREATE', 'Create country', '/countries', 'POST', FALSE),
       ('COUNTRY_UPDATE', 'Update country', '/countries', 'PUT', FALSE),
       ('COUNTRY_DELETE', 'Delete country', '/countries', 'DELETE', FALSE),

       ('BANK_LIST', 'List banks', '/banks', 'GET', FALSE),
       ('BANK_CREATE', 'Create bank', '/banks', 'POST', FALSE),
       ('BANK_UPDATE', 'Update bank', '/banks', 'PUT', FALSE),
       ('BANK_DELETE', 'Delete bank', '/banks', 'DELETE', FALSE),

       ('BLOODGROUP_LIST', 'List blood groups', '/blood-groups', 'GET', FALSE),
       ('BLOODGROUP_CREATE', 'Create blood group', '/blood-groups', 'POST', FALSE),
       ('BLOODGROUP_UPDATE', 'Update blood group', '/blood-groups', 'PUT', FALSE),
       ('BLOODGROUP_DELETE', 'Delete blood group', '/blood-groups', 'DELETE', FALSE),

       ('DEPARTMENT_LIST', 'List departments', '/departments', 'GET', FALSE),
       ('DEPARTMENT_CREATE', 'Create department', '/departments', 'POST', FALSE),
       ('DEPARTMENT_UPDATE', 'Update department', '/departments', 'PUT', FALSE),
       ('DEPARTMENT_DELETE', 'Delete department', '/departments', 'DELETE', FALSE),

       ('EDUCATIONLEVEL_LIST', 'List education levels', '/education-levels', 'GET', FALSE),
       ('EDUCATIONLEVEL_CREATE', 'Create education level', '/education-levels', 'POST', FALSE),
       ('EDUCATIONLEVEL_UPDATE', 'Update education level', '/education-levels', 'PUT', FALSE),
       ('EDUCATIONLEVEL_DELETE', 'Delete education level', '/education-levels', 'DELETE', FALSE),

       ('ATTENDANCE_TYPE_LIST', 'List attendance types', '/attendance-types', 'GET', FALSE),
       ('ATTENDANCE_TYPE_CREATE', 'Create attendance type', '/attendance-types', 'POST', FALSE),
       ('ATTENDANCE_TYPE_UPDATE', 'Update attendance type', '/attendance-types', 'PUT', FALSE),
       ('ATTENDANCE_TYPE_DELETE', 'Delete attendance type', '/attendance-types', 'DELETE', FALSE),

       ('ATTENDANCE_MACHINE_LIST', 'List attendance machines', '/attendance-machines', 'GET', FALSE),
       ('ATTENDANCE_MACHINE_CREATE', 'Create attendance machine', '/attendance-machines', 'POST', FALSE),
       ('ATTENDANCE_MACHINE_UPDATE', 'Update attendance machine', '/attendance-machines', 'PUT', FALSE),
       ('ATTENDANCE_MACHINE_DELETE', 'Delete attendance machine', '/attendance-machines', 'DELETE', FALSE),

       ('EMP_ATTEND_MACH_LIST', 'List employee attendance machines', '/employee-attendance-machines', 'GET', FALSE),
       ('EMP_ATTEND_MACH_CREATE', 'Create employee attendance machine', '/employee-attendance-machines', 'POST', FALSE),
       ('EMP_ATTEND_MACH_UPDATE', 'Update employee attendance machine', '/employee-attendance-machines', 'PUT', FALSE),
       ('EMP_ATTEND_MACH_DELETE', 'Delete employee attendance machine', '/employee-attendance-machines', 'DELETE',
        FALSE),

       ('EMPLOYEE_LIST', 'List employees', '/employees', 'GET', FALSE),
       ('EMPLOYEE_CREATE', 'Create employee', '/employees', 'POST', FALSE),
       ('EMPLOYEE_UPDATE', 'Update employee', '/employees', 'PUT', FALSE),
       ('EMPLOYEE_DELETE', 'Delete employee', '/employees', 'DELETE', FALSE),

       ('EMPLOYEE_EDU_LIST', 'List employee educations', '/employee-educations', 'GET', FALSE),
       ('EMPLOYEE_EDU_CREATE', 'Create employee education', '/employee-educations', 'POST', FALSE),
       ('EMPLOYEE_EDU_UPDATE', 'Update employee education', '/employee-educations', 'PUT', FALSE),
       ('EMPLOYEE_EDU_DELETE', 'Delete employee education', '/employee-educations', 'DELETE', FALSE),

       ('EMPLOYEE_DECISION_LIST', 'List employee decisions', '/employee-decisions', 'GET', FALSE),
       ('EMPLOYEE_DECISION_CREATE', 'Create employee decision', '/employee-decisions', 'POST', FALSE),
       ('EMPLOYEE_DECISION_UPDATE', 'Update employee decision', '/employee-decisions', 'PUT', FALSE),
       ('EMPLOYEE_DECISION_DELETE', 'Delete employee decision', '/employee-decisions', 'DELETE', FALSE),

       ('EMP_WORK_LOCATION_LIST', 'List employee work locations', '/employee-work-locations', 'GET', FALSE),
       ('EMP_WORK_LOCATION_CREATE', 'Create employee work location', '/employee-work-locations', 'POST', FALSE),
       ('EMP_WORK_LOCATION_UPDATE', 'Update employee work location', '/employee-work-locations', 'PUT', FALSE),
       ('EMP_WORK_LOCATION_DELETE', 'Delete employee work location', '/employee-work-locations', 'DELETE', FALSE),

       ('EMP_WORK_SHIFT_LIST', 'List employee work shifts', '/employee-work-shifts', 'GET', FALSE),
       ('EMP_WORK_SHIFT_CREATE', 'Create employee work shift', '/employee-work-shifts', 'POST', FALSE),
       ('EMP_WORK_SHIFT_UPDATE', 'Update employee work shift', '/employee-work-shifts', 'PUT', FALSE),
       ('EMP_WORK_SHIFT_DELETE', 'Delete employee work shift', '/employee-work-shifts', 'DELETE', FALSE)
;

-- Assign permissions to roles
INSERT IGNORE INTO role_permission (role_id, permission_id)
SELECT r.role_id, p.permission_id
FROM role r
         CROSS JOIN permission p
WHERE r.short_name = 'ROLE_DDC_SUPER_ADMIN';

INSERT IGNORE INTO role_permission (role_id, permission_id)
SELECT (SELECT role_id FROM role WHERE short_name = 'ROLE_DDC_HR_ADMIN'), p.permission_id
FROM permission p
WHERE p.url LIKE '/%';

INSERT IGNORE INTO role_permission (role_id, permission_id)
SELECT (SELECT role_id FROM role WHERE short_name = 'ROLE_DDC_HR_MANAGER'), p.permission_id
FROM permission p
WHERE p.url LIKE '/%'
  AND p.method IN ('GET', 'POST', 'PUT');

INSERT IGNORE INTO role_permission (role_id, permission_id)
SELECT (SELECT role_id FROM role WHERE short_name = 'ROLE_DDC_HR_VIEWER'), p.permission_id
FROM permission p
WHERE p.url LIKE '/%'
  AND p.method = 'GET';

INSERT IGNORE INTO role_permission (role_id, permission_id)
SELECT (SELECT role_id FROM role WHERE short_name = 'ROLE_DDC_API_CLIENT'), p.permission_id
FROM permission p
WHERE p.is_public = TRUE;
