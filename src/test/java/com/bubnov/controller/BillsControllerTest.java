package com.bubnov.controller;

import com.bubnov.controller.dto.bill.AmountResponseDTO;
import com.bubnov.controller.dto.bill.BillRequestDTO;
import com.bubnov.controller.dto.bill.BillResponseDTO;
import com.bubnov.controller.dto.confirmation.ConfirmationRequestDTO;
import com.bubnov.controller.dto.confirmation.ConfirmationResponseDTO;
import com.bubnov.exception.DatabaseException;
import com.bubnov.exception.RequestException;
import com.bubnov.repository.AccountRepository;
import com.bubnov.repository.BillRepository;
import com.bubnov.repository.ConfirmationRepository;
import com.bubnov.repository.H2Datasource;
import com.bubnov.service.BillService;
import com.bubnov.service.ConfirmationService;
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
    ConfirmationRepository confirmationRepository = ConfirmationRepository.getInstance();
    AccountRepository accountRepository = AccountRepository.getInstance();
    String databasePath = "jdbc:h2:mem:db;DB_CLOSE_DELAY=-1";
    String databaseScript = "src/main/resources/tests/testCardDatabase.sql";
    String databaseScriptDel = "src/main/resources/tests/deleteTestCardDatabase.sql";
    H2Datasource datasource = new H2Datasource(databasePath);
    BillService billService;
    BillsController billsController;
    ConfirmationService confirmationService;
    ConfirmationController confirmationController;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws DatabaseException, FileNotFoundException, SQLException {
        Connection db = datasource.setH2Connection();
        RunScript.execute(db, new FileReader(databaseScript));
        billRepository.setH2Datasource(datasource);
        confirmationRepository.setH2Datasource(datasource);
        accountRepository.setH2Datasource(datasource);
        billService = new BillService(billRepository);
        confirmationService = new ConfirmationService(confirmationRepository);
        billsController = new BillsController(billService, confirmationService);
    }

    @AfterEach
    void tearDown() throws DatabaseException, FileNotFoundException, SQLException {
        Connection db = datasource.setH2Connection();
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

    @Test
    void postCreateBillConfirmation() throws IOException, SQLException, DatabaseException, RequestException {
        BillRequestDTO billDTO = new BillRequestDTO("12345", BigDecimal.valueOf(9000.98),1);
        InputStream input = new ByteArrayInputStream(objectMapper.writeValueAsBytes(billDTO));
        String ans = billsController.postCreateBillConfirmation(input);
        ConfirmationResponseDTO responseDTO = objectMapper.readValue(ans, ConfirmationResponseDTO.class);
        BillRequestDTO requestDTO = objectMapper.readValue(responseDTO.getInfo(), BillRequestDTO.class);
        Assertions.assertEquals(requestDTO.getAccountId(), 1);
        Assertions.assertEquals(requestDTO.getAmount(), BigDecimal.valueOf(9000.98));
        Assertions.assertEquals(requestDTO.getBillNumber(), "12345");
    }

}