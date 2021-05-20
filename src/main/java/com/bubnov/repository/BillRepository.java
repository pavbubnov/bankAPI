package com.bubnov.repository;

import com.bubnov.controller.dto.bill.BillResponseDTO;
import com.bubnov.exception.DatabaseException;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BillRepository {

    private static BillRepository INSTANCE;
    private static final String SELECT_BILLS_BY_BILL_NUMBER = "SELECT * FROM BILLS WHERE BILL_NUMBER = (?)";
    private static final String UPDATE_BILLS_BY_BILL_NUMBER = "UPDATE BILLS SET AMOUNT = ? WHERE BILL_NUMBER = ?";
    private static final String ARE_BILLS_EXISTS = "SELECT COUNT(1) FROM BILLS WHERE BILL_NUMBER = ?";
    private static final String COLUMN_AMOUNT = "AMOUNT";
    private static final String COLUMN_ACCOUNT_ID = "ACCOUNT_ID";
    private String databasePath;
    H2Datasource datasource = new H2Datasource();

    private BillRepository() {
    }

    private BillRepository(String databasePath) {
        this.databasePath = databasePath;
    }

    public void setDatabasePath(String databasePath) {
        this.databasePath = databasePath;
    }

    public static BillRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BillRepository();
        }
        return INSTANCE;
    }

    public BillResponseDTO getBillByNumber(String billNumber) throws SQLException, DatabaseException {
        try (Connection db = datasource.setH2Connection(databasePath);
             PreparedStatement preparedStatement = db.prepareStatement(SELECT_BILLS_BY_BILL_NUMBER);
        ) {
            preparedStatement.setString(1, billNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            BillResponseDTO bill = new BillResponseDTO();
            while (resultSet.next()) {
                bill.setAmount(resultSet.getBigDecimal(COLUMN_AMOUNT));
                bill.setAccountId(resultSet.getInt(COLUMN_ACCOUNT_ID));
            }
            return bill;
        }
    }

    public boolean checkBillExists(String cardNumber) throws SQLException, DatabaseException {
        try (Connection db = datasource.setH2Connection(databasePath);
             PreparedStatement preparedStatement = db.prepareStatement(ARE_BILLS_EXISTS);
        ) {
            preparedStatement.setString(1, cardNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                if (resultSet.getInt(1) == 1) {
                    return true;
                }
            }
            return false;
        }
    }

    public void changeAmount(String billNumber, BigDecimal amount) throws SQLException, DatabaseException {
        try (Connection db = datasource.setH2Connection(databasePath);
             PreparedStatement preparedStatement = db.prepareStatement(UPDATE_BILLS_BY_BILL_NUMBER);
        ) {
            preparedStatement.setBigDecimal(1, amount);
            preparedStatement.setString(2, billNumber);
            preparedStatement.execute();
        }
    }
}
