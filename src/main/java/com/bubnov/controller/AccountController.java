package com.bubnov.controller;

import com.bubnov.controller.dto.account.AccountDTO;
import com.bubnov.controller.dto.card.CardRequestDTO;
import com.bubnov.controller.dto.confirmation.ConfirmationRequestDTO;
import com.bubnov.controller.dto.confirmation.ConfirmationResponseDTO;
import com.bubnov.entity.Account;
import com.bubnov.exception.DatabaseException;
import com.bubnov.exception.RequestException;
import com.bubnov.service.AccountService;
import com.bubnov.service.ConfirmationService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

public class AccountController {


    private final ConfirmationService confirmationService;
    private ObjectMapper objectMapper = new ObjectMapper();

    public AccountController( ConfirmationService confirmationService) {
        this.confirmationService = confirmationService;
    }

    public String postCreateAccountConfirmation(InputStream input) throws IOException, DatabaseException, SQLException {
        AccountDTO accountDTO = objectMapper.readValue(input, AccountDTO.class);
        ConfirmationRequestDTO requestDTO = new ConfirmationRequestDTO("Account", "POST",
                objectMapper.writeValueAsString(accountDTO) , "NOT_CONFIRMED");
        return objectMapper.writeValueAsString(confirmationService.postConfirmation(requestDTO));
    }




}
