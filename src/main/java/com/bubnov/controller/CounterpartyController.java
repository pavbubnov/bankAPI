package com.bubnov.controller;

import com.bubnov.controller.dto.counterparty.CounterpartyDTO;
import com.bubnov.exception.DatabaseException;
import com.bubnov.exception.RequestException;
import com.bubnov.service.CounterpartyService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

public class CounterpartyController {

    private CounterpartyService counterpartyService;

    private ObjectMapper objectMapper = new ObjectMapper();

    public CounterpartyController(CounterpartyService counterpartyService) {
        this.counterpartyService = counterpartyService;
    }

    public String postCounterparty(InputStream input)
            throws IOException, RequestException, DatabaseException, SQLException {
        CounterpartyDTO requestDTO = objectMapper.readValue(input, CounterpartyDTO.class);
        return objectMapper.writeValueAsString(counterpartyService.postCounterparty(requestDTO));
    }

    public String getCounterparties(int input) throws RequestException, DatabaseException, IOException, SQLException {
        return objectMapper.writeValueAsString(counterpartyService.getCounterparties(input));
    }
}
