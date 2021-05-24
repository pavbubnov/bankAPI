package com.bubnov.service;

import com.bubnov.controller.dto.account.AccountDTO;
import com.bubnov.controller.dto.confirmation.ConfirmationResponseDTO;
import com.bubnov.exception.DatabaseException;
import com.bubnov.repository.AccountRepository;
import com.bubnov.repository.H2Datasource;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.h2.tools.RunScript;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

class AccountServiceTest {

    private AccountRepository accountRepository = AccountRepository.getInstance();
    private String databasePath = "jdbc:h2:mem:db;DB_CLOSE_DELAY=-1";
    private String databaseScript = "src/main/resources/tests/testCardDatabase.sql";
    private String databaseScriptDel = "src/main/resources/tests/deleteTestCardDatabase.sql";
    private H2Datasource datasource = new H2Datasource(databasePath);
    private AccountService accountService;

    @BeforeEach
    void setUp() throws DatabaseException, FileNotFoundException, SQLException {
        Connection db = datasource.setH2Connection();
        RunScript.execute(db, new FileReader(databaseScript));
        accountRepository.setH2Datasource(datasource);
        accountService = new AccountService(accountRepository);
    }

    @AfterEach
    void tearDown() throws DatabaseException, FileNotFoundException, SQLException {
        Connection db = datasource.setH2Connection();
        RunScript.execute(db, new FileReader(databaseScriptDel));
    }

    @Test
    void postAccount() throws IOException, DatabaseException, SQLException {
        AccountDTO accountDTO = new AccountDTO("Ирина");
        ObjectMapper objectMapper = new ObjectMapper();
        String info = objectMapper.writeValueAsString(accountDTO);
        ConfirmationResponseDTO responseDTO = new ConfirmationResponseDTO(1, "Account", "POST",
                info, "NOT_CONFIRMED");
        AccountDTO response = accountService.postAccount(responseDTO);
        Assertions.assertEquals(response, accountDTO);
    }
}