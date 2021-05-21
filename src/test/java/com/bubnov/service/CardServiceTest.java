package com.bubnov.service;

import com.bubnov.controller.dto.card.CardRequestDTO;
import com.bubnov.controller.dto.card.CardResponseDTO;
import com.bubnov.exception.DatabaseException;
import com.bubnov.exception.RequestException;
import com.bubnov.repository.BillRepository;
import com.bubnov.repository.CardRepository;
import com.bubnov.repository.H2Datasource;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.h2.tools.RunScript;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

class CardServiceTest {

    CardRepository cardRepository = CardRepository.getInstance();
    BillRepository billRepository = BillRepository.getInstance();
    String databasePath = "jdbc:h2:mem:db;DB_CLOSE_DELAY=-1";
    String databaseScript = "src/main/resources/tests/testCardDatabase.sql";
    String databaseScriptDel = "src/main/resources/tests/deleteTestCardDatabase.sql";
    H2Datasource datasource = new H2Datasource(databasePath);
    CardService cardService;

    @BeforeEach
    void setUp() throws DatabaseException, FileNotFoundException, SQLException {
        Connection db = datasource.setH2Connection();
        RunScript.execute(db, new FileReader(databaseScript));
        cardRepository.setH2Datasource(datasource);
        billRepository.setH2Datasource(datasource);
        cardService = new CardService(cardRepository, billRepository);
    }

    @AfterEach
    void tearDown() throws DatabaseException, FileNotFoundException, SQLException {
        Connection db = datasource.setH2Connection();
        RunScript.execute(db, new FileReader(databaseScriptDel));
    }

    @Test
    void getCardsByBillNumber() throws RequestException, DatabaseException, JsonProcessingException {
        String request = "11111";
        List<CardResponseDTO> cardsByBillNumber = cardService.getCardsByBillNumber(request);
        List<CardResponseDTO> cards = new ArrayList<>();
        CardResponseDTO card = new CardResponseDTO("1111222233334444");
        CardResponseDTO card2 = new CardResponseDTO("1234123412341234");
        cards.add(card);
        cards.add(card2);
        Assertions.assertEquals(cards, cardsByBillNumber);
    }

    @Test
    void getCardsThrow() {
        String requestBad = "11112";
        Throwable throwable = assertThrows(RequestException.class, () -> {
            cardService.getCardsByBillNumber(requestBad);
        });
        Assertions.assertEquals(throwable.getMessage(), "Не удалось найти карты по счету: " +
                requestBad);
    }

    @Test
    void createCard() throws RequestException, DatabaseException, JsonProcessingException {
        CardRequestDTO requestDTO = new CardRequestDTO("0000000000000000", "11111");
        CardResponseDTO card = cardService.createCard(requestDTO);
        CardResponseDTO responseDTO = new CardResponseDTO("0000000000000000");
        Assertions.assertEquals(card, responseDTO);
    }

    @Test
    void createCardThrowExist() {
        CardRequestDTO requestDTO = new CardRequestDTO("1111222233334444", "11111");
        Throwable throwableCard = assertThrows(RequestException.class, () -> {
            cardService.createCard(requestDTO);
        });
        Assertions.assertEquals(throwableCard.getMessage(),
                "Карта " + requestDTO.getCardNumber() + " уже существует");
    }

    @Test
    void createCardThrowNotExist() {
        CardRequestDTO requestDTOBad = new CardRequestDTO("0000000000000001", "11112");
        Throwable throwableBill = assertThrows(RequestException.class, () -> {
            cardService.createCard(requestDTOBad);
        });
        Assertions.assertEquals(throwableBill.getMessage(),
                "Счет " + requestDTOBad.getBillNumber() + " не создан");
    }
}