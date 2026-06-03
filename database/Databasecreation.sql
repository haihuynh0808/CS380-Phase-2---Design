CREATE DATABASE IF NOT EXISTS appdb;
USE appdb;

CREATE TABLE appdb.users (
    userID INT NOT NULL AUTO_INCREMENT,
    username VARCHAR(200) NOT NULL,
    email VARCHAR(200) NOT NULL,
    password VARCHAR(150) NOT NULL,
    role VARCHAR(20) DEFAULT 'user',
    PRIMARY KEY (userID),
    CONSTRAINT check_role CHECK (role IN ('admin', 'user'))
);

CREATE TABLE appdb.transactions (
    id INT NOT NULL AUTO_INCREMENT,
    type VARCHAR(200) DEFAULT 'Income',
    amount DECIMAL(10,2),
    category VARCHAR(200) DEFAULT 'Salary',
    description VARCHAR(500) NULL,
    userID INT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_user FOREIGN KEY (userID) 
        REFERENCES appdb.users(userID)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);