INSERT INTO user_access_status(id, status) VALUES (1, 'STAGED');
INSERT INTO user_access_status(id, status) VALUES (2, 'ACTIVE');
INSERT INTO user_access_status(id, status) VALUES (3, 'SUSPENDED');
INSERT INTO user_access_status(id, status) VALUES (4, 'LOCKED_OUT');
INSERT INTO user_access_status(id, status) VALUES (5, 'ADMIN');
INSERT INTO user_access_status(id, status) VALUES (6, 'DEV');

INSERT INTO platform_user(
        id, created_date, first_name, last_name, dob, email, password, phone_number, user_access_status_id)
    VALUES(1, '2021-12-24', 'John', 'Smith', '1970-02-23', 'staged@example.com', 'Pass1234!', '0287513626', 1);
INSERT INTO platform_user(
        id, created_date, first_name, last_name, dob, email, password, phone_number, user_access_status_id)
    VALUES(2, '2022-01-04', 'Mary', 'Green', '1990-06-06', 'active@example.com', 'Pass1234!', '0286579635', 2);
INSERT INTO platform_user(
        id, created_date, first_name, last_name, dob, email, password, phone_number, user_access_status_id)
    VALUES(3, '2022-01-28', 'Jess', 'Brown', '1986-01-04', 'suspended@example.com', 'Pass1234!', '0283215978', 3);
INSERT INTO platform_user(
        id, created_date, first_name, last_name, dob, email, password, phone_number, user_access_status_id)
    VALUES(4,'2022-02-01', 'Tom', 'Thompson', '2000-09-30', 'locked_out@example.com', 'Pass1234!', '0286782453', 4);
INSERT INTO platform_user(
        id, created_date, first_name, last_name, dob, email, password, phone_number, user_access_status_id)
    VALUES(5, '2022-01-05', 'Jake', 'Sully', '1995-07-08', 'admin@example.com', 'Pass1234!', '0286547891', 5);

INSERT INTO driver(id, license_number, platform_user_id) VALUES(1, '35789654', 1);
INSERT INTO driver(id, license_number, platform_user_id) VALUES(2, '16548329', 2);
INSERT INTO driver(id, license_number, platform_user_id) VALUES(3, '35789126', 3);
INSERT INTO driver(id, license_number, platform_user_id) VALUES(4, '65782349', 4);
INSERT INTO driver(id, license_number, platform_user_id) VALUES(5, '67324982', 5);

INSERT INTO passenger(id, platform_user_id) VALUES(1, 1);
INSERT INTO passenger(id, platform_user_id) VALUES(2, 2);
INSERT INTO passenger(id, platform_user_id) VALUES(3, 3);
INSERT INTO passenger(id, platform_user_id) VALUES(4, 4);
INSERT INTO passenger(id, platform_user_id) VALUES(5, 5);
