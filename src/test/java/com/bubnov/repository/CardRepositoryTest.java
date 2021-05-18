package com.bubnov.repository;

import com.bubnov.controller.dto.card.CardResponseDTO;
import com.bubnov.entity.Card;
import com.bubnov.exception.DatabaseException;
import org.h2.tools.RunScript;
import org.junit.jupiter.api.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CardRepositoryTest {

    Connection connection;
    CardRepository cardRepository;

    @BeforeEach
    void setUp() throws DatabaseException {
        H2ConnectionUtils h2ConnectionUtils = new H2ConnectionUtils();
        connection = h2ConnectionUtils.getH2Connection();
        getInstance();
        cardRepository.getConnection(connection);
    }


    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void getConnection() {
        try {
            cardRepository.getConnection(connection);
        } catch (Exception e){
            fail();
        }
    }

    @Test
    void getInstance() {
        cardRepository = CardRepository.getInstance();
        Assertions.assertTrue(cardRepository.getClass() == CardRepository.class);
    }
//
    @Test
    void getAllCardsByBillNumber() throws FileNotFoundException, SQLException {
        RunScript.execute(connection, new FileReader
                ("/Users/a19189145/Documents/workProjects/bankAPI/src/main/resources/testCardDatabase.sql"));
        List<CardResponseDTO> allCardsByBillNumber = cardRepository.getAllCardsByBillNumber("11111");


    }
//
//    @Test
//    void createCard() {
//    }
//
//    @Test
//    void checkCardExists() {
//    }
}