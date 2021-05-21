package com.bubnov.controller;

import com.bubnov.controller.dto.confirmation.ConfirmationResponseDTO;
import com.bubnov.controller.dto.confirmation.ConfirmationUpdateDTO;
import com.bubnov.exception.DatabaseException;
import com.bubnov.exception.RequestException;
import com.bubnov.service.AccountService;
import com.bubnov.service.ConfirmationService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

public class ConfirmationController {
    private ConfirmationService confirmationService;
    private AccountService accountService;

    private ObjectMapper objectMapper = new ObjectMapper();

    public ConfirmationController(ConfirmationService confirmationService, AccountService accountService) {
        this.confirmationService = confirmationService;
        this.accountService = accountService;
    }

    public String getAllUnconfirm() throws RequestException, DatabaseException, IOException, SQLException {
        return objectMapper.writeValueAsString(confirmationService.getUnconfirm());
    }

    public String updateStatus(InputStream input) throws DatabaseException, SQLException, IOException {
        ConfirmationUpdateDTO updateDTO = objectMapper.readValue(input, ConfirmationUpdateDTO.class);
        confirmationService.updateStatus(updateDTO);
        if (updateDTO.getStatus().equals("CONFIRMED")) {
            ConfirmationResponseDTO confirmationDTO = confirmationService.getById(updateDTO.getId());
            switch (confirmationDTO.getEntityName()) {
                case "Account":
                    accountService.postAccount(confirmationDTO);
                    break;
            }
        }
        return objectMapper.writeValueAsString(confirmationService.updateStatus(updateDTO));
    }

}
