package com.bubnov.controller;

import com.bubnov.controller.dto.deposit.DepositRequestDTO;
import com.bubnov.exception.DatabaseException;
import com.bubnov.exception.RequestException;
import com.bubnov.service.DepositService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class DepositController {

    private DepositService depositService;

    ObjectMapper objectMapper = new ObjectMapper();

    public DepositController(DepositService depositService) {
        this.depositService = depositService;
    }

    public String postDeposit (HttpExchange exchange) throws IOException, RequestException, DatabaseException {
        DepositRequestDTO requestDTO = objectMapper.readValue(exchange.getRequestBody(), DepositRequestDTO.class);
        return depositService.postDeposit(requestDTO);
    }

}
