CREATE TABLE IF NOT EXISTS user_access_status
(
    id INT NOT NULL
        CONSTRAINT user_access_status_pkey PRIMARY KEY,
    status VARCHAR(100) NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS platform_user_id_seq
    INCREMENT 1
    START 6;

CREATE TABLE IF NOT EXISTS platform_user
(
    id INT NOT NULL DEFAULT platform_user_id_seq.nextval
        CONSTRAINT platform_user_pkey PRIMARY KEY,
    created_date DATE NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    dob DATE NOT NULL,
    email VARCHAR(100) NOT NULL
        CONSTRAINT platform_user_email_key UNIQUE,
    password VARCHAR(65) NOT NULL,
    phone_number VARCHAR(50),
    user_access_status_id INT NOT NULL
        CONSTRAINT user_access_status_id_fkey REFERENCES user_access_status
);

CREATE TABLE IF NOT EXISTS driver
(
    id INT NOT NULL
        CONSTRAINT driver_pkey PRIMARY KEY,
    license_number VARCHAR(100),
    platform_user_id INT NOT NULL
        CONSTRAINT platform_user_id_fkey REFERENCES platform_user ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS passenger
(
    id INT NOT NULL
        CONSTRAINT passenger_pkey PRIMARY KEY,
    platform_user_id INT NOT NULL
        CONSTRAINT platform_user_id_fkey_2 REFERENCES platform_user ON DELETE CASCADE
);
