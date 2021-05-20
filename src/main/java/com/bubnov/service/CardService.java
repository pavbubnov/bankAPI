package com.bubnov.service;

import com.bubnov.controller.dto.bill.BillRequestDTO;
import com.bubnov.controller.dto.card.CardRequestDTO;
import com.bubnov.controller.dto.card.CardResponseDTO;
import com.bubnov.exception.DatabaseException;
import com.bubnov.exception.RequestException;
import com.bubnov.repository.BillRepository;
import com.bubnov.repository.CardRepository;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.sql.SQLException;
import java.util.List;

public class CardService {

    private final CardRepository cardRepository;
    private final BillRepository billRepository;

    public CardService(CardRepository cardRepository, BillRepository billRepository) {
        this.cardRepository = cardRepository;
        this.billRepository = billRepository;
    }

    public List<CardResponseDTO> getCardsByBillNumber(String billNumber)
            throws  RequestException, DatabaseException {
        List<CardResponseDTO> cards;
        try {
            cards = cardRepository.getAllCardsByBillNumber(billNumber);
        } catch (SQLException throwables) {
            throw new DatabaseException("Ошибка базы данных");
        }
        if (cards.size() == 0) {
            throw new RequestException("Не удалось найти карты по счету: " + billNumber);
        }
        return cards;
    }

    public CardResponseDTO createCard(CardRequestDTO requestDTO) throws RequestException {
        CardResponseDTO card;
        try {
            if (!billRepository.checkBillExists(requestDTO.getBillNumber())) {
                throw new RequestException("Счет " + requestDTO.getBillNumber() + " не создан");
            } else if (!cardRepository.checkCardExists(requestDTO.getCardNumber())) {
                throw new RequestException("Карта " + requestDTO.getCardNumber() + " уже существует");
            }
            card = cardRepository.createCard(requestDTO);
        } catch (Exception e) {
            throw new RequestException(e.getMessage());
        }
        return card;
    }
}
