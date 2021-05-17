package com.bubnov.repository;

import com.bubnov.controller.dto.CardRequestDTO;
import com.bubnov.controller.dto.CardResponseDTO;
import com.bubnov.exception.DatabaseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CardRepository {

    private static CardRepository INSTANCE;
    private Connection db;

    private CardRepository() {

    }

    public static CardRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CardRepository();
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

    public List<CardResponseDTO> getAllCardsByBillNumber(String billNumber) throws SQLException {
        PreparedStatement preparedStatement = this.db.prepareStatement(
                "SELECT * FROM CARDS WHERE BILL_NUMBER = (?)");
        preparedStatement.setString(1, billNumber);
        ResultSet resultSet = preparedStatement.executeQuery();
        List<CardResponseDTO> cards = new ArrayList<>();
        while (resultSet.next()) {
            CardResponseDTO card = new CardResponseDTO(resultSet.getString("CARD_NUMBER"));
            cards.add(card);
        }
        return cards;
    }

    public CardResponseDTO createCard(CardRequestDTO card) throws SQLException {
        PreparedStatement preparedStatement = db.prepareStatement(
                "INSERT INTO CARDS(CARD_NUMBER, BILL_NUMBER) VALUES (?, ?);");
        preparedStatement.setString(1, card.getCardNumber());
        preparedStatement.setString(2, card.getBillNumber());
        preparedStatement.execute();
        return new CardResponseDTO(card);
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

    public boolean checkCardExists(CardRequestDTO card) throws SQLException {
        PreparedStatement preparedStatement =
                db.prepareStatement("SELECT COUNT(1) FROM CARDS WHERE CARD_NUMBER = ?");
        preparedStatement.setString(1, card.getCardNumber());
        ResultSet resultSet = preparedStatement.executeQuery();
        while(resultSet.next()) {
            if (resultSet.getInt(1) == 0){
                return true;
            }
        }
        return false;
    }

}

