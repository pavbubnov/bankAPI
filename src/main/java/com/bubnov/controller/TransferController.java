package com.bubnov.controller;

import com.bubnov.controller.dto.transfer.TransferDTO;
import com.bubnov.exception.DatabaseException;
import com.bubnov.exception.RequestException;
import com.bubnov.service.TransferService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

public class TransferController {

    private TransferService transferService;

    ObjectMapper objectMapper = new ObjectMapper();

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    public String postTransfer(InputStream input)
            throws IOException, RequestException, DatabaseException, SQLException {
        TransferDTO requestDTO = objectMapper.readValue(input, TransferDTO.class);
        return objectMapper.writeValueAsString(transferService.postTransfer(requestDTO));
    }

}
