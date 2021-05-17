package com.bubnov.repository;

import com.bubnov.controller.dto.bill.BillResponseDTO;
import com.bubnov.controller.dto.card.CardRequestDTO;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BillRepository {

    private static BillRepository INSTANCE;
    private Connection db;

    private BillRepository() {
    }

    public void getConnection (Connection connection) {
        db = connection;
    }

    public static BillRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BillRepository();
        }
        return INSTANCE;
    }

    public BillResponseDTO getBillByNumber(String billNumber) throws SQLException {
        PreparedStatement preparedStatement = db.prepareStatement(
                "SELECT * FROM BILLS WHERE BILL_NUMBER = (?)");
        preparedStatement.setString(1, billNumber);
        ResultSet resultSet = preparedStatement.executeQuery();
        BillResponseDTO bill = new BillResponseDTO();
        while (resultSet.next()) {
            bill.setAmount(resultSet.getBigDecimal("AMOUNT"));
            bill.setAccountId(resultSet.getInt("ACCOUNT_ID"));
        }
        return bill;
    }

    public boolean checkBillExists(CardRequestDTO card) throws SQLException {
        PreparedStatement preparedStatement =
                db.prepareStatement("SELECT COUNT(1) FROM BILLS WHERE BILL_NUMBER = ?");
        preparedStatement.setString(1, card.getBillNumber());
        ResultSet resultSet = preparedStatement.executeQuery();
        while(resultSet.next()) {
            if (resultSet.getInt(1) == 1){
                return true;
            }
        }
        return false;
    }

    public void changeAmount(String billNumber, BigDecimal amount) throws SQLException {
        PreparedStatement preparedStatement =
                db.prepareStatement("UPDATE BILLS SET AMOUNT = ? WHERE BILL_NUMBER = ?");
        preparedStatement.setBigDecimal(1, amount);
        preparedStatement.setString(2, billNumber);
        preparedStatement.execute();
    }

}
