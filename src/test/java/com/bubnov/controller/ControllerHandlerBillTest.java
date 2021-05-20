package com.bubnov.controller;

import com.bubnov.controller.dto.bill.AmountResponseDTO;
import com.bubnov.controller.dto.card.CardRequestDTO;
import com.bubnov.controller.dto.card.CardResponseDTO;
import com.bubnov.exception.DatabaseException;
import com.bubnov.repository.BillRepository;
import com.bubnov.repository.CardRepository;
import com.bubnov.repository.DepositRepository;
import com.bubnov.repository.H2Datasource;
import com.bubnov.service.BillService;
import com.bubnov.service.CardService;
import com.bubnov.service.DepositService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import org.h2.tools.RunScript;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

class ControllerHandlerBillTest {

    BillRepository billRepository = BillRepository.getInstance();
    CardRepository cardRepository = CardRepository.getInstance();
    DepositRepository depositRepository = DepositRepository.getInstance();
    H2Datasource datasource = new H2Datasource();
    String databasePath = "jdbc:h2:mem:db;DB_CLOSE_DELAY=-1";
    String databaseScript = "src/main/resources/tests/testCardDatabase.sql";
    String databaseScriptDel = "src/main/resources/tests/deleteTestCardDatabase.sql";
    BillService billService;
    DepositService depositService;
    CardService cardService;
    BillsController billsController;
    CardsController cardsController;
    DepositController depositController;
    ControllerHandler controllerHandler;
    int serverPort = 8000;
    ObjectMapper objectMapper = new ObjectMapper();
    HttpServer server;

    @BeforeEach
    void setUp() throws DatabaseException, IOException, SQLException {
        Connection db = datasource.setH2Connection(databasePath);
        RunScript.execute(db, new FileReader(databaseScript));
        cardRepository.setDatabasePath(databasePath);
        billRepository.setDatabasePath(databasePath);
        depositRepository.setDatabasePath(databasePath);
        cardService = new CardService(cardRepository, billRepository);
        billService = new BillService(billRepository);
        depositService = new DepositService(depositRepository, billRepository);
        cardsController = new CardsController(cardService);
        billsController = new BillsController(billService);
        depositController = new DepositController(depositService);
        controllerHandler = new ControllerHandler(cardsController, billsController, depositController);
        server = HttpServer.create(new InetSocketAddress(serverPort), 0);
        controllerHandler.startController(server);
    }

    @AfterEach
    void tearDown() throws DatabaseException, FileNotFoundException, SQLException {
        Connection db = datasource.setH2Connection(databasePath);
        RunScript.execute(db, new FileReader(databaseScriptDel));
        server.stop(0);
    }

    @Test
    void billGetOkTest() throws IOException {

        URL url = new URL("http://localhost:8000/clients/bills/11111");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        Assertions.assertEquals(connection.getResponseCode(), 200);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), "utf-8")
        );
        AmountResponseDTO responseDTO = objectMapper.readValue(in, AmountResponseDTO.class);
        in.close();
        Assertions.assertEquals(responseDTO.getAmount(), BigDecimal.valueOf(50000.05));
    }

    @Test
    void billGetNotFoundTest() throws IOException {
        URL url = new URL("http://localhost:8000/clients/bills/22221");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        Assertions.assertEquals(connection.getResponseCode(), 400);
        Assertions.assertEquals(errorMessage(connection), "Счет : 22221 не найден");
    }

    private String errorMessage(HttpURLConnection connection) throws IOException {
        BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "utf-8"));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = rd.readLine()) != null) {
            response.append(inputLine);
        }
        rd.close();
        return response.toString();
    }

    @Test
    void BILLGetBadRequestTest() throws IOException {
        URL url = new URL("http://localhost:8000/clients/bills/2222d");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        Assertions.assertEquals(connection.getResponseCode(), 400);
        Assertions.assertEquals(errorMessage(connection), "Некорректно задан счет");
    }
}