package com.bubnov.service;

import com.bubnov.entity.Card;
import com.bubnov.exception.DatabaseException;
import com.bubnov.exception.RequestException;
import com.bubnov.repository.Repository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.SQLException;
import java.util.List;

public class AccountService {

    private final Repository repository;
    ObjectMapper objectMapper = new ObjectMapper();

    public AccountService(Repository repository) {
        this.repository = repository;
    }

    public String getCardsByBillId(int billId) throws JsonProcessingException, RequestException, DatabaseException {
        List<Card> cards;
        try {
            cards = repository.getAllCardsByBillId(billId);
        } catch (SQLException throwables) {
            throw new DatabaseException("Ошибка базы данных");
        }
        if (cards.size() == 0) {
            throw new RequestException("Не удалось найти карты по счету: " + billId);
        }
        return objectMapper.writeValueAsString(cards);
    }


}
