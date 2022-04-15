--- #### #######################################################################
--- ####                POPULATE ACL
--- #### #######################################################################

-- Default Roles
INSERT INTO ROLES (NAME, DELETEABLE, DESCRIPTION) VALUES ('Guest', false, 'Default Role for all new Users.');
INSERT INTO ROLES (NAME, DELETEABLE, DESCRIPTION) VALUES ('User', false, 'After a simple validation process of an existing EMail address.');
INSERT INTO ROLES (NAME, DELETEABLE, DESCRIPTION) VALUES ('Moderator', true, 'Higher privileged role than a standard user.');
INSERT INTO ROLES (NAME, DELETEABLE, DESCRIPTION) VALUES ('Administrator', false, 'Full privilege.');

-- Admin Account
INSERT INTO ACCOUNT (EMAIL, PASSWORD, ROLE_NAME, DEFAULT_LOCALE, DEFAULT_TIMEZONE, REGISTERED, VERIFIED, DEACTIVATED, VERIFICATION_CODE)
    VALUES ('admin@sample.org', '', 'Administrator', 'EN_en', 'UTC+00:00', '1984-04-01 12:00:01.0', '2020-12-31 12:00:01.0', '2021-01-11 12:00:01.0', '03a057d0-6ee7-43b1-ac68-d9a7681165a4');

-- RESOURCE
INSERT INTO RESOURCES (RESOURCE, TEMPLATE, DELETEABLE) VALUES ('Article', 'default', false);
INSERT INTO RESOURCES (RESOURCE, TEMPLATE, DELETEABLE) VALUES ('Article', 'edit', false);

-- PERMISSION
INSERT INTO PERMISSIONS (IDX, ROLE_NAME, RESOURCE_RESOURCE, RESOURCE_TEMPLATE, DO_READ, DO_CHANGE, DO_CREATE, DO_DELETE)
    VALUES ('eb7bb730-95a0-45ac-983c-258b7a56f1f4', 'Administrator', 'Article', 'default', true, true, true, true);