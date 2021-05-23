package com.bubnov.service;

import com.bubnov.controller.dto.counterparty.CounterpartyDTO;
import com.bubnov.controller.dto.transfer.TransferDTO;
import com.bubnov.exception.DatabaseException;
import com.bubnov.exception.RequestException;
import com.bubnov.repository.BillRepository;
import com.bubnov.repository.CounterpartyRepository;
import com.bubnov.repository.H2Datasource;
import com.bubnov.repository.TransferRepository;
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

import static org.junit.jupiter.api.Assertions.assertThrows;

class TransferServiceTest {

    private CounterpartyRepository counterpartyRepository = CounterpartyRepository.getInstance();
    private TransferRepository transferRepository = TransferRepository.getInstance();
    private BillRepository billRepository = BillRepository.getInstance();
    private String databasePath = "jdbc:h2:mem:db;DB_CLOSE_DELAY=-1";
    private String databaseScript = "src/main/resources/tests/testCardDatabase.sql";
    private String databaseScriptDel = "src/main/resources/tests/deleteTestCardDatabase.sql";
    private H2Datasource datasource = new H2Datasource(databasePath);
    private TransferService transferService;
    private CounterpartyDTO counterpartyDTO = new CounterpartyDTO(1, 2);

    @BeforeEach
    void setUp() throws DatabaseException, FileNotFoundException, SQLException {
        Connection db = datasource.setH2Connection();
        RunScript.execute(db, new FileReader(databaseScript));
        counterpartyRepository.setH2Datasource(datasource);
        transferRepository.setH2Datasource(datasource);
        billRepository.setH2Datasource(datasource);
        transferService = new TransferService(transferRepository, billRepository, counterpartyRepository);
        counterpartyRepository.createCounterparty(counterpartyDTO);
    }

    @AfterEach
    void tearDown() throws DatabaseException, FileNotFoundException, SQLException {
        Connection db = datasource.setH2Connection();
        RunScript.execute(db, new FileReader(databaseScriptDel));
    }

    @Test
    void postTransfer() throws RequestException, DatabaseException, SQLException {
        TransferDTO transferDTO = new TransferDTO("11111", "22222",
                BigDecimal.valueOf(1.1));
        Assertions.assertEquals(transferService.postTransfer(transferDTO), transferDTO);
    }

    @Test
    void postTransferBadAmount() {
        TransferDTO transferDTOBad = new TransferDTO("11111", "22222",
                BigDecimal.valueOf(-1.1));
        Throwable throwable = assertThrows(RequestException.class, () -> {
            transferService.postTransfer(transferDTOBad);
        });
        Assertions.assertEquals(throwable.getMessage(), "Сумма перевода должна быть положительной");
    }

    @Test
    void postTransferBadBill1() {
        TransferDTO transferDTOBad = new TransferDTO("11112", "22222",
                BigDecimal.valueOf(1.1));
        Throwable throwable = assertThrows(RequestException.class, () -> {
            transferService.postTransfer(transferDTOBad);
        });
        Assertions.assertEquals(throwable.getMessage(), "Счет 11112 не создан");
    }

    @Test
    void postTransferBadBill2() {
        TransferDTO transferDTOBad = new TransferDTO("11111", "22221",
                BigDecimal.valueOf(1.1));
        Throwable throwable = assertThrows(RequestException.class, () -> {
            transferService.postTransfer(transferDTOBad);
        });
        Assertions.assertEquals(throwable.getMessage(), "Счет 22221 не создан");
    }

    @Test
    void postTransferNotCounterParty() {
        TransferDTO transferDTOBad = new TransferDTO("11111", "33333",
                BigDecimal.valueOf(1.1));
        Throwable throwable = assertThrows(RequestException.class, () -> {
            transferService.postTransfer(transferDTOBad);
        });
        Assertions.assertEquals(throwable.getMessage(), "Получатель не является контрагентом отправителя, " +
                "создайте запрос на добавление контрагента");
    }

    @Test
    void postTransferNotEnoughMoney() {
        TransferDTO transferDTOBad = new TransferDTO("11111", "22222",
                BigDecimal.valueOf(100000000));
        Throwable throwable = assertThrows(RequestException.class, () -> {
            transferService.postTransfer(transferDTOBad);
        });
        Assertions.assertEquals(throwable.getMessage(), "У отправителя недостаточно средств для совершения " +
                "перевода");
    }
}