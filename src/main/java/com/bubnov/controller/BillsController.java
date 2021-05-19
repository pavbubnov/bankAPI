package com.bubnov.controller;

import com.bubnov.controller.dto.bill.BillRequestDTO;
import com.bubnov.exception.DatabaseException;
import com.bubnov.exception.RequestException;
import com.bubnov.service.BillService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class BillsController {

    private final BillService billService;
    ObjectMapper objectMapper = new ObjectMapper();

    public BillsController(BillService billService) {
        this.billService = billService;
    }

    public String getAmount(HttpExchange exchange) throws RequestException, DatabaseException, IOException {
        BillRequestDTO billNumber = objectMapper.readValue(exchange.getRequestBody(), BillRequestDTO.class);
        return objectMapper.writeValueAsString(billService.getAmount(billNumber));
    }


}
