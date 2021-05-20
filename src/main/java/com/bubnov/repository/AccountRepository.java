package com.bubnov.repository;

import com.bubnov.entity.Account;
import com.bubnov.exception.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccountRepository {

    private static AccountRepository INSTANCE;
    private String databasePath;
    H2Datasource datasource = new H2Datasource();
    private static final String SELECT_ACCOUNT =
            "SELECT * FROM ACCOUNTS WHERE ID = ?";

    public static AccountRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AccountRepository();
        }
        return INSTANCE;
    }

    public void setDatabasePath(String databasePath) {
        this.databasePath = databasePath;
    }

    public Account getAccountById(int id) throws DatabaseException, SQLException {
        try (Connection db = datasource.setH2Connection(databasePath);
             PreparedStatement preparedStatement = db.prepareStatement(SELECT_ACCOUNT);
        ) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            Account test = new Account(resultSet.getInt(1), resultSet.getString(2));
            return new Account(resultSet.getInt(1), resultSet.getString(2));
        }
    }
}
