package com.bubnov.controller;

import com.bubnov.controller.dto.bill.BillRequestDTO;
import com.bubnov.exception.DatabaseException;
import com.bubnov.exception.RequestException;
import com.bubnov.service.BillService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public class BillsController {

    private final BillService billService;
    ObjectMapper objectMapper = new ObjectMapper();

    public BillsController(BillService billService) {
        this.billService = billService;
    }

    public String getAmount(String input) throws RequestException, DatabaseException, IOException {
        return objectMapper.writeValueAsString(billService.getAmount(input));
    }


}
