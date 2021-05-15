package com.bubnov.repository;

import java.util.ArrayList;
import java.util.List;

public class Query {

    public static final String CREATE_ACCOUNT_TABLE =
            "CREATE TABLE ACCOUNTS(\n" +
                    " id INT PRIMARY KEY AUTO_INCREMENT,\n" +
                    " name VARCHAR(255)\n" +
                    "                   );";

    public static final String CREATE_BILL_TABLE =
            "CREATE TABLE BILLS(\n" +
                    " id INT PRIMARY KEY AUTO_INCREMENT,\n" +
                    " account_id INT\n" +
                    "                   );";

    public static final String CREATE_CARD_TABLE =
            "CREATE TABLE CARDS(\n" +
                    " id INT PRIMARY KEY AUTO_INCREMENT,\n" +
                    " number VARCHAR(255),\n" +
                    " amount NUMERIC(19,2),\n" +
                    " bill_id INT\n" +
                    "                   );";

    public static final String POST_START_ACCOUNTS =
            "INSERT INTO ACCOUNTS(NAME)\n" +
                    "VALUES ('Павел');\n" +
                    "\n" +
                    "INSERT INTO ACCOUNTS(NAME)\n" +
                    "VALUES ('Максим');";

    public static final String POST_START_BILLS =
            "INSERT INTO BILLS(ACCOUNT_ID)\n" +
                    "VALUES (1);\n" +
                    "\n" +
                    "INSERT INTO BILLS(ACCOUNT_ID)\n" +
                    "VALUES (2);";

    public static final String POST_START_CARDS =
            "INSERT INTO CARDS(NUMBER, AMOUNT, BILL_ID)\n" +
                    "VALUES ('1111222233334444', 50000, 1);\n" +
                    "\n" +
                    "INSERT INTO CARDS(NUMBER, AMOUNT, BILL_ID)\n" +
                    "VALUES ('1122223333444455', 75000, 2);";

    public static List<String> startQueryList() {
        List<String> startQueryList = new ArrayList<>();
        startQueryList.add(CREATE_ACCOUNT_TABLE);
        startQueryList.add(CREATE_BILL_TABLE);
        startQueryList.add(CREATE_CARD_TABLE);
        startQueryList.add(POST_START_ACCOUNTS);
        startQueryList.add(POST_START_BILLS);
        startQueryList.add(POST_START_CARDS);
        return startQueryList;
    }

}
