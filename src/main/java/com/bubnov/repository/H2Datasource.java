package com.bubnov.repository;

import com.bubnov.exception.DatabaseException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class H2Datasource {

    public Connection setH2Connection(String databasePath) throws DatabaseException {
        try {
            return DriverManager.getConnection(databasePath);
        } catch (SQLException throwables) {
            throw new DatabaseException("Не удалось установить соединение с базой данных");
        }
    }

}
