CREATE TABLE CARDS(
id INT PRIMARY KEY AUTO_INCREMENT,
card_number VARCHAR(255) NOT NULL,
bill_number VARCHAR(255) NOT NULL);

INSERT INTO CARDS(CARD_NUMBER, BILL_NUMBER)
VALUES ('1111222233334444', '11111'),
    ('1234123412341234', '11111'),
    ('1122223333444455', '22222');
