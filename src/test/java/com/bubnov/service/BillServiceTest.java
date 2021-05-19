package com.bubnov.service;

import com.bubnov.controller.dto.bill.AmountResponseDTO;
import com.bubnov.controller.dto.bill.BillRequestDTO;
import com.bubnov.exception.DatabaseException;
import com.bubnov.exception.RequestException;
import com.bubnov.repository.BillRepository;
import static org.junit.jupiter.api.Assertions.assertThrows;
import com.bubnov.repository.H2Datasource;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.h2.tools.RunScript;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

class BillServiceTest {

    BillRepository billRepository = BillRepository.getInstance();
    H2Datasource datasource = new H2Datasource();
    String databasePath = "jdbc:h2:mem:db;DB_CLOSE_DELAY=-1";
    String databaseScript = "src/main/resources/tests/testCardDatabase.sql";
    String databaseScriptDel = "src/main/resources/tests/deleteTestCardDatabase.sql";
    BillService billService;

    @BeforeEach
    void setUp() throws DatabaseException, FileNotFoundException, SQLException {
        Connection db = datasource.setH2Connection(databasePath);
        RunScript.execute(db, new FileReader(databaseScript));
        billRepository.setDatabasePath(databasePath);
        billService = new BillService(billRepository);
    }

    @AfterEach
    void tearDown() throws DatabaseException, FileNotFoundException, SQLException {
        Connection db = datasource.setH2Connection(databasePath);
        RunScript.execute(db, new FileReader(databaseScriptDel));
    }

    @Test
    void getAmount() throws RequestException, DatabaseException, JsonProcessingException {
        BillRequestDTO requestDTO = new BillRequestDTO("11111");
        AmountResponseDTO amount = billService.getAmount(requestDTO);
        AmountResponseDTO amountExpect = new AmountResponseDTO(BigDecimal.valueOf(50000.05));
        Assertions.assertEquals(amountExpect, amount);
        BillRequestDTO requestDTOBad = new BillRequestDTO("11112");
        Throwable throwable = assertThrows(RequestException.class, () -> {
            billService.getAmount(requestDTOBad);
        });
        Assertions.assertEquals(throwable.getMessage(), "Счет : " + requestDTOBad.getBillNumber() + " не найден");
    }
}