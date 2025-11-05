-- V1.5__seed_System_User_data.sql
-- Seed RBAC data for Human Resource domain

-- Roles
INSERT INTO role (short_name, description, note)
VALUES ('ROLE_DDC_SUPER_ADMIN', 'Full system admin', 'system'),
       ('ROLE_DDC_HR_ADMIN', 'Human Resource admin', ''),
       ('ROLE_DDC_HR_MANAGER', 'HR manager', ''),
       ('ROLE_DDC_HR_VIEWER', 'HR viewer (read only)', ''),
       ('ROLE_DDC_API_CLIENT', 'API client (public endpoints)', '');

-- Permissions (example)
INSERT INTO permission (short_name, description, url, method, is_public)
VALUES ('EMPLOYEE_LIST', 'List employees', '/employees', 'GET', FALSE),
       ('EMPLOYEE_CREATE', 'Create employee', '/employees', 'POST', FALSE),
       ('EMPLOYEE_UPDATE', 'Update employee', '/employees', 'PUT', FALSE),
       ('EMPLOYEE_DELETE', 'Delete employee', '/employees', 'DELETE', FALSE),
       ('DEPT_LIST', 'List departments', '/departments', 'GET', FALSE),
       ('OPENAPI_JSON', 'OpenAPI JSON', '/v3/api-docs', 'GET', TRUE),
       ('SWAGGER_UI', 'Swagger UI', '/swagger-ui/**', 'GET', TRUE),
       ('HEALTH', 'Health', '/actuator/health', 'GET', TRUE);

-- Map permissions -> roles
-- Super admin gets all
INSERT INTO role_permission (role_id, permission_id)
SELECT r.role_id, p.permission_id
FROM role r
         CROSS JOIN permission p
WHERE r.short_name = 'ROLE_DDC_SUPER_ADMIN';

-- HR_ADMIN gets API permissions for /api/v1/*
INSERT INTO role_permission (role_id, permission_id)
SELECT (SELECT role_id FROM role WHERE short_name = 'ROLE_DDC_HR_ADMIN'), p.permission_id
FROM permission p
WHERE p.url LIKE '/%';

-- HR_MANAGER: read + create/update
INSERT INTO role_permission (role_id, permission_id)
SELECT (SELECT role_id FROM role WHERE short_name = 'ROLE_DDC_HR_MANAGER'), p.permission_id
FROM permission p
WHERE p.url LIKE '/%'
  AND p.method IN ('GET', 'POST', 'PUT');

-- HR_VIEWER: read only
INSERT INTO role_permission (role_id, permission_id)
SELECT (SELECT role_id FROM role WHERE short_name = 'ROLE_DDC_HR_VIEWER'), p.permission_id
FROM permission p
WHERE p.url LIKE '/%'
  AND p.method = 'GET';

-- API_CLIENT: public
INSERT INTO role_permission (role_id, permission_id)
SELECT (SELECT role_id FROM role WHERE short_name = 'ROLE_DDC_API_CLIENT'), p.permission_id
FROM permission p
WHERE p.is_public = TRUE;

-- NOTE:
-- For initial users, DO NOT insert authorization_service_user_id manually as Keycloak UUID.
-- Instead create users in Keycloak first, take their Keycloak ID, then insert user rows with that id,
-- or better: use the app's UserController which creates user in Keycloak and persists DB with returned id.
