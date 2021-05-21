
package com.bubnov.repository;

import com.bubnov.controller.dto.deposit.DepositRequestDTO;
import com.bubnov.exception.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DepositRepository {

    private static DepositRepository INSTANCE;
    private static final String INSERT_DEPOSIT = "INSERT INTO DEPOSITS(BILL_NUMBER, AMOUNT) VALUES (?, ?)";

    private H2Datasource h2Datasource;

    private DepositRepository() {
    }

    public void setH2Datasource(H2Datasource h2Datasource) {
        this.h2Datasource = h2Datasource;
    }

    public static DepositRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DepositRepository();
        }
        return INSTANCE;
    }

    public DepositRequestDTO createDeposit(DepositRequestDTO request) throws SQLException, DatabaseException {
        try (Connection db = h2Datasource.setH2Connection();
             PreparedStatement preparedStatement = db.prepareStatement(INSERT_DEPOSIT);
        ) {
            preparedStatement.setString(1, request.getBillNumber());
            preparedStatement.setBigDecimal(2, request.getAmount());
            preparedStatement.execute();
            return request;
        }
    }

}
