package com.bubnov.controller;

import com.bubnov.controller.dto.deposit.DepositRequestDTO;
import com.bubnov.exception.DatabaseException;
import com.bubnov.exception.RequestException;
import com.bubnov.service.DepositService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public class DepositController {

    private DepositService depositService;

    private ObjectMapper objectMapper = new ObjectMapper();

    public DepositController(DepositService depositService) {
        this.depositService = depositService;
    }

    public String postDeposit(InputStream input) throws IOException, RequestException, DatabaseException {
        DepositRequestDTO requestDTO = objectMapper.readValue(input, DepositRequestDTO.class);
        return objectMapper.writeValueAsString(depositService.postDeposit(requestDTO));
    }

}
