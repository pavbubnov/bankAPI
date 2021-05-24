package com.bubnov.controller;

import com.bubnov.controller.dto.deposit.DepositRequestDTO;
import com.bubnov.controller.dto.deposit.DepositResponseDTO;
import com.bubnov.exception.DatabaseException;
import com.bubnov.exception.RequestException;
import com.bubnov.repository.BillRepository;
import com.bubnov.repository.DepositRepository;
import com.bubnov.repository.H2Datasource;
import com.bubnov.service.DepositService;
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

import static org.junit.jupiter.api.Assertions.assertThrows;

class DepositControllerTest {

    private DepositRepository depositRepository = DepositRepository.getInstance();
    private BillRepository billRepository = BillRepository.getInstance();
    private String databasePath = "jdbc:h2:mem:db;DB_CLOSE_DELAY=-1";
    private String databaseScript = "src/main/resources/tests/testCardDatabase.sql";
    private String databaseScriptDel = "src/main/resources/tests/deleteTestCardDatabase.sql";
    private H2Datasource datasource = new H2Datasource(databasePath);
    private DepositService depositService;
    private DepositController depositController;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws DatabaseException, FileNotFoundException, SQLException {
        Connection db = datasource.setH2Connection();
        RunScript.execute(db, new FileReader(databaseScript));
        depositRepository.setH2Datasource(datasource);
        billRepository.setH2Datasource(datasource);
        depositService = new DepositService(depositRepository, billRepository);
        depositController = new DepositController(depositService);
    }

    @AfterEach
    void tearDown() throws DatabaseException, FileNotFoundException, SQLException {
        Connection db = datasource.setH2Connection();
        RunScript.execute(db, new FileReader(databaseScriptDel));
    }

    @Test
    void postDeposit() throws RequestException, DatabaseException, IOException {
        DepositRequestDTO requestDTO = new DepositRequestDTO("11111", BigDecimal.valueOf(5051.51));
        InputStream input = new ByteArrayInputStream(objectMapper.writeValueAsBytes(requestDTO));
        String out = depositController.postDeposit(input);
        DepositResponseDTO responseExpect = new DepositResponseDTO("11111", BigDecimal.valueOf(5051.51),
                BigDecimal.valueOf(55051.56));
        String jsonExpect = objectMapper.writeValueAsString(responseExpect);
        Assertions.assertEquals(out,jsonExpect);
    }

    @Test
    void postDepositBadNum() throws JsonProcessingException {
        DepositRequestDTO requestDTOBadNum = new DepositRequestDTO("11112", BigDecimal.valueOf(5051.51));
        InputStream input = new ByteArrayInputStream(objectMapper.writeValueAsBytes(requestDTOBadNum));
        Throwable throwableNum = assertThrows(RequestException.class, () -> {
            depositController.postDeposit(input);
        });
        Assertions.assertEquals(throwableNum.getMessage(),
                "Не удалось найти счет: " + requestDTOBadNum.getBillNumber());
    }

    @Test
    void postDepositBadAmount() throws JsonProcessingException {
        DepositRequestDTO requestDTOBadAmount1 = new DepositRequestDTO("11111", BigDecimal.ZERO);
        InputStream input = new ByteArrayInputStream(objectMapper.writeValueAsBytes(requestDTOBadAmount1));
        Throwable throwableAmount1 = assertThrows(RequestException.class, () -> {
            depositController.postDeposit(input);
        });
        Assertions.assertEquals(throwableAmount1.getMessage(),
                "Сумма пополнения должна быть положительной");
    }

    @Test
    void postDepositBadAmount2() throws JsonProcessingException {
        DepositRequestDTO requestDTOBadAmount2 = new DepositRequestDTO("11111", BigDecimal.valueOf(-5051.51));
        InputStream input = new ByteArrayInputStream(objectMapper.writeValueAsBytes(requestDTOBadAmount2));
        Throwable throwableAmount2 = assertThrows(RequestException.class, () -> {
            depositController.postDeposit(input);
        });
        Assertions.assertEquals(throwableAmount2.getMessage(),
                "Сумма пополнения должна быть положительной");
    }
}