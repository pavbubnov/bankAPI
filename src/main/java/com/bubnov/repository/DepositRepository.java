package com.bubnov.repository;

import com.bubnov.controller.dto.deposit.DepositRequestDTO;
import com.bubnov.controller.dto.deposit.DepositResponseDTO;
import com.bubnov.entity.Deposit;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DepositRepository {

    private static DepositRepository INSTANCE;
    private Connection db;

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
        PreparedStatement preparedStatement = db.prepareStatement(
                "INSERT INTO DEPOSITS(BILL_NUMBER, AMOUNT) VALUES (?, ?);");
        preparedStatement.setString(1, request.getBillNumber());
        preparedStatement.setBigDecimal(2, request.getAmount());
        preparedStatement.execute();
        return request;
    }

    public Deposit getDeposit(String billNumber) throws SQLException {
        PreparedStatement preparedStatement = db.prepareStatement(
                "SELECT * FROM DEPOSITS WHERE BILL_NUMBER = ?;");
        preparedStatement.setString(1, billNumber);
        ResultSet resultSet = preparedStatement.executeQuery();
        Deposit deposit = new Deposit();
        while (resultSet.next()) {
                    deposit.setId(resultSet.getInt("ID"));
                    deposit.setBillNumber(resultSet.getString("BILL_NUMBER"));
                    deposit.setAmount(resultSet.getBigDecimal("AMOUNT"));
        }
        return deposit;
    }
}
