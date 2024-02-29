INSERT INTO role (id, name)
VALUES (1, 'ROLE_REGULAR');
INSERT INTO role (id, name)
VALUES (2, 'ROLE_PATIENT');
INSERT INTO role (id, name)
VALUES (3, 'ROLE_PRACTITIONER');
INSERT INTO role (id, name)
VALUES (4, 'ROLE_ADMIN');

/* ************************************ PERMISSIONS ************************************ */
/* ========= ADMIN ========= */
INSERT INTO permission
VALUES (1, 'get_all_users');
INSERT INTO role_permissions
VALUES (1, 4); /* admin <- get_all_users*/

INSERT INTO permission
VALUES (2, 'register_practitioner');
INSERT INTO role_permissions
VALUES (2, 4);
/* admin <- register_practitioner*/

/* ====== PRACTITIONER ====== */
INSERT INTO permission
VALUES (3, 'find_record_with_personalId');
INSERT INTO role_permissions
VALUES (3, 3); /* practitioner <- find_record_with_personalId*/

INSERT INTO permission
VALUES (4, 'find_access_request');
INSERT INTO role_permissions
VALUES (4, 3); /* practitioner <- find_access_request*/

INSERT INTO permission
VALUES (5, 'send_access_request');
INSERT INTO role_permissions
VALUES (5, 3);
/* practitioner <- send_access_request*/

/* ========= PATIENT ========= */
INSERT INTO permission
VALUES (6, 'find_access_request');
INSERT INTO role_permissions
VALUES (6, 2);
/* patient <- find_access_request*/

/* ************************************* SPECIALTY ************************************* */
INSERT INTO specialty
VALUES (1, 'NEURO', 'Neurology');
INSERT INTO specialty
VALUES (2, 'PATHO', 'Pathology');
INSERT INTO specialty
VALUES (3, 'PEDS', 'Pediatrics');
INSERT INTO specialty
VALUES (4, 'CARDIO', 'Cardiology');
INSERT INTO specialty
VALUES (5, 'ORTHO', 'Orthopedics');
INSERT INTO specialty
VALUES (6, 'PULMONO', 'Pulmonology');
INSERT INTO specialty
VALUES (7, 'GYN', 'Gynaecology');
INSERT INTO specialty
VALUES (8, 'DERM', 'Dermatology');