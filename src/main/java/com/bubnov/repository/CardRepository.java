package com.bubnov.repository;

import com.bubnov.controller.dto.card.CardRequestDTO;
import com.bubnov.controller.dto.card.CardResponseDTO;
import com.bubnov.exception.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CardRepository {

    private static final String SELECT_CARDS_BY_BILL_NUMBER = "SELECT * FROM CARDS WHERE BILL_NUMBER = (?)";
    private static final String IS_CARD_EXIST = "SELECT COUNT(1) FROM CARDS WHERE CARD_NUMBER = ?";
    private static final String INSERT_CARD = "INSERT INTO CARDS(CARD_NUMBER, BILL_NUMBER) VALUES (?, ?);";
    private static final String COLUMN_CARD_NUMBER = "CARD_NUMBER";
    private H2Datasource h2Datasource;

    private static CardRepository INSTANCE;

    private CardRepository() {
    }

    public void setH2Datasource(H2Datasource h2Datasource) {
        this.h2Datasource = h2Datasource;
    }

    public static CardRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CardRepository();
        }
        return INSTANCE;
    }

    public List<CardResponseDTO> getAllCardsByBillNumber(String billNumber) throws SQLException, DatabaseException {
        List<CardResponseDTO> cards = new ArrayList<>();
        try (Connection db = h2Datasource.setH2Connection();
             PreparedStatement preparedStatement = db.prepareStatement(SELECT_CARDS_BY_BILL_NUMBER);
        ) {
            preparedStatement.setString(1, billNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                CardResponseDTO card = new CardResponseDTO(resultSet.getString(COLUMN_CARD_NUMBER));
                cards.add(card);
            }
            return cards;
        }
    }

    public CardResponseDTO createCard(CardRequestDTO card) throws SQLException, DatabaseException {
        try (Connection db = h2Datasource.setH2Connection();
             PreparedStatement preparedStatement = db.prepareStatement(INSERT_CARD);
        ) {
            preparedStatement.setString(1, card.getCardNumber());
            preparedStatement.setString(2, card.getBillNumber());
            preparedStatement.execute();
            return new CardResponseDTO(card);
        }
    }

    public boolean checkCardExists(String cardNumber) throws SQLException, DatabaseException {
        try (Connection db = h2Datasource.setH2Connection();
             PreparedStatement preparedStatement = db.prepareStatement(IS_CARD_EXIST);
        ) {
            preparedStatement.setString(1, cardNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                if (resultSet.getInt(1) == 0) {
                    return true;
                }
            }
            return false;
        }
    }
}

