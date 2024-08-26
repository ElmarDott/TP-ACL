--- #### #######################################################################
--- ####                POPULATE CONFIGURATION TABLE
--- #### #######################################################################
---  Notes: CONF-KEY in table APP_CONFIG is SHA-256 protected

--- #### ACL
--- password.pattern
INSERT INTO public.app_config (IDX, CONF_KEY, CONF_VALUE, DEFAULT_VALUE, CONF_SET, MODUL_NAME, SERVICE_VERSION, MANDATORY, DEPRECATED, COMMENT)
VALUES ('282c3297-e55d-481e-90a6-d8183427392a', 'edc13c9cae26130b488eca5e211df44296e1d46b493e8935905dcbf97124175b',
'[A-Za-z0-9]{10,20}', '#password.pattern#}', 'conf', 'acl', '1.0', true, false, '');

-- session.timeout
INSERT INTO public.app_config (IDX, CONF_KEY, CONF_VALUE, DEFAULT_VALUE, CONF_SET, MODUL_NAME, SERVICE_VERSION, MANDATORY, DEPRECATED, COMMENT)
VALUES ('cd4beaa6-a39f-a688-4349-5bc43d192f26', '655c034affea41acbc012e71a00433bd7ab29f15040865d7fd62cbfb31e79372',
'15s', '#session.timeout#', 'conf', 'acl', '1.0', true, false, '');

-- pepperized
INSERT INTO public.app_config (IDX, CONF_KEY, CONF_VALUE, DEFAULT_VALUE, CONF_SET, MODUL_NAME, SERVICE_VERSION, MANDATORY, DEPRECATED, COMMENT)
VALUES ('3bac223c-36ff-4c91-a9fd-4e72fb8c44ed', 'd6d63469bb936f57dc7d72e5eff2982c4562cb71a9563d1210af90d0981c0bb5',
'', '#pepperized#', 'conf', 'acl', '1.0', true, false, '');

-- iam.server (e.g. KeyCloak)
INSERT INTO public.app_config (IDX, CONF_KEY, CONF_VALUE, DEFAULT_VALUE, CONF_SET, MODUL_NAME, SERVICE_VERSION, MANDATORY, DEPRECATED, COMMENT)
VALUES ('babfd0f0-7d68-48ac-9d2c-571a17050230', 'a8fe0291fc2834b6e951032a426200f3c82a4361fc9e35f3250a8d277fd554b7',
'', '#iam.server#', 'conf', 'acl', '1.0', true, false, '');

-- iam.realm (e.g. MyCompany)
INSERT INTO public.app_config (IDX, CONF_KEY, CONF_VALUE, DEFAULT_VALUE, CONF_SET, MODUL_NAME, SERVICE_VERSION, MANDATORY, DEPRECATED, COMMENT)
VALUES ('433132f4-bffd-4e79-85dc-9b9ac39b1d82', 'c52cebe3c6a3c99741896614b6f0910d4f18470dde23da898edb51fae73d2d67',
'', '#iam.realm#', 'conf', 'acl', '1.0', true, false, '');

