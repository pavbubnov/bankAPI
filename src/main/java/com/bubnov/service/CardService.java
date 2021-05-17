package com.bubnov.service;

import com.bubnov.controller.dto.bill.BillRequestDTO;
import com.bubnov.controller.dto.card.CardRequestDTO;
import com.bubnov.controller.dto.card.CardResponseDTO;
import com.bubnov.exception.DatabaseException;
import com.bubnov.exception.RequestException;
import com.bubnov.repository.BillRepository;
import com.bubnov.repository.CardRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.SQLException;
import java.util.List;

public class CardService {


    private final CardRepository cardRepository;
    private final BillRepository billRepository;
    ObjectMapper objectMapper = new ObjectMapper();

    public CardService(CardRepository cardRepository, BillRepository billRepository) {
        this.cardRepository = cardRepository;
        this.billRepository = billRepository;
    }

    public String getCardsByBillNumber(BillRequestDTO billNumber)
            throws JsonProcessingException, RequestException, DatabaseException {
        List<CardResponseDTO> cards;
        try {
            cards = cardRepository.getAllCardsByBillNumber(billNumber.getBillNumber());
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
            if (!billRepository.checkBillExists(requestDTO)){
                throw new RequestException("Счет: " + requestDTO.getBillNumber() + " не создан");
            } else if (!cardRepository.checkCardExists(requestDTO)) {
                throw new RequestException("Карта: " + requestDTO.getCardNumber()+ " уже существует");
            }
            card = cardRepository.createCard(requestDTO);
        } catch (SQLException throwables) {
            throw new RequestException("Не удалось создать карту по счету: " + requestDTO.getBillNumber());
        }
        return objectMapper.writeValueAsString(card);
    }


}
