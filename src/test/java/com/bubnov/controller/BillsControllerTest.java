package com.bubnov.controller;

import com.bubnov.controller.dto.bill.AmountResponseDTO;
import com.bubnov.exception.DatabaseException;
import com.bubnov.exception.RequestException;
import com.bubnov.repository.BillRepository;
import com.bubnov.repository.H2Datasource;
import com.bubnov.service.BillService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.h2.tools.RunScript;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class BillsControllerTest {

    BillRepository billRepository = BillRepository.getInstance();
    H2Datasource datasource = new H2Datasource();
    String databasePath = "jdbc:h2:mem:db;DB_CLOSE_DELAY=-1";
    String databaseScript = "src/main/resources/tests/testCardDatabase.sql";
    String databaseScriptDel = "src/main/resources/tests/deleteTestCardDatabase.sql";
    BillService billService;
    BillsController billsController;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws DatabaseException, FileNotFoundException, SQLException {
        Connection db = datasource.setH2Connection(databasePath);
        RunScript.execute(db, new FileReader(databaseScript));
        billRepository.setDatabasePath(databasePath);
        billService = new BillService(billRepository);
        billsController = new BillsController(billService);
    }

    @AfterEach
    void tearDown() throws DatabaseException, FileNotFoundException, SQLException {
        Connection db = datasource.setH2Connection(databasePath);
        RunScript.execute(db, new FileReader(databaseScriptDel));
    }

    @Test
    void getAmount() throws IOException, DatabaseException, RequestException {
        String request = "11111";
        String out = billsController.getAmount(request);
        AmountResponseDTO amountExpect = new AmountResponseDTO(BigDecimal.valueOf(50000.05));
        String jsonExpect = objectMapper.writeValueAsString(amountExpect);
        Assertions.assertEquals(out, jsonExpect);
    }

    @Test
    void getAmountThrow() {
        String requestBad = "11112";
        Throwable throwable = assertThrows(RequestException.class, () -> {
            billsController.getAmount(requestBad);
        });
        Assertions.assertEquals(throwable.getMessage(), "Счет : " + requestBad + " не найден");
    }
}