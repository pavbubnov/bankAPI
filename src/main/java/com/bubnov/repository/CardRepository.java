package com.bubnov.repository;

import com.bubnov.controller.dto.card.CardRequestDTO;
import com.bubnov.controller.dto.card.CardResponseDTO;
import com.bubnov.exception.DatabaseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CardRepository {

    private static CardRepository INSTANCE;
    private Connection db;

    private CardRepository() {
    }

    public void getConnection (Connection connection) {
        db = connection;
    }

    public static CardRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CardRepository();
        }
        return INSTANCE;
    }

    public void createStart(List<String> list) throws DatabaseException {
        PreparedStatement preparedStatement;
        for (int i = 0; i < list.size(); i++) {
            try {
                preparedStatement = db.prepareStatement(list.get(i));
                preparedStatement.execute();
            } catch (SQLException throwables) {
                throw new DatabaseException("Не заполнить базу данных начальными значениями");
            }
        }
    }

    public List<CardResponseDTO> getAllCardsByBillNumber(String billNumber) throws SQLException {
        PreparedStatement preparedStatement = db.prepareStatement(
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

