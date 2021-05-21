package com.bubnov.repository;

import com.bubnov.controller.dto.card.CardRequestDTO;
import com.bubnov.controller.dto.card.CardResponseDTO;
import com.bubnov.exception.DatabaseException;
import org.h2.tools.RunScript;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;


class CardRepositoryTest {

    CardRepository cardRepository = CardRepository.getInstance();
    String databasePath = "jdbc:h2:mem:db;DB_CLOSE_DELAY=-1";
    String databaseScript = "src/main/resources/tests/testCardDatabase.sql";
    String databaseScriptDel = "src/main/resources/tests/deleteTestCardDatabase.sql";
    H2Datasource datasource = new H2Datasource(databasePath);

    @BeforeEach
    void setUp() throws DatabaseException, SQLException, FileNotFoundException {
        Connection db = datasource.setH2Connection();
        cardRepository.setH2Datasource(datasource);
        RunScript.execute(db, new FileReader(databaseScript));
    }

    @AfterEach
    void tearDown() throws DatabaseException, SQLException, FileNotFoundException {
        Connection db = datasource.setH2Connection();
        RunScript.execute(db, new FileReader(databaseScriptDel));
    }

    @Test
    void getAllCardsByBillNumber() throws DatabaseException, SQLException {
        List<String> numbers = getCardsList("11111");
        org.assertj.core.api.Assertions.assertThat(numbers).containsExactly("1111222233334444", "1234123412341234");
        Assertions.assertTrue(cardRepository.getAllCardsByBillNumber("11112").size() == 0);
    }

    @Test
    void createCard() throws DatabaseException, SQLException {
        List<String> numbers = getCardsList("11111");
        org.assertj.core.api.Assertions.assertThat(numbers).containsExactly("1111222233334444", "1234123412341234");
        CardRequestDTO newCard = new CardRequestDTO("000000000000000000", "11111");
        cardRepository.createCard(newCard);
        numbers = getCardsList("11111");
        org.assertj.core.api.Assertions.assertThat(numbers).
                containsExactly("1111222233334444", "1234123412341234", "000000000000000000");

    }

    @Test
    void checkCardExists() throws DatabaseException, SQLException {
        CardRequestDTO newCard = new CardRequestDTO("0000000000000000", "11111");
        Assertions.assertTrue(cardRepository.checkCardExists("0000000000000000"));
        cardRepository.createCard(newCard);
        Assertions.assertFalse(cardRepository.checkCardExists("0000000000000000"));
    }

    private List<String> getCardsList(String billNumber) throws DatabaseException, SQLException {
        List<CardResponseDTO> allCardsByBillNumber = cardRepository.getAllCardsByBillNumber(billNumber);
        List<String> numbers = allCardsByBillNumber.stream().map(cardResponseDTO ->
                cardResponseDTO.getCardNumber()).collect(Collectors.toList());
        return numbers;
    }
}