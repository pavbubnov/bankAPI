package com.bubnov.repository;

import com.bubnov.controller.dto.bill.BillRequestDTO;
import com.bubnov.controller.dto.confirmation.ConfirmationRequestDTO;
import com.bubnov.controller.dto.confirmation.ConfirmationResponseDTO;
import com.bubnov.controller.dto.confirmation.ConfirmationUpdateDTO;
import com.bubnov.exception.DatabaseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

class ConfirmationRepositoryTest {

    ConfirmationRepository confirmationRepository = ConfirmationRepository.getInstance();
    String databasePath = "jdbc:h2:mem:db;DB_CLOSE_DELAY=-1";
    String databaseScript = "src/main/resources/tests/testCardDatabase.sql";
    String databaseScriptDel = "src/main/resources/tests/deleteTestCardDatabase.sql";
    H2Datasource datasource = new H2Datasource(databasePath);

    @BeforeEach
    void setUp() throws DatabaseException, SQLException, FileNotFoundException {
        Connection db = datasource.setH2Connection();
        confirmationRepository.setH2Datasource(datasource);
        RunScript.execute(db, new FileReader(databaseScript));
    }

    @AfterEach
    void tearDown() throws DatabaseException, SQLException, FileNotFoundException {
        Connection db = datasource.setH2Connection();
        RunScript.execute(db, new FileReader(databaseScriptDel));
    }

    @Test
    void getALLNotConfirm() throws JsonProcessingException, DatabaseException, SQLException {
        BillRequestDTO billDTO = new BillRequestDTO("12345", BigDecimal.valueOf(9000), 1);
        ObjectMapper objectMapper = new ObjectMapper();
        String info = objectMapper.writeValueAsString(billDTO);
        ConfirmationRequestDTO confirmationRequestDTO = new ConfirmationRequestDTO("Bill", "POST",
                info, "NOT_CONFIRMED");
        confirmationRepository.createConfirmation(confirmationRequestDTO);
        ConfirmationResponseDTO expect = new ConfirmationResponseDTO(1, "Bill", "POST",
                info, "NOT_CONFIRMED");
        Assertions.assertEquals(confirmationRepository.getALLNotConfirm().get(0), expect);
    }

    @Test
    void getConfirmById() throws JsonProcessingException, DatabaseException, SQLException {
        BillRequestDTO billDTO = new BillRequestDTO("12345", BigDecimal.valueOf(9000), 1);
        ObjectMapper objectMapper = new ObjectMapper();
        String info = objectMapper.writeValueAsString(billDTO);
        ConfirmationRequestDTO confirmationRequestDTO = new ConfirmationRequestDTO("Bill", "POST",
                info, "NOT_CONFIRMED");
        confirmationRepository.createConfirmation(confirmationRequestDTO);
        ConfirmationResponseDTO expect = new ConfirmationResponseDTO(1, "Bill", "POST",
                info, "NOT_CONFIRMED");
        ConfirmationResponseDTO confirmById = confirmationRepository.getConfirmById(1);
        Assertions.assertEquals(confirmById, expect);
    }

    @Test
    void createConfirmation() throws JsonProcessingException, DatabaseException, SQLException {
        BillRequestDTO billDTO = new BillRequestDTO("12345", BigDecimal.valueOf(9000), 1);
        ObjectMapper objectMapper = new ObjectMapper();
        String info = objectMapper.writeValueAsString(billDTO);
        ConfirmationRequestDTO confirmationRequestDTO = new ConfirmationRequestDTO("Bill", "POST",
                info, "NOT_CONFIRMED");
        ConfirmationResponseDTO responseDTO = confirmationRepository.createConfirmation(confirmationRequestDTO);
        ConfirmationResponseDTO expect = new ConfirmationResponseDTO(1, "Bill", "POST",
                info, "NOT_CONFIRMED");
        Assertions.assertEquals(responseDTO, expect);
    }

    @Test
    void updateStatus() throws JsonProcessingException, DatabaseException, SQLException {
        BillRequestDTO billDTO = new BillRequestDTO("12345", BigDecimal.valueOf(9000), 1);
        ObjectMapper objectMapper = new ObjectMapper();
        String info = objectMapper.writeValueAsString(billDTO);
        ConfirmationRequestDTO confirmationRequestDTO = new ConfirmationRequestDTO("Bill", "POST",
                info, "NOT_CONFIRMED");
        confirmationRepository.createConfirmation(confirmationRequestDTO);
        ConfirmationResponseDTO expect = new ConfirmationResponseDTO(1, "Bill", "POST",
                info, "CONFIRMED");
        ConfirmationUpdateDTO updateDTO = new ConfirmationUpdateDTO(1, "CONFIRMED");
        confirmationRepository.updateStatus(updateDTO);
        Assertions.assertEquals(confirmationRepository.getConfirmById(1), expect);
    }
}