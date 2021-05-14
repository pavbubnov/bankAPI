package com.bubnov.repository;

public class Query {

    public static final String CREATE_TABLE =
            "CREATE TABLE ACCOUNT(\n" +
                    " id INT PRIMARY KEY AUTO_INCREMENT,\n" +
                    " name VARCHAR(255),\n" +
                    "                   );";

    public static final String POST_START_CITIES =
                    "INSERT INTO CITIES(ID, NAME)\n" +
                    "VALUES ('Агрыз', 'Татарстан');";

}
