-- ###############################  Users Setup  ###############################
DROP TABLE IF EXISTS user_business;
DROP TABLE IF EXISTS business;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS address;

CREATE TABLE address
(
    ID            INT AUTO_INCREMENT PRIMARY KEY,
    STREET_NUMBER VARCHAR(100),
    STREET_NAME   VARCHAR(200),
    POSTCODE      VARCHAR(30),
    CITY          VARCHAR(200),
    REGION        VARCHAR(200),
    COUNTRY       VARCHAR(100) NOT NULL
);

CREATE TABLE user
(
    ID              INT AUTO_INCREMENT PRIMARY KEY,
    FIRST_NAME      VARCHAR(50) NOT NULL,
    LAST_NAME       VARCHAR(50) NOT NULL,
    MIDDLE_NAME     VARCHAR(50),
    NICKNAME        VARCHAR(50),
    BIO             VARCHAR(250),
    EMAIL           VARCHAR(50) NOT NULL UNIQUE,
    DATE_OF_BIRTH   DATE        NOT NULL,
    PHONE_NUMBER    VARCHAR(18),
    IMAGE_NAME      VARCHAR(30),

    CREATED         DATETIME    NOT NULL,
    ROLE            VARCHAR(30) NOT NULL,
    PASSWORD        CHAR(60)    NOT NULL,

    HOME_ADDRESS_ID INT         NOT NULL,
    CONSTRAINT user_address_fk FOREIGN KEY (HOME_ADDRESS_ID) REFERENCES address (ID)
);

CREATE TABLE business
(
    ID                       INT AUTO_INCREMENT PRIMARY KEY,
    NAME                     VARCHAR(50) NOT NULL,
    DESCRIPTION              VARCHAR(250),
    BUSINESS_TYPE            VARCHAR(50) NOT NULL,
    CREATED                  DATETIME    NOT NULL,

    PRIMARY_ADMINISTRATOR_ID INT         NOT NULL,
    ADDRESS_ID               INT         NOT NULL,
    CONSTRAINT business_address_fk FOREIGN KEY (ADDRESS_ID) REFERENCES address (ID)
);

CREATE TABLE user_business
(
    USER_ID     INT NOT NULL,
    BUSINESS_ID INT NOT NULL,
    CONSTRAINT user_business_pk
        UNIQUE (USER_ID, BUSINESS_ID),
    CONSTRAINT user_business_user_fk
        FOREIGN KEY (USER_ID) REFERENCES user (ID),
    CONSTRAINT user_business_business_fk
        FOREIGN KEY (BUSINESS_ID) references business (ID)
);

INSERT INTO address(STREET_NUMBER,
                    STREET_NAME,
                    POSTCODE,
                    CITY,
                    REGION,
                    COUNTRY)
VALUES ('10',
        'Downing Street',
        'SW1A 2AA',
        'Westminster',
        'London',
        'United Kingdom'),
       ('99',
        'Waimari Road',
        '8041',
        'Christchurch',
        'Canterbury',
        'New Zealand'),
       ('53',
        'Ilam Road',
        '8041',
        'Christchurch',
        'Canterbury',
        'New Zealand'),
       ('123',
        'Fake Street',
        '8041',
        'Christchurch',
        'Canterbury',
        'New Zealand'),
       ('79',
        'Place Road',
        '8041',
        'Christchurch',
        'Canterbury',
        'New Zealand');

-- Inserting test user

INSERT INTO user(FIRST_NAME,
                 LAST_NAME,
                 MIDDLE_NAME,
                 NICKNAME,
                 BIO,
                 EMAIL,
                 DATE_OF_BIRTH,
                 PHONE_NUMBER,
                 HOME_ADDRESS_ID,
                 CREATED,
                 ROLE,
                 PASSWORD)
VALUES ('Fletcher',
        'Dick',
        'James',
        'fdi19',
        'Hello! I am Fletcher and I am currently studying Software Engineering.',
        'fdi19@uclive.ac.nz',
        '2000-03-10',
        '+64 22 104 1375',
        1,
        '2020-07-14T14:32:00.000000',
        'ROLE_USER',
        '$2y$12$WfyxRpooIc6QjYxvPPH7leapKY.tKFSMZdT/W1oWcTro/FutOzqQi'),
       ('Maximilian',
        'Birzer',
        '',
        'mbi47',
        'This is Maxs bio',
        'mbi47@uclive.ac.nz',
        '1986-12-22',
        '+64 21 0266 7255',
        1,
        '2020-07-14T14:32:00.000000',
        'ROLE_ADMIN',
        '$2y$12$WfyxRpooIc6QjYxvPPH7leapKY.tKFSMZdT/W1oWcTro/FutOzqQi'),
       ('Alec',
        'Fox',
        '',
        'amf133',
        'This is Alecs bio',
        'amf133@uclive.ac.nz',
        '2000-10-31',
        '',
        2,
        '2020-07-14T14:32:00.000000',
        'ROLE_ADMIN',
        '$2y$12$WfyxRpooIc6QjYxvPPH7leapKY.tKFSMZdT/W1oWcTro/FutOzqQi'),
       ('Dawson',
        'Berry',
        'Neil',
        'dnb36',
        'This is a bad bio',
        'dnb36@uclive.ac.nz',
        '2001-11-23',
        '',
        3,
        '2020-07-14T14:32:00.000000',
        'ROLE_USER',
        '$2y$12$WfyxRpooIc6QjYxvPPH7leapKY.tKFSMZdT/W1oWcTro/FutOzqQi');

INSERT INTO business(NAME,
                     PRIMARY_ADMINISTRATOR_ID,
                     DESCRIPTION,
                     ADDRESS_ID,
                     BUSINESS_TYPE,
                     CREATED)
VALUES ('TestName',
        4,
        'A Good business',
        5,
        'Retail Trade',
        '2020-07-14T14:32:00.000000'),
       ('Fake Business',
        4,
        'Shh very secret',
        4,
        'Retail Trade',
        '2020-07-14T14:32:00.000000');

INSERT INTO user_business(USER_ID,
                          BUSINESS_ID)
VALUES (4,
        1),
       (4,
        2),
       (3,
        1);