package com.bubnov.service;

import com.bubnov.controller.dto.bill.BillRequestDTO;
import com.bubnov.controller.dto.confirmation.ConfirmationRequestDTO;
import com.bubnov.controller.dto.confirmation.ConfirmationResponseDTO;
import com.bubnov.controller.dto.confirmation.ConfirmationUpdateDTO;
import com.bubnov.exception.DatabaseException;
import com.bubnov.exception.RequestException;
import com.bubnov.repository.AccountRepository;
import com.bubnov.repository.BillRepository;
import com.bubnov.repository.ConfirmationRepository;
import com.bubnov.repository.H2Datasource;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.h2.tools.RunScript;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ConfirmationServiceTest {

    private ConfirmationRepository confirmationRepository = ConfirmationRepository.getInstance();
    private AccountRepository accountRepository = AccountRepository.getInstance();
    private BillRepository billRepository = BillRepository.getInstance();
    private String databasePath = "jdbc:h2:mem:db;DB_CLOSE_DELAY=-1";
    private String databaseScript = "src/main/resources/tests/testCardDatabase.sql";
    private String databaseScriptDel = "src/main/resources/tests/deleteTestCardDatabase.sql";
    private H2Datasource datasource = new H2Datasource(databasePath);
    private ConfirmationService confirmationService;

    @BeforeEach
    void setUp() throws DatabaseException, FileNotFoundException, SQLException {
        Connection db = datasource.setH2Connection();
        RunScript.execute(db, new FileReader(databaseScript));
        confirmationRepository.setH2Datasource(datasource);
        accountRepository.setH2Datasource(datasource);
        billRepository.setH2Datasource(datasource);
        confirmationService = new ConfirmationService(confirmationRepository);
    }

    @AfterEach
    void tearDown() throws DatabaseException, FileNotFoundException, SQLException {
        Connection db = datasource.setH2Connection();
        RunScript.execute(db, new FileReader(databaseScriptDel));
    }

    @Test
    void postConfirmation() throws IOException, SQLException, RequestException, DatabaseException {
        BillRequestDTO billDTO = new BillRequestDTO("12345", BigDecimal.valueOf(9000), 1);
        ObjectMapper objectMapper = new ObjectMapper();
        String info = objectMapper.writeValueAsString(billDTO);
        ConfirmationRequestDTO requestDTO = new ConfirmationRequestDTO("Bill", "POST",
                info, "NOT_CONFIRMED");
        ConfirmationResponseDTO responseDTO = new ConfirmationResponseDTO(1, "Bill", "POST",
                info, "NOT_CONFIRMED");
        Assertions.assertEquals(confirmationService.postConfirmation(requestDTO), responseDTO);
    }

    @Test
    void postConfirmationBadAccount1() throws JsonProcessingException {
        BillRequestDTO billDTO = new BillRequestDTO("12345", BigDecimal.valueOf(9000), 0);
        ObjectMapper objectMapper = new ObjectMapper();
        String info = objectMapper.writeValueAsString(billDTO);
        ConfirmationRequestDTO requestDTO = new ConfirmationRequestDTO("Bill", "POST",
                info, "NOT_CONFIRMED");
        Throwable throwable = assertThrows(RequestException.class, () -> {
            confirmationService.postConfirmation(requestDTO);
        });
        Assertions.assertEquals(throwable.getMessage(), "Номер аккаунта должен быть положитльным");
    }

    @Test
    void postConfirmationBadAccount2() throws JsonProcessingException {
        BillRequestDTO billDTO = new BillRequestDTO("12345", BigDecimal.valueOf(9000), 10);
        ObjectMapper objectMapper = new ObjectMapper();
        String info = objectMapper.writeValueAsString(billDTO);
        ConfirmationRequestDTO requestDTO = new ConfirmationRequestDTO("Bill", "POST",
                info, "NOT_CONFIRMED");
        Throwable throwable = assertThrows(RequestException.class, () -> {
            confirmationService.postConfirmation(requestDTO);
        });
        Assertions.assertEquals(throwable.getMessage(), "Аккаунта с id: 10 не существует");
    }

    @Test
    void postConfirmationBadBill1() throws JsonProcessingException {
        BillRequestDTO billDTO = new BillRequestDTO("12t45", BigDecimal.valueOf(9000), 1);
        ObjectMapper objectMapper = new ObjectMapper();
        String info = objectMapper.writeValueAsString(billDTO);
        ConfirmationRequestDTO requestDTO = new ConfirmationRequestDTO("Bill", "POST",
                info, "NOT_CONFIRMED");
        Throwable throwable = assertThrows(RequestException.class, () -> {
            confirmationService.postConfirmation(requestDTO);
        });
        Assertions.assertEquals(throwable.getMessage(), "Номер счета задан некорректно");
    }

    @Test
    void postConfirmationBadBill2() throws JsonProcessingException {
        BillRequestDTO billDTO = new BillRequestDTO("22222", BigDecimal.valueOf(9000), 1);
        ObjectMapper objectMapper = new ObjectMapper();
        String info = objectMapper.writeValueAsString(billDTO);
        ConfirmationRequestDTO requestDTO = new ConfirmationRequestDTO("Bill", "POST",
                info, "NOT_CONFIRMED");
        Throwable throwable = assertThrows(RequestException.class, () -> {
            confirmationService.postConfirmation(requestDTO);
        });
        Assertions.assertEquals(throwable.getMessage(), "Счет: 22222 уже существует");
    }

    @Test
    void getUnconfirm() throws IOException, DatabaseException, SQLException, RequestException {
        ObjectMapper objectMapper = new ObjectMapper();
        BillRequestDTO billDTO = new BillRequestDTO("12345", BigDecimal.valueOf(9000), 1);
        BillRequestDTO billDTO2 = new BillRequestDTO("12346", BigDecimal.valueOf(9000), 1);
        String info = objectMapper.writeValueAsString(billDTO);
        String info2 = objectMapper.writeValueAsString(billDTO2);
        ConfirmationRequestDTO requestDTO = new ConfirmationRequestDTO("Bill", "POST",
                info, "NOT_CONFIRMED");
        ConfirmationRequestDTO requestDTO2 = new ConfirmationRequestDTO("Bill", "POST",
                info2, "NOT_CONFIRMED");

        ConfirmationResponseDTO responseDTO = new ConfirmationResponseDTO(1, "Bill", "POST",
                info, "NOT_CONFIRMED");
        ConfirmationResponseDTO responseDTO2 = new ConfirmationResponseDTO(2, "Bill", "POST",
                info2, "NOT_CONFIRMED");
        confirmationService.postConfirmation(requestDTO);
        confirmationService.postConfirmation(requestDTO2);
        List<ConfirmationResponseDTO> unconfirms = confirmationService.getUnconfirm();
        org.assertj.core.api.Assertions.assertThat(unconfirms).containsExactly(responseDTO, responseDTO2);
    }

    @Test
    void updateStatus() throws IOException, SQLException, RequestException, DatabaseException {
        BillRequestDTO billDTO = new BillRequestDTO("12345", BigDecimal.valueOf(9000), 1);
        ObjectMapper objectMapper = new ObjectMapper();
        String info = objectMapper.writeValueAsString(billDTO);
        ConfirmationRequestDTO requestDTO = new ConfirmationRequestDTO("Bill", "POST",
                info, "NOT_CONFIRMED");
        confirmationService.postConfirmation(requestDTO);
        ConfirmationUpdateDTO updateDTO = new ConfirmationUpdateDTO(1, "CONFIRMED");
        ConfirmationResponseDTO responseDTO = new ConfirmationResponseDTO(1, "Bill", "POST",
                info, "CONFIRMED");
        Assertions.assertEquals(confirmationService.updateStatus(updateDTO), updateDTO);
    }

    @Test
    void getById() throws IOException, SQLException, RequestException, DatabaseException {
        BillRequestDTO billDTO = new BillRequestDTO("12345", BigDecimal.valueOf(9000), 1);
        ObjectMapper objectMapper = new ObjectMapper();
        String info = objectMapper.writeValueAsString(billDTO);
        ConfirmationRequestDTO requestDTO = new ConfirmationRequestDTO("Bill", "POST",
                info, "NOT_CONFIRMED");
        ConfirmationResponseDTO responseDTO = new ConfirmationResponseDTO(1, "Bill", "POST",
                info, "NOT_CONFIRMED");
        confirmationService.postConfirmation(requestDTO);
        Assertions.assertEquals(confirmationService.getById(1), responseDTO);
    }
}