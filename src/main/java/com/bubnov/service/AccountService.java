package com.bubnov.service;

import com.bubnov.controller.dto.BillRequestDTO;
import com.bubnov.controller.dto.CardRequestDTO;
import com.bubnov.controller.dto.CardResponseDTO;
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

    public String getCardsByBillNumber(BillRequestDTO billNumber)
            throws JsonProcessingException, RequestException, DatabaseException {
        List<CardResponseDTO> cards;
        try {
            cards = repository.getAllCardsByBillNumber(billNumber.getBillNumber());
        } catch (SQLException throwables) {
            throw new DatabaseException("Ошибка базы данных");
        }
        if (cards.size() == 0) {
            throw new RequestException("Не удалось найти карты по счету: " + billNumber.getBillNumber());
        }
        return objectMapper.writeValueAsString(cards);
    }

    public String createCard(CardRequestDTO requestDTO) throws RequestException, JsonProcessingException {
        CardResponseDTO card;
        try {
            if (!repository.checkBillExists(requestDTO)){
                throw new RequestException("Счет: " + requestDTO.getBillNumber() + " не создан");
            };
            card = repository.createCard(requestDTO);
        } catch (SQLException throwables) {
            throw new RequestException("Не удалось создать карту по счету: " + requestDTO.getBillNumber());
        }
        return objectMapper.writeValueAsString(card);
    }


}
