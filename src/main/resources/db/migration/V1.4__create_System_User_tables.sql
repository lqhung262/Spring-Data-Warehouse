-- =====================================================================
-- V1.4__create_System_User_tables.sql
-- Tạo các bảng RBAC: user, role, permission, role_permission, user_role
-- =====================================================================

-- SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;

-- =========================
-- USER
-- =========================
CREATE TABLE user
(
    user_id                       BIGINT PRIMARY KEY AUTO_INCREMENT,
    authorization_service_user_id VARCHAR(100) NOT NULL,
    user_name                     VARCHAR(100) NOT NULL,
    full_name                     VARCHAR(255),
    email_address                 VARCHAR(150) NOT NULL,
    is_enabled                    BOOLEAN      NOT NULL DEFAULT TRUE,
    created_by                    BIGINT       NOT NULL,
    created_at                    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by                    BIGINT,
    updated_at                    DATETIME              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uq_user_authz_id UNIQUE (authorization_service_user_id),
    CONSTRAINT uq_user_username UNIQUE (user_name),
    CONSTRAINT uq_user_email UNIQUE (email_address),

    CONSTRAINT fk_user_created_by FOREIGN KEY (created_by) REFERENCES user_profile (user_profile_id),
    CONSTRAINT fk_user_updated_by FOREIGN KEY (updated_by) REFERENCES user_profile (user_profile_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;


-- =========
-- ROLE
-- =========
CREATE TABLE role
(
    role_id     BIGINT PRIMARY KEY AUTO_INCREMENT,
    short_name  VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    note        VARCHAR(255),

    CONSTRAINT uq_role_short_name UNIQUE (short_name)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- ==============
-- PERMISSION
-- ==============
CREATE TABLE permission
(
    permission_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    short_name    VARCHAR(100) NOT NULL,
    description   VARCHAR(255),
    url           VARCHAR(255) NOT NULL,
    method        VARCHAR(10)  NOT NULL,
    is_public     BOOLEAN      NOT NULL DEFAULT FALSE,

    CONSTRAINT uq_perm_short_name UNIQUE (short_name),
    CONSTRAINT uq_perm_url_method UNIQUE (url, method),
    CONSTRAINT chk_perm_method CHECK (method IN ('GET', 'POST', 'PATCH', 'PUT', 'DELETE'))
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;


-- =================
-- ROLE_PERMISSION
-- =================
CREATE TABLE role_permission
(
    role_permission_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_id            BIGINT NOT NULL,
    permission_id      BIGINT NOT NULL,

    CONSTRAINT uq_role_permission UNIQUE (role_id, permission_id),
    CONSTRAINT fk_rp_role FOREIGN KEY (role_id) REFERENCES `role` (role_id) ON DELETE CASCADE,
    CONSTRAINT fk_rp_permission FOREIGN KEY (permission_id) REFERENCES `permission` (permission_id) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;



-- ===========
-- USER_ROLE
-- ===========
CREATE TABLE user_role
(
    user_role_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id      BIGINT NOT NULL,
    role_id      BIGINT NOT NULL,

    CONSTRAINT uq_user_role UNIQUE (user_id, role_id),
    CONSTRAINT fk_ur_user FOREIGN KEY (user_id) REFERENCES `user` (user_id) ON DELETE CASCADE,
    CONSTRAINT fk_ur_role FOREIGN KEY (role_id) REFERENCES `role` (role_id) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

