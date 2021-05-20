package com.bubnov.repository;

import com.bubnov.controller.dto.transfer.TransferDTO;
import com.bubnov.exception.DatabaseException;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TransferRepository {

    private static TransferRepository INSTANCE;
    private static final String INSERT_TRANSFER = "INSERT INTO TRANSFERS(SENDER_BILL, RECIPIENT_BILL, AMOUNT) " +
            "VALUES (?, ?, ?)";
    private static final String UPDATE_BILL = "UPDATE BILLS SET AMOUNT = ? WHERE BILL_NUMBER = ?";

    private String databasePath;
    H2Datasource datasource = new H2Datasource();

    private TransferRepository() {
    }

    public void setDatabasePath(String databasePath) {
        this.databasePath = databasePath;
    }

    public static TransferRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TransferRepository();
        }
        return INSTANCE;
    }

    public TransferDTO createTransfer(TransferDTO request) throws SQLException, DatabaseException {
        try (Connection db = datasource.setH2Connection(databasePath);
             PreparedStatement preparedStatement = db.prepareStatement(INSERT_TRANSFER);
        ) {
            preparedStatement.setString(1, request.getSenderBillNumber());
            preparedStatement.setString(2, request.getRecipientBillNumber());
            preparedStatement.setBigDecimal(3, request.getAmount());
            preparedStatement.execute();
            return request;
        }
    }

    public void doTransactionalTransfer(TransferDTO request, BigDecimal senderNewAmount,
                                        BigDecimal recipientNewAmount) throws SQLException, DatabaseException {
        try (Connection db = datasource.setH2Connection(databasePath);
             PreparedStatement paymentStatement = db.prepareStatement(UPDATE_BILL);
             PreparedStatement depositStatement = db.prepareStatement(UPDATE_BILL);
        ) {
            db.setAutoCommit(false);
            paymentStatement.setBigDecimal(1, senderNewAmount);
            paymentStatement.setString(2, request.getSenderBillNumber());
            paymentStatement.executeUpdate();
            depositStatement.setBigDecimal(1, recipientNewAmount);
            depositStatement.setString(2, request.getRecipientBillNumber());
            depositStatement.executeUpdate();
            db.commit();
            db.setAutoCommit(true);
        }
    }
}
