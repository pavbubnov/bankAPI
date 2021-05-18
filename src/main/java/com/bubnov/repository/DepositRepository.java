package com.bubnov.repository;

import com.bubnov.controller.dto.deposit.DepositRequestDTO;
import com.bubnov.entity.Deposit;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DepositRepository {

    private static DepositRepository INSTANCE;
    private Connection db;
    private static final String INSERT_DEPOSIT = "INSERT INTO DEPOSITS(BILL_NUMBER, AMOUNT) VALUES (?, ?)";
    private static final String SELECT_DEPOSITS_BY_BILL = "INSERT INTO DEPOSITS(BILL_NUMBER, AMOUNT) VALUES (?, ?)";
    private static final String COLUMN_ID = "ID";
    private static final String COLUMN_BILL_NUMBER = "BILL_NUMBER";
    private static final String COLUMN_AMOUNT = "AMOUNT";

    private DepositRepository() {
    }

    public void getConnection (Connection connection) {
        db = connection;
    }

    public static DepositRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DepositRepository();
        }
        return INSTANCE;
    }

    public DepositRequestDTO createDeposit(DepositRequestDTO request) throws SQLException {
        PreparedStatement preparedStatement = db.prepareStatement(INSERT_DEPOSIT);
        preparedStatement.setString(1, request.getBillNumber());
        preparedStatement.setBigDecimal(2, request.getAmount());
        preparedStatement.execute();
        return request;
    }

    public Deposit getDeposit(String billNumber) throws SQLException {
        PreparedStatement preparedStatement = db.prepareStatement(SELECT_DEPOSITS_BY_BILL);
        preparedStatement.setString(1, billNumber);
        ResultSet resultSet = preparedStatement.executeQuery();
        Deposit deposit = new Deposit();
        while (resultSet.next()) {
                    deposit.setId(resultSet.getInt(COLUMN_ID));
                    deposit.setBillNumber(resultSet.getString(COLUMN_BILL_NUMBER));
                    deposit.setAmount(resultSet.getBigDecimal(COLUMN_AMOUNT));
        }
        return deposit;
    }
}
