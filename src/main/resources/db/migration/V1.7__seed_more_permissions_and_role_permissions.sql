-- V1.7__seed_more_permissions_and_role_permissions.sql

INSERT IGNORE INTO permission (short_name, description, url, method, is_public)
VALUES ('EXPENSE_TYPE_LIST', 'List expense-types', '/expense-types', 'GET', FALSE),
       ('EXPENSE_TYPE_CREATE', 'Create expense-type', '/expense-types', 'POST', FALSE),
       ('EXPENSE_TYPE_UPDATE', 'Update expense-type', '/expense-types', 'PUT', FALSE),
       ('EXPENSE_TYPE_DELETE', 'Delete expense-type', '/expense-types', 'DELETE', FALSE),

       ('EMPLOYEE_TYPE_LIST', 'List employee types', '/employee-types', 'GET', FALSE),
       ('EMPLOYEE_TYPE_CREATE', 'Create employee type', '/employee-types', 'POST', FALSE),
       ('EMPLOYEE_TYPE_UPDATE', 'Update employee type', '/employee-types', 'PUT', FALSE),
       ('EMPLOYEE_TYPE_DELETE', 'Delete employee type', '/employee-types', 'DELETE', FALSE),

       ('LABOR_STATUS_LIST', 'List labor statuses', '/labor-statuses', 'GET', FALSE),
       ('LABOR_STATUS_CREATE', 'Create labor status', '/labor-statuses', 'POST', FALSE),
       ('LABOR_STATUS_UPDATE', 'Update labor status', '/labor-statuses', 'PUT', FALSE),
       ('LABOR_STATUS_DELETE', 'Delete labor status', '/labor-statuses', 'DELETE', FALSE),

       ('JOB_RANK_LIST', 'List job ranks', '/job-ranks', 'GET', FALSE),
       ('JOB_RANK_CREATE', 'Create job rank', '/job-ranks', 'POST', FALSE),
       ('JOB_RANK_UPDATE', 'Update job rank', '/job-ranks', 'PUT', FALSE),
       ('JOB_RANK_DELETE', 'Delete job rank', '/job-ranks', 'DELETE', FALSE),

       ('JOB_TITLE_LIST', 'List job titles', '/job-titles', 'GET', FALSE),
       ('JOB_TITLE_CREATE', 'Create job title', '/job-titles', 'POST', FALSE),
       ('JOB_TITLE_UPDATE', 'Update job title', '/job-titles', 'PUT', FALSE),
       ('JOB_TITLE_DELETE', 'Delete job title', '/job-titles', 'DELETE', FALSE),

       ('JOB_POSITION_LIST', 'List job positions', '/job-positions', 'GET', FALSE),
       ('JOB_POSITION_CREATE', 'Create job position', '/job-positions', 'POST', FALSE),
       ('JOB_POSITION_UPDATE', 'Update job position', '/job-positions', 'PUT', FALSE),
       ('JOB_POSITION_DELETE', 'Delete job position', '/job-positions', 'DELETE', FALSE),

       ('LANGUAGE_LIST', 'List languages', '/languages', 'GET', FALSE),
       ('LANGUAGE_CREATE', 'Create language', '/languages', 'POST', FALSE),
       ('LANGUAGE_UPDATE', 'Update language', '/languages', 'PUT', FALSE),
       ('LANGUAGE_DELETE', 'Delete language', '/languages', 'DELETE', FALSE),

       ('NATIONALITY_LIST', 'List nationalities', '/nationalities', 'GET', FALSE),
       ('NATIONALITY_CREATE', 'Create nationality', '/nationalities', 'POST', FALSE),
       ('NATIONALITY_UPDATE', 'Update nationality', '/nationalities', 'PUT', FALSE),
       ('NATIONALITY_DELETE', 'Delete nationality', '/nationalities', 'DELETE', FALSE),

       ('MARITAL_STATUS_LIST', 'List marital statuses', '/marital-statuses', 'GET', FALSE),
       ('MARITAL_STATUS_CREATE', 'Create marital status', '/marital-statuses', 'POST', FALSE),
       ('MARITAL_STATUS_UPDATE', 'Update marital status', '/marital-statuses', 'PUT', FALSE),
       ('MARITAL_STATUS_DELETE', 'Delete marital status', '/marital-statuses', 'DELETE', FALSE),

       ('MAJOR_LIST', 'List majors', '/majors', 'GET', FALSE),
       ('MAJOR_CREATE', 'Create major', '/majors', 'POST', FALSE),
       ('MAJOR_UPDATE', 'Update major', '/majors', 'PUT', FALSE),
       ('MAJOR_DELETE', 'Delete major', '/majors', 'DELETE', FALSE),

       ('SPECIALIZATION_LIST', 'List specializations', '/specializations', 'GET', FALSE),
       ('SPECIALIZATION_CREATE', 'Create specialization', '/specializations', 'POST', FALSE),
       ('SPECIALIZATION_UPDATE', 'Update specialization', '/specializations', 'PUT', FALSE),
       ('SPECIALIZATION_DELETE', 'Delete specialization', '/specializations', 'DELETE', FALSE),

       ('SCHOOL_LIST', 'List schools', '/schools', 'GET', FALSE),
       ('SCHOOL_CREATE', 'Create school', '/schools', 'POST', FALSE),
       ('SCHOOL_UPDATE', 'Update school', '/schools', 'PUT', FALSE),
       ('SCHOOL_DELETE', 'Delete school', '/schools', 'DELETE', FALSE),

       ('OLD_WARD_LIST', 'List old wards', '/old-wards', 'GET', FALSE),
       ('OLD_WARD_CREATE', 'Create old ward', '/old-wards', 'POST', FALSE),
       ('OLD_WARD_UPDATE', 'Update old ward', '/old-wards', 'PUT', FALSE),
       ('OLD_WARD_DELETE', 'Delete old ward', '/old-wards', 'DELETE', FALSE),

       ('OLD_DISTRICT_LIST', 'List old districts', '/old-districts', 'GET', FALSE),
       ('OLD_DISTRICT_CREATE', 'Create old district', '/old-districts', 'POST', FALSE),
       ('OLD_DISTRICT_UPDATE', 'Update old district', '/old-districts', 'PUT', FALSE),
       ('OLD_DISTRICT_DELETE', 'Delete old district', '/old-districts', 'DELETE', FALSE),

       ('OLD_PROVINCE_CITY_LIST', 'List old province cities', '/old-province-cities', 'GET', FALSE),
       ('OLD_PROVINCE_CITY_CREATE', 'Create old province city', '/old-province-cities', 'POST', FALSE),
       ('OLD_PROVINCE_CITY_UPDATE', 'Update old province city', '/old-province-cities', 'PUT', FALSE),
       ('OLD_PROVINCE_CITY_DELETE', 'Delete old province city', '/old-province-cities', 'DELETE',
        FALSE),

       ('WARD_LIST', 'List wards', '/wards', 'GET', FALSE),
       ('WARD_CREATE', 'Create ward', '/wards', 'POST', FALSE),
       ('WARD_UPDATE', 'Update ward', '/wards', 'PUT', FALSE),
       ('WARD_DELETE', 'Delete ward', '/wards', 'DELETE', FALSE),

       ('PROVINCE_CITY_LIST', 'List province cities', '/province-cities', 'GET', FALSE),
       ('PROVINCE_CITY_CREATE', 'Create province city', '/province-cities', 'POST', FALSE),
       ('PROVINCE_CITY_UPDATE', 'Update province city', '/province-cities', 'PUT', FALSE),
       ('PROVINCE_CITY_DELETE', 'Delete province city', '/province-cities', 'DELETE', FALSE),

       ('IDENTITY_ISSUING_AUTHORITY_LIST', 'List identity issuing authorities', '/identity-issuing-authorities', 'GET',
        FALSE),
       ('IDENTITY_ISSUING_AUTHORITY_CREATE', 'Create identity issuing authority', '/identity-issuing-authorities',
        'POST', FALSE),
       ('IDENTITY_ISSUING_AUTHORITY_UPDATE', 'Update identity issuing authority', '/identity-issuing-authorities',
        'PUT', FALSE),
       ('IDENTITY_ISSUING_AUTHORITY_DELETE', 'Delete identity issuing authority', '/identity-issuing-authorities',
        'DELETE', FALSE),

       ('WORK_SHIFT_GROUP_LIST', 'List work shift groups', '/work-shift-groups', 'GET', FALSE),
       ('WORK_SHIFT_GROUP_CREATE', 'Create work shift group', '/work-shift-groups', 'POST', FALSE),
       ('WORK_SHIFT_GROUP_UPDATE', 'Update work shift group', '/work-shift-groups', 'PUT', FALSE),
       ('WORK_SHIFT_GROUP_DELETE', 'Delete work shift group', '/work-shift-groups', 'DELETE', FALSE),

       ('WORK_SHIFT_LIST', 'List work shifts', '/work-shifts', 'GET', FALSE),
       ('WORK_SHIFT_CREATE', 'Create work shift', '/work-shifts', 'POST', FALSE),
       ('WORK_SHIFT_UPDATE', 'Update work shift', '/work-shifts', 'PUT', FALSE),
       ('WORK_SHIFT_DELETE', 'Delete work shift', '/work-shifts', 'DELETE', FALSE),

       ('OT_TYPE_LIST', 'List OT types', '/ot-types', 'GET', FALSE),
       ('OT_TYPE_CREATE', 'Create OT type', '/ot-types', 'POST', FALSE),
       ('OT_TYPE_UPDATE', 'Update OT type', '/ot-types', 'PUT', FALSE),
       ('OT_TYPE_DELETE', 'Delete OT type', '/ot-types', 'DELETE', FALSE),

       ('MEDICAL_FACILITY_LIST', 'List medical facilities', '/medical-facilities', 'GET', FALSE),
       ('MEDICAL_FACILITY_CREATE', 'Create medical facility', '/medical-facilities', 'POST', FALSE),
       ('MEDICAL_FACILITY_UPDATE', 'Update medical facility', '/medical-facilities', 'PUT', FALSE),
       ('MEDICAL_FACILITY_DELETE', 'Delete medical facility', '/medical-facilities', 'DELETE', FALSE)
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
