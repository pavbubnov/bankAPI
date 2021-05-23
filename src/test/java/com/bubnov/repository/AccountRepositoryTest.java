package com.bubnov.repository;

import com.bubnov.controller.dto.account.AccountDTO;
import com.bubnov.entity.Account;
import com.bubnov.exception.DatabaseException;
import org.h2.tools.RunScript;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class AccountRepositoryTest {

    private AccountRepository accountRepository = AccountRepository.getInstance();
    private String databasePath = "jdbc:h2:mem:db;DB_CLOSE_DELAY=-1";
    private String databaseScript = "src/main/resources/tests/testCardDatabase.sql";
    private String databaseScriptDel = "src/main/resources/tests/deleteTestCardDatabase.sql";
    private H2Datasource datasource = new H2Datasource(databasePath);

    @BeforeEach
    void setUp() throws DatabaseException, SQLException, FileNotFoundException {
        Connection db = datasource.setH2Connection();
        accountRepository.setH2Datasource(datasource);
        RunScript.execute(db, new FileReader(databaseScript));
    }

    @AfterEach
    void tearDown() throws DatabaseException, SQLException, FileNotFoundException {
        Connection db = datasource.setH2Connection();
        RunScript.execute(db, new FileReader(databaseScriptDel));
    }

    @Test
    void getAccountById() throws DatabaseException, SQLException {
        Account accountById = accountRepository.getAccountById(1);
        Account expect = new Account(1, "Павел");
        Assertions.assertEquals(accountById, expect);
    }

    @Test
    void postAccount() throws DatabaseException, SQLException{
        AccountDTO accountDTO = new AccountDTO("Ирина");
        AccountDTO response = accountRepository.postAccount(accountDTO);
        Account expect = new Account(4, "Ирина");
        Assertions.assertEquals(response.getName(), expect.getNameOfPerson());
        Assertions.assertEquals(accountRepository.getAccountById(4), expect);
    }
}