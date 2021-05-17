package com.bubnov.repository;

import com.bubnov.entity.Account;
import com.bubnov.entity.Card;
import com.bubnov.exception.DatabaseException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Repository {

    private static Repository INSTANCE;
    private Connection db;

    private Repository() {

    }

    public static Repository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Repository();
        }
        return INSTANCE;
    }

    public void getH2Connection() throws DatabaseException {
        try {
            this.db = DriverManager.getConnection("jdbc:h2:mem:");
        } catch (SQLException throwables) {
            throw new DatabaseException("Не удалось установить соединение с базой данных");
        }
    }

    public void closeConnection() {
        try {
            this.db.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void createStart(List<String> list) throws DatabaseException {
        PreparedStatement preparedStatement;
        for (int i = 0; i < list.size(); i++) {
            try {
                preparedStatement = this.db.prepareStatement(list.get(i));
                preparedStatement.execute();
            } catch (SQLException throwables) {
                throw new DatabaseException("Не заполнить базу данных начальными значениями");
            }
        }
    }

//    public void postCity(Account account) {
//        try {
//            PreparedStatement preparedStatement = this.db.prepareStatement("INSERT INTO ACCOUNT(NAME) VALUES (?)");
//            preparedStatement.setString(1, account.getNameOfPerson());
//            preparedStatement.execute();
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//    }

    public List<Card> getAllCardsByBillNumber(long billNumber) throws SQLException {
        PreparedStatement preparedStatement = this.db.prepareStatement(
                "SELECT * FROM CARDS WHERE BILL_NUMBER = (?)");
        preparedStatement.setLong(1, billNumber);
        ResultSet resultSet = preparedStatement.executeQuery();
        List<Card> cards = new ArrayList<>();
        while (resultSet.next()) {
            Card card = new Card(resultSet.getInt("ID"), resultSet.getLong("CARD_NUMBER"),
                    resultSet.getBigDecimal("AMOUNT"), resultSet.getLong("BILL_NUMBER"));
            cards.add(card);
        }
        return cards;
    }

}

