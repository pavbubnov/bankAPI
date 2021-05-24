package com.bubnov.repository;

import com.bubnov.controller.dto.counterparty.CounterpartyDTO;
import com.bubnov.exception.DatabaseException;
import org.h2.tools.RunScript;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

class CounterpartyRepositoryTest {

    private CounterpartyRepository counterpartyRepository = CounterpartyRepository.getInstance();
    private String databasePath = "jdbc:h2:mem:db;DB_CLOSE_DELAY=-1";
    private String databaseScript = "src/main/resources/tests/testCardDatabase.sql";
    private String databaseScriptDel = "src/main/resources/tests/deleteTestCardDatabase.sql";
    private H2Datasource datasource = new H2Datasource(databasePath);

    @BeforeEach
    void setUp() throws DatabaseException, SQLException, FileNotFoundException {
        Connection db = datasource.setH2Connection();
        counterpartyRepository.setH2Datasource(datasource);
        RunScript.execute(db, new FileReader(databaseScript));
    }

    @AfterEach
    void tearDown() throws DatabaseException, SQLException, FileNotFoundException {
        Connection db = datasource.setH2Connection();
        RunScript.execute(db, new FileReader(databaseScriptDel));
    }

    @Test
    void createCounterparty() throws DatabaseException, SQLException {
        CounterpartyDTO counterpartyDTO = new CounterpartyDTO(1, 2);
        CounterpartyDTO expect = counterpartyRepository.createCounterparty(counterpartyDTO);
        Assertions.assertEquals(counterpartyDTO, expect);
    }

    @Test
    void checkThatCounterParty() throws DatabaseException, SQLException {
        Assertions.assertFalse(counterpartyRepository.checkThatCounterParty(1, 2));
        CounterpartyDTO counterpartyDTO = new CounterpartyDTO(1, 2);
        CounterpartyDTO expect = counterpartyRepository.createCounterparty(counterpartyDTO);
        Assertions.assertTrue(counterpartyRepository.checkThatCounterParty(1, 2));
    }

    @Test
    void getCounterparties() throws DatabaseException, SQLException {
        CounterpartyDTO counterpartyDTO = new CounterpartyDTO(1, 2);
        counterpartyRepository.createCounterparty(counterpartyDTO);
        CounterpartyDTO counterpartyDTO2 = new CounterpartyDTO(1, 3);
        counterpartyRepository.createCounterparty(counterpartyDTO2);
        List<Integer> counterparties = counterpartyRepository.getCounterparties(1);
        org.assertj.core.api.Assertions.assertThat(counterparties).
                containsExactly(2, 3);
    }
}