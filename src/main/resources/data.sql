INSERT INTO role (id, name)
VALUES (1, 'ROLE_REGULAR');
INSERT INTO role (id, name)
VALUES (2, 'ROLE_PATIENT');
INSERT INTO role (id, name)
VALUES (3, 'ROLE_PRACTITIONER');
INSERT INTO role (id, name)
VALUES (4, 'ROLE_ADMIN');

/* ************************************ PERMISSIONS ************************************ */
INSERT INTO permission
VALUES (1, 'get_all_users');
INSERT INTO permission
VALUES (2, 'register_practitioner');
INSERT INTO permission
VALUES (3, 'find_record_with_personalId');
INSERT INTO permission
VALUES (4, 'get_access_request_for_user');
INSERT INTO permission
VALUES (5, 'send_access_request');
INSERT INTO permission
VALUES (6, 'get_access_request_for_user');
INSERT INTO permission
VALUES (7, 'get_access_requests_by_reviewed');
INSERT INTO permission
VALUES (8, 'review_access_request');
INSERT INTO permission
VALUES (9, 'get_recent_requests');
INSERT INTO permission
VALUES (10, 'get_access_requests_by_status');
INSERT INTO permission
VALUES (11, 'get_request_history');
INSERT INTO permission
VALUES (12, 'get_patient_record');
INSERT INTO permission
VALUES (13, 'get_all_available_access_requests');
INSERT INTO permission
VALUES (14, 'new_encounter');
INSERT INTO permission
VALUES (15, 'end_encounter');
INSERT INTO permission
VALUES (16, 'verify_integrity');
INSERT INTO permission
VALUES (17, 'get_medication_history');
INSERT INTO permission
VALUES (18, 'get_condition_history');
INSERT INTO permission
VALUES (19, 'get_allergy_history');
INSERT INTO permission
VALUES (20, 'get_all_patients');
INSERT INTO permission
VALUES (21, 'find_record_with_userId');
INSERT INTO permission
VALUES (22, 'get_access_log');
INSERT INTO permission
VALUES (23, 'get_my_record');

/* ========= ADMIN ========= */
INSERT INTO role_permissions
VALUES (1, 4); /* admin <- get_all_users*/
INSERT INTO role_permissions
VALUES (2, 4);/* admin <- register_practitioner*/

/* ====== PRACTITIONER ====== */
INSERT INTO role_permissions
VALUES (3, 3); /* practitioner <- find_record_with_personalId*/
INSERT INTO role_permissions
VALUES (4, 3); /* practitioner <- find_access_request*/
INSERT INTO role_permissions
VALUES (5, 3);/* practitioner <- send_access_request*/
INSERT INTO role_permissions
VALUES (7, 3);/* practitioner <- get_access_requests_by_reviewed*/
INSERT INTO role_permissions
VALUES (9, 3);/* practitioner <- get_recent_requests*/
INSERT INTO role_permissions
VALUES (10, 3);/* practitioner <- get_access_requests_by_status*/
INSERT INTO role_permissions
VALUES (11, 3);/* practitioner <- get_request_history*/
INSERT INTO role_permissions
VALUES (12, 3);/* practitioner <- get_patient_record*/
INSERT INTO role_permissions
VALUES (13, 3);/* practitioner <- get_all_available_patient_record*/
INSERT INTO role_permissions
VALUES (14, 3);/* practitioner <- new_encounter*/
INSERT INTO role_permissions
VALUES (15, 3);/* practitioner <- end_encounter*/
INSERT INTO role_permissions
VALUES (17, 3);/* practitioner <- get_medication_history*/
INSERT INTO role_permissions
VALUES (18, 3);/* practitioner <- get_condition_history*/
INSERT INTO role_permissions
VALUES (19, 3);/* practitioner <- get_allergy_history*/
INSERT INTO role_permissions
VALUES (20, 3);/* practitioner <- get_all_patients*/
INSERT INTO role_permissions
VALUES (21, 3);/* practitioner <- find_record_with_userId*/
INSERT INTO role_permissions
VALUES (22, 3);/* practitioner <- get_access_log*/

/* ========= PATIENT ========= */
INSERT INTO role_permissions
VALUES (6, 2);/* patient <- find_access_request*/
INSERT INTO role_permissions
VALUES (7, 2);/* patient <- get_access_requests_by_reviewed*/
INSERT INTO role_permissions
VALUES (8, 2);/* patient <- review_access_request*/
INSERT INTO role_permissions
VALUES (9, 2);/* patient <- get_recent_requests*/
INSERT INTO role_permissions
VALUES (10, 2);/* patient <- get_access_requests_by_status*/
INSERT INTO role_permissions
VALUES (11, 2);/* patient <- get_request_history*/
INSERT INTO role_permissions
VALUES (12, 2);/* patient <- get_patient_record*/
INSERT INTO role_permissions
VALUES (16, 2);/* patient <- verify_integrity*/
INSERT INTO role_permissions
VALUES (22, 2);/* patient <- get_access_log*/
INSERT INTO role_permissions
VALUES (23, 2);/* practitioner <- get_my_record*/

/* ************************************* SPECIALTY ************************************* */
INSERT INTO specialty
VALUES (1, 'NEURO', 'Neurologist');
INSERT INTO specialty
VALUES (2, 'PATHO', 'Pathologist');
INSERT INTO specialty
VALUES (3, 'PEDS', 'Pediatrician');
INSERT INTO specialty
VALUES (4, 'CARDIO', 'Cardiologist');
INSERT INTO specialty
VALUES (5, 'ORTHO', 'Orthopediatrician');
INSERT INTO specialty
VALUES (6, 'PULMONO', 'Pulmonologist');
INSERT INTO specialty
VALUES (7, 'GYN', 'Gynaecologist');
INSERT INTO specialty
VALUES (8, 'DERM', 'Dermatologist');