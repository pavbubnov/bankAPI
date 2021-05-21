package com.bubnov.service;

import com.bubnov.controller.dto.account.AccountDTO;
import com.bubnov.controller.dto.confirmation.ConfirmationRequestDTO;
import com.bubnov.controller.dto.confirmation.ConfirmationResponseDTO;
import com.bubnov.exception.DatabaseException;
import com.bubnov.repository.AccountRepository;
import com.bubnov.repository.ConfirmationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.sql.SQLException;

public class AccountService {

    private final AccountRepository accountRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public AccountDTO postAccount(ConfirmationResponseDTO responseDTO)
            throws DatabaseException, SQLException, IOException {
        AccountDTO accountDTO = objectMapper.readValue(responseDTO.getInfo(), AccountDTO.class);
        responseDTO.getInfo();
        return accountRepository.postAccount(accountDTO);
    }
}
