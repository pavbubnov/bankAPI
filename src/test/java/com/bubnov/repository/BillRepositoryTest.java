package com.bubnov.repository;

import com.bubnov.controller.dto.bill.BillRequestDTO;
import com.bubnov.controller.dto.bill.BillResponseDTO;
import com.bubnov.exception.DatabaseException;
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

class BillRepositoryTest {

    private BillRepository billRepository = BillRepository.getInstance();
    private String databasePath = "jdbc:h2:mem:db;DB_CLOSE_DELAY=-1";
    private String databaseScript = "src/main/resources/tests/testCardDatabase.sql";
    private String databaseScriptDel = "src/main/resources/tests/deleteTestCardDatabase.sql";
    private H2Datasource datasource = new H2Datasource(databasePath);

    @BeforeEach
    void setUp() throws DatabaseException, SQLException, FileNotFoundException {
        Connection db = datasource.setH2Connection();
        billRepository.setH2Datasource(datasource);
        RunScript.execute(db, new FileReader(databaseScript));
    }

    @AfterEach
    void tearDown() throws DatabaseException, SQLException, FileNotFoundException {
        Connection db = datasource.setH2Connection();
        RunScript.execute(db, new FileReader(databaseScriptDel));
    }

    @Test
    void getBillByNumber() throws DatabaseException, SQLException {
        BillResponseDTO billResponseDTO = billRepository.getBillByNumber("11111");
        Assertions.assertEquals(billResponseDTO.getAmount(), BigDecimal.valueOf(50000.05));
        Assertions.assertEquals(billResponseDTO.getAccountId(), 1);

        BillResponseDTO billResponseDTOBad = billRepository.getBillByNumber("11112");
        Assertions.assertNull(billResponseDTOBad.getAmount());
    }

    @Test
    void checkBillExists() throws DatabaseException, SQLException {
        Assertions.assertTrue(billRepository.checkBillExists("11111"));
        Assertions.assertFalse(billRepository.checkBillExists("11112"));
    }

    @Test
    void changeAmount() throws DatabaseException, SQLException {
        BillResponseDTO billResponseDTO = billRepository.getBillByNumber("11111");
        Assertions.assertEquals(billResponseDTO.getAmount(), BigDecimal.valueOf(50000.05));
        billRepository.changeAmount("11111", BigDecimal.valueOf(50301.17));
        billResponseDTO = billRepository.getBillByNumber("11111");
        Assertions.assertEquals(billResponseDTO.getAmount(), BigDecimal.valueOf(50301.17));
    }

    @Test
    void createBill() throws DatabaseException, SQLException {
        BillRequestDTO billRequestDTO = new BillRequestDTO("12345", BigDecimal.valueOf(9000.98), 1);
        billRepository.createBill(billRequestDTO);
        BillResponseDTO responseExpect = new BillResponseDTO(BigDecimal.valueOf(9000.98), 1);
        BillResponseDTO billByNumber = billRepository.getBillByNumber("12345");
        Assertions.assertEquals(billByNumber, responseExpect);
    }
}