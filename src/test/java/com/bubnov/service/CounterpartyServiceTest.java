package com.bubnov.service;

import com.bubnov.controller.dto.counterparty.CounterpartyDTO;
import com.bubnov.entity.Account;
import com.bubnov.exception.DatabaseException;
import com.bubnov.exception.RequestException;
import com.bubnov.repository.AccountRepository;
import com.bubnov.repository.CounterpartyRepository;
import com.bubnov.repository.H2Datasource;
import org.h2.tools.RunScript;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class CounterpartyServiceTest {

    private CounterpartyRepository counterpartyRepository = CounterpartyRepository.getInstance();
    private AccountRepository accountRepository = AccountRepository.getInstance();
    private String databasePath = "jdbc:h2:mem:db;DB_CLOSE_DELAY=-1";
    private String databaseScript = "src/main/resources/tests/testCardDatabase.sql";
    private String databaseScriptDel = "src/main/resources/tests/deleteTestCardDatabase.sql";
    private H2Datasource datasource = new H2Datasource(databasePath);
    private CounterpartyService counterpartyService;

    @BeforeEach
    void setUp() throws DatabaseException, FileNotFoundException, SQLException {
        Connection db = datasource.setH2Connection();
        RunScript.execute(db, new FileReader(databaseScript));
        accountRepository.setH2Datasource(datasource);
        counterpartyRepository.setH2Datasource(datasource);
        counterpartyService = new CounterpartyService(counterpartyRepository, accountRepository);
    }

    @AfterEach
    void tearDown() throws DatabaseException, FileNotFoundException, SQLException {
        Connection db = datasource.setH2Connection();
        RunScript.execute(db, new FileReader(databaseScriptDel));
    }

    @Test
    void postCounterparty() throws DatabaseException, SQLException, RequestException {
        CounterpartyDTO counterpartyDTO = new CounterpartyDTO(1, 2);
        Assertions.assertEquals(counterpartyService.postCounterparty(counterpartyDTO), counterpartyDTO);
    }

    @Test
    void postCounterpartyBadId1() {
        CounterpartyDTO counterpartyDTOBad = new CounterpartyDTO(10, 2);
        Throwable throwable = assertThrows(RequestException.class, () -> {
            counterpartyService.postCounterparty(counterpartyDTOBad);
        });
        Assertions.assertEquals(throwable.getMessage(), "Аккаунта с id: 10 не существует");
    }

    @Test
    void postCounterpartyBadId2() {
        CounterpartyDTO counterpartyDTOBad = new CounterpartyDTO(1, 10);
        Throwable throwable = assertThrows(RequestException.class, () -> {
            counterpartyService.postCounterparty(counterpartyDTOBad);
        });
        Assertions.assertEquals(throwable.getMessage(), "Аккаунта с id: 10 не существует");
    }

    @Test
    void postCounterpartyTwice() throws DatabaseException, SQLException, RequestException {
        CounterpartyDTO counterpartyDTO = new CounterpartyDTO(1, 2);
        counterpartyService.postCounterparty(counterpartyDTO);
        Throwable throwable = assertThrows(RequestException.class, () -> {
            counterpartyService.postCounterparty(counterpartyDTO);
        });
        Assertions.assertEquals(throwable.getMessage(),
                "Аккаунт с id: 2 уже является контрагентом для аккаунта с id: 1");
    }

    @Test
    void getCounterparties() throws DatabaseException, SQLException, RequestException {
        CounterpartyDTO counterpartyDTO = new CounterpartyDTO(1, 2);
        counterpartyService.postCounterparty(counterpartyDTO);
        CounterpartyDTO counterpartyDTO2 = new CounterpartyDTO(1, 3);
        counterpartyService.postCounterparty(counterpartyDTO2);
        List<Account> counterparties = counterpartyService.getCounterparties(1);
        Account account2 = new Account(2, "Максим");
        Account account3 = new Account(3, "Глеб");
        List<Account> expect = new ArrayList<>();
        expect.add(account2);
        expect.add(account3);
        Assertions.assertEquals(counterparties, expect);
    }

    @Test
    void getCounterpartiesBadId() throws DatabaseException, SQLException, RequestException {
        Throwable throwable = assertThrows(RequestException.class, () -> {
            counterpartyService.getCounterparties(10);
        });
        Assertions.assertEquals(throwable.getMessage(),
                "Аккаунта с id: 10 не существует");
    }
}