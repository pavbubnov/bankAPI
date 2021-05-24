package com.bubnov.service;

import com.bubnov.controller.dto.deposit.DepositRequestDTO;
import com.bubnov.controller.dto.deposit.DepositResponseDTO;
import com.bubnov.exception.DatabaseException;
import com.bubnov.exception.RequestException;
import com.bubnov.repository.BillRepository;
import com.bubnov.repository.DepositRepository;
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

import static org.junit.jupiter.api.Assertions.assertThrows;

class DepositServiceTest {

    private BillRepository billRepository = BillRepository.getInstance();
    private DepositRepository depositRepository = DepositRepository.getInstance();
    private String databasePath = "jdbc:h2:mem:db;DB_CLOSE_DELAY=-1";
    private String databaseScript = "src/main/resources/tests/testCardDatabase.sql";
    private String databaseScriptDel = "src/main/resources/tests/deleteTestCardDatabase.sql";
    private H2Datasource datasource = new H2Datasource(databasePath);
    private DepositService depositService;

    @BeforeEach
    void setUp() throws DatabaseException, SQLException, FileNotFoundException {
        Connection db = datasource.setH2Connection();
        depositRepository.setH2Datasource(datasource);
        billRepository.setH2Datasource(datasource);
        depositService = new DepositService(depositRepository, billRepository);
        RunScript.execute(db, new FileReader(databaseScript));
    }

    @AfterEach
    void tearDown() throws DatabaseException, SQLException, FileNotFoundException {
        Connection db = datasource.setH2Connection();
        RunScript.execute(db, new FileReader(databaseScriptDel));
    }

    @Test
    void postDeposit() throws RequestException, DatabaseException, JsonProcessingException {
        DepositRequestDTO requestDTO = new DepositRequestDTO("11111", BigDecimal.valueOf(5051.51));
        DepositResponseDTO response = depositService.postDeposit(requestDTO);
        DepositResponseDTO responseExpect = new DepositResponseDTO("11111", BigDecimal.valueOf(5051.51),
                BigDecimal.valueOf(55051.56));
        Assertions.assertEquals(response, responseExpect);
    }

    @Test
    void postDepositBadNum() {
        DepositRequestDTO requestDTOBadNum = new DepositRequestDTO("11112", BigDecimal.valueOf(5051.51));
        Throwable throwableNum = assertThrows(RequestException.class, () -> {
            depositService.postDeposit(requestDTOBadNum);
        });
        Assertions.assertEquals(throwableNum.getMessage(),
                "Не удалось найти счет: " + requestDTOBadNum.getBillNumber());
    }

    @Test
    void postDepositBadAmount() {
        DepositRequestDTO requestDTOBadAmount1 = new DepositRequestDTO("11111", BigDecimal.ZERO);
        Throwable throwableAmount1 = assertThrows(RequestException.class, () -> {
            depositService.postDeposit(requestDTOBadAmount1);
        });
        Assertions.assertEquals(throwableAmount1.getMessage(),
                "Сумма пополнения должна быть положительной");
    }

    @Test
    void postDepositBadAmount2() {
        DepositRequestDTO requestDTOBadAmount2 = new DepositRequestDTO("11111", BigDecimal.valueOf(-5051.51));
        Throwable throwableAmount2 = assertThrows(RequestException.class, () -> {
            depositService.postDeposit(requestDTOBadAmount2);
        });
        Assertions.assertEquals(throwableAmount2.getMessage(),
                "Сумма пополнения должна быть положительной");
    }
}