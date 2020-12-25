create database carsharing;
\c  carsharing;

CREATE TABLE users
(
    id         uuid PRIMARY KEY NOT NULL,
    name       VARCHAR          NOT NULL,
    email      VARCHAR          NOT NULL,
    is_renting Boolean          NOT NULL,
    debt       Numeric          NOT NULL
);

CREATE TABLE cars
(
    id           uuid PRIMARY KEY NOT NULL,
    name         VARCHAR          NOT NULL,
    color        VARCHAR          NOT NULL,
    plate_number VARCHAR          NOT NULL,
    lat          NUMERIC          NOT NULL,
    lon          NUMERIC          NOT NULL,
    fuel         NUMERIC          NOT NULL,
    is_occupied  Boolean          NOT NULL,
    occupied_by  uuid,
    price        NUMERIC          NOT NULL
);

CREATE TABLE events
(
    id        uuid PRIMARY KEY NOT NUll,
    event     VARCHAR          NOT NULL,--car rented, car left
    car_id    uuid             NOT NULL,
    user_id   uuid             NOT NULL,
    lat       NUMERIC          NOT NULL,
    lon       NUMERIC          NOT NULL,
    timestamp TIMESTAMP DEFAULT now()
);

INSERT INTO users
VALUES ('00000000-0000-0000-0000-000000000001',
        'John',
        'john.smith@example.com',
        false,
        0.0),
       ('00000000-0000-0000-0000-000000000002',
        'F',
        'f@gmail.com',
        false,
        0.0);

INSERT INTO cars
VALUES ('00000000-0000-0000-0000-100000000001',
        'kia rio',
        'black',
        'в172ан78',
        50.348923484978173,
        35.348923484978173,
        0.8,
        false,
        null,
        10.0),
       ('00000000-0000-0000-0000-100000000002',
        'mazda 3',
        'red',
        'з029ку78',
        50.2355278782,
        40.8493535135,
        0.85,
        false,
        null,
        5.0);
