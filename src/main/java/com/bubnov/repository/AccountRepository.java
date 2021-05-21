package com.bubnov.repository;

import com.bubnov.controller.dto.account.AccountDTO;
import com.bubnov.entity.Account;
import com.bubnov.exception.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountRepository {

    private static AccountRepository INSTANCE;
    private H2Datasource h2Datasource;
    private static final String SELECT_ACCOUNT =
            "SELECT * FROM ACCOUNTS WHERE ID = ?";
    private static final String INSERT_ACCOUNT =
            "INSERT INTO ACCOUNTS (NAME) VALUES (?)";
    ;

    public static AccountRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AccountRepository();
        }
        return INSTANCE;
    }

    public void setH2Datasource(H2Datasource h2Datasource) {
        this.h2Datasource = h2Datasource;
    }

    public Account getAccountById(int id) throws DatabaseException, SQLException {
        try (Connection db = h2Datasource.setH2Connection();
             PreparedStatement preparedStatement = db.prepareStatement(SELECT_ACCOUNT);
        ) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            Account test = new Account(resultSet.getInt(1), resultSet.getString(2));
            return new Account(resultSet.getInt(1), resultSet.getString(2));
        }
    }

    public AccountDTO postAccount(AccountDTO account) throws DatabaseException, SQLException {
        try (Connection db = h2Datasource.setH2Connection();
             PreparedStatement preparedStatement = db.prepareStatement(INSERT_ACCOUNT);
        ) {
            preparedStatement.setString(1, account.getName());
            preparedStatement.execute();
            return account;
        }
    }
}
