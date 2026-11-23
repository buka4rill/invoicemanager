SET NAMES 'UTF8MB4';
-- Timezone is set to Europe/Berlin via the MySQL container's TZ env var (docker-compose.yml)

DROP TABLE IF EXISTS two_factor_verifications;
DROP TABLE IF EXISTS reset_password_verifications;
DROP TABLE IF EXISTS account_verifications;
DROP TABLE IF EXISTS user_events;
DROP TABLE IF EXISTS events;
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS users;



CREATE TABLE users
(
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    first_name      VARCHAR(50) NOT NULL,
    last_name       VARCHAR(50) NOT NULL,
    email           VARCHAR(100) NOT NULL,
    password        VARCHAR(255) DEFAULT NULL,
    address         VARCHAR(255) DEFAULT NULL,
    phone           VARCHAR(30) DEFAULT NULL,
    title           VARCHAR(50) DEFAULT NULL,
    bio             VARCHAR(255) DEFAULT NULL,
    enabled         BOOLEAN DEFAULT FALSE,
    non_locked      BOOLEAN DEFAULT TRUE,
    using_mfa       BOOLEAN DEFAULT FALSE,
    image_url       VARCHAR(255) DEFAULT 'https://cdn-icons-png.flaticon.com/512/149/149071.png',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT      uq_users_email UNIQUE (email)
);

CREATE TABLE roles
(
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(50) NOT NULL,
    permission      VARCHAR(255) NOT NULL, -- user:read,user:delete,customer:read
    CONSTRAINT      uq_roles_name UNIQUE (name)
);

INSERT INTO roles (name, permission) VALUES
    ('ROLE_USER', 'user:read,customer:read'),
    ('ROLE_MANAGER', 'user:read,user:update,customer:read,customer:update'),
    ('ROLE_ADMIN', 'user:read,user:create,user:update,customer:read,customer:create,customer:update'),
    ('ROLE_SYSADMIN', 'user:read,user:create,user:update,user:delete,customer:read,customer:create,customer:update,customer:delete,system:manage');

CREATE TABLE user_roles
(
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT UNSIGNED NOT NULL,
    role_id         BIGINT UNSIGNED NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT      uq_user_roles_user_id UNIQUE (user_id)
);

CREATE TABLE events
(
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    type            VARCHAR(50) NOT NULL CHECK(type IN ('LOGIN_ATTEMPT', 'LOGIN_ATTEMPT_FAILURE', 'LOGIN_ATTEMPT_SUCCESS', 'PROFILE_UPDATE', 'PROFILE_PICTURE_UPDATE', 'LOGIN_ATTEMPT_FAILURE', 'ROLE_UPDATE', 'ACCOUNT_SETTINGS_UPDATE', 'PASSWORD_UPDATE', 'MFA_UPDATE')),
    description     VARCHAR(255) NOT NULL,
    CONSTRAINT      uq_events_type UNIQUE (type)
);

CREATE TABLE user_events
(
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT UNSIGNED NOT NULL,
    event_id        BIGINT UNSIGNED NOT NULL,
    device          VARCHAR(100) DEFAULT NULL,
    ip_address      VARCHAR(100) DEFAULT NULL,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE account_verifications
(
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT UNSIGNED NOT NULL,
    url             VARCHAR(255) DEFAULT NULL,
    --  expires_at  DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT      uq_account_verifications_user_id UNIQUE (user_id),
    CONSTRAINT      uq_account_verifications_url UNIQUE (url)
);

CREATE TABLE reset_password_verifications
(
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT UNSIGNED NOT NULL,
    url             VARCHAR(255) DEFAULT NULL,
    expires_at      DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT      uq_reset_password_verifications_user_id UNIQUE (user_id),
    CONSTRAINT      uq_reset_password_verifications_url UNIQUE (url)
);

CREATE TABLE two_factor_verifications
(
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT UNSIGNED NOT NULL,
    code            VARCHAR(10) DEFAULT NULL,
    expires_at      DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT      uq_two_factor_verifications_user_id UNIQUE (user_id),
    CONSTRAINT      uq_two_factor_verifications_code UNIQUE (code)
);

