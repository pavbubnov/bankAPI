package com.bubnov.controller;

import com.bubnov.controller.dto.bill.BillRequestDTO;
import com.bubnov.controller.dto.card.CardRequestDTO;
import com.bubnov.exception.DatabaseException;
import com.bubnov.exception.RequestException;
import com.bubnov.service.CardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;

public class CardsController {

    private final CardService cardService;

    public CardsController(CardService cardService) {
        this.cardService = cardService;
    }
    ObjectMapper objectMapper = new ObjectMapper();

    public String postCard (InputStream input) throws IOException, RequestException {
        CardRequestDTO requestDTO = objectMapper.readValue(input, CardRequestDTO.class);
        return objectMapper.writeValueAsString(cardService.createCard(requestDTO));
    }

    public String getCards (InputStream input) throws RequestException, DatabaseException, IOException {
        BillRequestDTO billNumber = objectMapper.readValue(input, BillRequestDTO.class);
        return objectMapper.writeValueAsString(cardService.getCardsByBillNumber(billNumber));
    }






}
