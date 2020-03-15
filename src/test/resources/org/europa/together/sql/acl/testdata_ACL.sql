-- #### #######################################################################
-- ####                POPULATE ACL
-- #### #######################################################################
--- EMPTY TABLES
TRUNCATE ROLES, ACCOUNT, LOGIN, PERMISSIONS, RESOURCES;

--- RESOURCE
INSERT INTO RESOURCES (RESOURCE, TEMPLATE, ACTIONS, DELETEABLE) VALUES ('Document', 'default', 'ALL', false);
INSERT INTO RESOURCES (RESOURCE, TEMPLATE, ACTIONS, DELETEABLE) VALUES ('Article', 'default', 'ALL', false);
INSERT INTO RESOURCES (RESOURCE, TEMPLATE, ACTIONS, DELETEABLE) VALUES ('Article', 'delete', 'ALL', true);
INSERT INTO RESOURCES (RESOURCE, TEMPLATE, ACTIONS, DELETEABLE) VALUES ('Article', 'teaser', 'ALL', true);

--- ROLES
INSERT INTO ROLES (NAME, DELETEABLE, DESCRIPTION) VALUES ('Sample', true, 'Role to test delete when Permission exist.');
INSERT INTO ROLES (NAME, DELETEABLE, DESCRIPTION) VALUES ('Temp', true, 'Role to test delete when Permission exist.');
INSERT INTO ROLES (NAME, DELETEABLE, DESCRIPTION) VALUES ('Guest', false, 'Default Role for all new Users.');
INSERT INTO ROLES (NAME, DELETEABLE, DESCRIPTION) VALUES ('User', false, 'After a simple validation process of an existing EMail address.');
INSERT INTO ROLES (NAME, DELETEABLE, DESCRIPTION) VALUES ('Moderator', true, 'Higher privileged role than a standard user.');
INSERT INTO ROLES (NAME, DELETEABLE, DESCRIPTION) VALUES ('Administrator', false, 'Full privilege.');

-- ACCOUNT
INSERT INTO ACCOUNT (EMAIL, PASSWORD, ROLE_NAME, REGISTRATION_DATE, VERIFIED, ACTIVATED, VERIFICATION_CODE)
    VALUES ('guest@sample.org', '', 'Guest', '1984-04-01 12:00:01.0', true, true, '03a057d0-6ee7-43b1-ac68-d9a7681165a4');
INSERT INTO ACCOUNT (EMAIL, PASSWORD, ROLE_NAME, REGISTRATION_DATE, VERIFIED, ACTIVATED, VERIFICATION_CODE)
    VALUES ('user@sample.org', '', 'User', '1984-04-01 12:00:01.0', true, false, 'a674b42e-6670-4674-a8c7-8e86132eaaa2');
INSERT INTO ACCOUNT (EMAIL, PASSWORD, ROLE_NAME, REGISTRATION_DATE, VERIFIED, ACTIVATED, VERIFICATION_CODE)
    VALUES ('moderator@sample.org', '', 'Moderator', '1984-04-01 12:00:01.0', true, false, '3644b892-1438-45e4-87c5-0ea140fea92b');

INSERT INTO ACCOUNT (EMAIL, PASSWORD, ROLE_NAME, REGISTRATION_DATE, VERIFIED, ACTIVATED, VERIFICATION_CODE)
    VALUES ('moderator_01@sample.org', '', 'Moderator', '1984-04-01 12:00:01.0', false, true, '46b25a22-69b6-43eb-a711-1dcf1ed0eb5e');
INSERT INTO ACCOUNT (EMAIL, PASSWORD, ROLE_NAME, REGISTRATION_DATE, VERIFIED, ACTIVATED, VERIFICATION_CODE)
    VALUES ('moderator_02@sample.org', '', 'Moderator', '1984-04-01 12:00:01.0', true, false, 'fda79c72-59de-430d-8d5f-8451c7a00c04');
INSERT INTO ACCOUNT (EMAIL, PASSWORD, ROLE_NAME, REGISTRATION_DATE, VERIFIED, ACTIVATED, VERIFICATION_CODE)
    VALUES ('admin@sample.org', '', 'Administrator', '1984-04-01 12:00:01.0', true, true, '2b421f4c-4408-4f99-b1aa-25002b85ea87');

--- #### LOGIN
INSERT INTO LOGIN (IDX, EMAIL, LOGIN_DATE, IPADRESS, BROWSER_ID, OPERATION_SYSTEM)
    VALUES ('03a057d0-6ee7-43b1-ac68-d9a7681165a4', 'moderator@sample.org', '2010-06-15 12:00:01.0', '127.0.0.1', '', '');
INSERT INTO LOGIN (IDX, EMAIL, LOGIN_DATE, IPADRESS, BROWSER_ID, OPERATION_SYSTEM)
    VALUES ('a674b42e-6670-4674-a8c7-8e86132eaaa2', 'moderator@sample.org', '2010-06-17 12:00:01.0', 'localhost', '', '');
INSERT INTO LOGIN (IDX, EMAIL, LOGIN_DATE, IPADRESS, BROWSER_ID, OPERATION_SYSTEM)
    VALUES ('2b421f4c-4408-4f99-b1aa-25002b85ea87', 'moderator@sample.org', '2010-06-25 12:00:01.0', '127.0.0.1', '', '');

--- ### PERMISSION
INSERT INTO PERMISSIONS (IDX, ROLE_NAME, RESOURCE_RESOURCE, RESOURCE_TEMPLATE, DO_READ, DO_CHANGE, DO_CREATE, DO_DELETE)
    VALUES ('1f4c2b42-4408-4f99-b1aa-25002b85ea87', 'Guest', 'Article', 'teaser', true, false, false, false);
INSERT INTO PERMISSIONS (IDX, ROLE_NAME, RESOURCE_RESOURCE, RESOURCE_TEMPLATE, DO_READ, DO_CHANGE, DO_CREATE, DO_DELETE)
    VALUES ('5d1da892-6316-4781-94ea-d82d8a15e350', 'User', 'Article', 'default', true, true, true, true);
INSERT INTO PERMISSIONS (IDX, ROLE_NAME, RESOURCE_RESOURCE, RESOURCE_TEMPLATE, DO_READ, DO_CHANGE, DO_CREATE, DO_DELETE)
    VALUES ('eb7bb730-95a0-45ac-983c-258b7a56f1f4', 'User', 'Document', 'default', true, true, true, true);
INSERT INTO PERMISSIONS (IDX, ROLE_NAME, RESOURCE_RESOURCE, RESOURCE_TEMPLATE, DO_READ, DO_CHANGE, DO_CREATE, DO_DELETE)
    VALUES ('9b5e3e50-4fdb-46de-bbed-8e011d35cfe8', 'Sample', 'Article', 'teaser', true, true, true, true);
