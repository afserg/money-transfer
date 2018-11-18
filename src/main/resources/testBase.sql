CREATE TABLE accounts
(
    id int AUTO_INCREMENT PRIMARY KEY NOT NULL,
    number varchar2(25) NOT NULL,
    balance long NOT NULL
);
CREATE UNIQUE INDEX accounts_id_uindex ON accounts (id);

INSERT INTO accounts (number, balance) VALUES ('123', 1000);
INSERT INTO accounts (number, balance) VALUES ('234', 1000);

CREATE TABLE transfers
(
    id int AUTO_INCREMENT PRIMARY KEY NOT NULL,
    accFrom varchar2(25) NOT NULL,
    accTo varchar2(25) NOT NULL,
    amount long NOT NULL
);
CREATE UNIQUE INDEX transfers_id_uindex ON transfers (id);