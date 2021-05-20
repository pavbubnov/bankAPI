package com.bubnov.controller;

import com.bubnov.controller.dto.card.CardRequestDTO;
import com.bubnov.controller.dto.card.CardResponseDTO;
import com.bubnov.exception.DatabaseException;
import com.bubnov.exception.RequestException;
import com.bubnov.repository.BillRepository;
import com.bubnov.repository.CardRepository;
import com.bubnov.repository.H2Datasource;
import com.bubnov.service.CardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.h2.tools.RunScript;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class CardsControllerTest {

    CardRepository cardRepository = CardRepository.getInstance();
    BillRepository billRepository = BillRepository.getInstance();
    H2Datasource datasource = new H2Datasource();
    String databasePath = "jdbc:h2:mem:db;DB_CLOSE_DELAY=-1";
    String databaseScript = "src/main/resources/tests/testCardDatabase.sql";
    String databaseScriptDel = "src/main/resources/tests/deleteTestCardDatabase.sql";
    CardService cardService;
    CardsController cardsController;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws DatabaseException, FileNotFoundException, SQLException {
        Connection db = datasource.setH2Connection(databasePath);
        RunScript.execute(db, new FileReader(databaseScript));
        cardRepository.setDatabasePath(databasePath);
        billRepository.setDatabasePath(databasePath);
        cardService = new CardService(cardRepository, billRepository);
        cardsController = new CardsController(cardService);
    }

    @AfterEach
    void tearDown() throws DatabaseException, FileNotFoundException, SQLException {
        Connection db = datasource.setH2Connection(databasePath);
        RunScript.execute(db, new FileReader(databaseScriptDel));
    }

    @Test
    void getCard() throws IOException, DatabaseException, RequestException {
        String request = "11111";
        String out = cardsController.getCards(request);
        List<CardResponseDTO> list = new ArrayList<>();
        CardResponseDTO cardResponseDTO1 = new CardResponseDTO("1111222233334444");
        CardResponseDTO cardResponseDTO2 = new CardResponseDTO("1234123412341234");
        list.add(cardResponseDTO1);
        list.add(cardResponseDTO2);
        String jsonExpect = objectMapper.writeValueAsString(list);
        Assertions.assertEquals(out, jsonExpect);
    }

    @Test
    void getCardThrow() throws IOException {
        String requestBad = "11112";
        Throwable throwable = assertThrows(RequestException.class, () -> {
            cardsController.getCards(requestBad);
        });
        Assertions.assertEquals(throwable.getMessage(), "Не удалось найти карты по счету: " + requestBad);
    }

    @Test
    void postCards() throws IOException, RequestException {
        CardRequestDTO requestDTO = new CardRequestDTO("0000000000000000", "11111");
        InputStream input = new ByteArrayInputStream(objectMapper.writeValueAsBytes(requestDTO));
        String out = cardsController.postCard(input);
        CardResponseDTO responseDTO = new CardResponseDTO("0000000000000000");
        String jsonExpect = objectMapper.writeValueAsString(responseDTO);
        Assertions.assertEquals(out, jsonExpect);
    }

    @Test
    void postCardThrowExist() throws IOException, RequestException {
        CardRequestDTO requestDTO = new CardRequestDTO("1111222233334444", "11111");
        InputStream input = new ByteArrayInputStream(objectMapper.writeValueAsBytes(requestDTO));
        Throwable throwableCard = assertThrows(RequestException.class, () -> {
            cardsController.postCard(input);
        });
        Assertions.assertEquals(throwableCard.getMessage(),
                "Карта " + requestDTO.getCardNumber() + " уже существует");
    }

    @Test
    void postCardThrowNotExist() throws IOException, RequestException {
        CardRequestDTO requestDTOBad = new CardRequestDTO("0000000000000001", "11112");
        InputStream input = new ByteArrayInputStream(objectMapper.writeValueAsBytes(requestDTOBad));
        Throwable throwableBill = assertThrows(RequestException.class, () -> {
            cardsController.postCard(input);
        });
        Assertions.assertEquals(throwableBill.getMessage(),
                "Счет " + requestDTOBad.getBillNumber() + " не создан");
    }
}