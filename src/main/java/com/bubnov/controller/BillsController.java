package com.bubnov.controller;

import com.bubnov.controller.dto.bill.BillRequestDTO;
import com.bubnov.controller.dto.confirmation.ConfirmationRequestDTO;
import com.bubnov.exception.DatabaseException;
import com.bubnov.exception.RequestException;
import com.bubnov.service.BillService;
import com.bubnov.service.ConfirmationService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

public class BillsController {

    private final BillService billService;
    private final ConfirmationService confirmationService;
    private ObjectMapper objectMapper = new ObjectMapper();

    public BillsController(BillService billService, ConfirmationService confirmationService) {
        this.billService = billService;
        this.confirmationService = confirmationService;
    }

    public String getAmount(String input) throws RequestException, DatabaseException, IOException {
        return objectMapper.writeValueAsString(billService.getAmount(input));
    }

    public String postCreateBillConfirmation(InputStream input)
            throws IOException, DatabaseException, SQLException, RequestException {
        BillRequestDTO billDTO = objectMapper.readValue(input, BillRequestDTO.class);
        ConfirmationRequestDTO requestDTO = new ConfirmationRequestDTO("Bill", "POST",
                objectMapper.writeValueAsString(billDTO) , "NOT_CONFIRMED");
        return objectMapper.writeValueAsString(confirmationService.postConfirmation(requestDTO));
    }
}
