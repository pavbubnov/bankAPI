package com.bubnov.repository;

import com.bubnov.controller.dto.transfer.TransferDTO;
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

class TransferRepositoryTest {

    TransferRepository transferRepository = TransferRepository.getInstance();
    String databasePath = "jdbc:h2:mem:db;DB_CLOSE_DELAY=-1";
    String databaseScript = "src/main/resources/tests/testCardDatabase.sql";
    String databaseScriptDel = "src/main/resources/tests/deleteTestCardDatabase.sql";
    H2Datasource datasource = new H2Datasource(databasePath);

    @BeforeEach
    void setUp() throws DatabaseException, SQLException, FileNotFoundException {
        Connection db = datasource.setH2Connection();
        transferRepository.setH2Datasource(datasource);
        RunScript.execute(db, new FileReader(databaseScript));
    }

    @AfterEach
    void tearDown() throws DatabaseException, SQLException, FileNotFoundException {
        Connection db = datasource.setH2Connection();
        RunScript.execute(db, new FileReader(databaseScriptDel));
    }

    @Test
    void createTransfer() throws DatabaseException, SQLException {
        TransferDTO request = new TransferDTO("11111", "22222", BigDecimal.valueOf(1.1));
        TransferDTO response = transferRepository.createTransfer(request);
        Assertions.assertEquals(request, response);
    }

    @Test
    void doTransactionalTransfer() throws DatabaseException, SQLException {
        TransferDTO request = new TransferDTO("11111", "22222", BigDecimal.valueOf(1.1));
        BigDecimal senderNewAmount = BigDecimal.valueOf(49998.95);
        BigDecimal recipientNewAmount = BigDecimal.valueOf(100001.1);
        transferRepository.doTransactionalTransfer(request, senderNewAmount, recipientNewAmount);
        BillRepository billRepository = BillRepository.getInstance();
        billRepository.setH2Datasource(datasource);
        Assertions.assertEquals(billRepository.getBillByNumber("11111").getAmount(),
                BigDecimal.valueOf(49998.95));
        Assertions.assertEquals(billRepository.getBillByNumber("22222").getAmount().longValue(),
                BigDecimal.valueOf(100001.1).longValue());
    }

}