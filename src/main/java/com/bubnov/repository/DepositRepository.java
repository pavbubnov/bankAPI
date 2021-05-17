package com.bubnov.repository;

import com.bubnov.controller.dto.deposit.DepositRequestDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
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


}
