package com.bubnov.repository;

import com.bubnov.exception.DatabaseException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class H2ConnectionUtils {

    public Connection getH2Connection() throws DatabaseException {
        try {
            return DriverManager.getConnection("jdbc:h2:mem:");
        } catch (SQLException throwables) {
            throw new DatabaseException("Не удалось установить соединение с базой данных");
        }
    }

}
