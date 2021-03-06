package com.bubnov.controller.controllerhandler;

import com.bubnov.controller.BillsController;
import com.bubnov.controller.CardsController;
import com.bubnov.controller.DepositController;
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
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

class ControllerHandlerCardTest {

    private BillRepository billRepository = BillRepository.getInstance();
    private CardRepository cardRepository = CardRepository.getInstance();
    private DepositRepository depositRepository = DepositRepository.getInstance();
    private String databasePath = "jdbc:h2:mem:db;DB_CLOSE_DELAY=-1";
    private String databaseScript = "src/main/resources/tests/testCardDatabase.sql";
    private String databaseScriptDel = "src/main/resources/tests/deleteTestCardDatabase.sql";
    private H2Datasource datasource = new H2Datasource(databasePath);
    private BillService billService;
    private DepositService depositService;
    private CardService cardService;
    private BillsController billsController;
    private CardsController cardsController;
    private DepositController depositController;
    private ControllerHandler controllerHandler;
    private int serverPort = 8000;
    private ObjectMapper objectMapper = new ObjectMapper();
    private HttpServer server;

    @BeforeEach
    void setUp() throws DatabaseException, IOException, SQLException {
        Connection db = datasource.setH2Connection();
        RunScript.execute(db, new FileReader(databaseScript));
        cardRepository.setH2Datasource(datasource);
        billRepository.setH2Datasource(datasource);
        depositRepository.setH2Datasource(datasource);
        cardService = new CardService(cardRepository, billRepository);
        billService = new BillService(billRepository);
        depositService = new DepositService(depositRepository, billRepository);
        cardsController = new CardsController(cardService);
        billsController = new BillsController(billService, null);
        depositController = new DepositController(depositService);
        controllerHandler = new ControllerHandler(cardsController, billsController, depositController);
        server = HttpServer.create(new InetSocketAddress(serverPort), 0);
        controllerHandler.startController(server);
    }

    @AfterEach
    void tearDown() throws DatabaseException, FileNotFoundException, SQLException {
        Connection db = datasource.setH2Connection();
        RunScript.execute(db, new FileReader(databaseScriptDel));
        server.stop(0);
    }

    @Test
    void cardGetOkTest() throws IOException {

        URL url = new URL("http://localhost:8000/clients/cards/22222");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        Assertions.assertEquals(connection.getResponseCode(), 200);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), "utf-8")
        );
        List<CardResponseDTO> list = objectMapper.readValue
                (in, objectMapper.getTypeFactory().constructCollectionType(List.class, CardResponseDTO.class));
        List<String> cards = list.stream().map(cardResponseDTO ->
                cardResponseDTO.getCardNumber()).collect(Collectors.toList());
        in.close();
        org.assertj.core.api.Assertions.assertThat(cards).
                containsExactly("1122223333444455");

    }

    @Test
    void cardGetNotFoundTest() throws IOException {
        URL url = new URL("http://localhost:8000/clients/cards/22221");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        Assertions.assertEquals(connection.getResponseCode(), 400);
        Assertions.assertEquals(errorMessage(connection), "???? ?????????????? ?????????? ?????????? ???? ??????????: 22221");
    }

    @Test
    void cardGetBadRequestTest() throws IOException {
        URL url = new URL("http://localhost:8000/clients/cards/2222d");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        Assertions.assertEquals(connection.getResponseCode(), 400);
        Assertions.assertEquals(errorMessage(connection), "?????????????????????? ?????????? ??????????");
    }

    @Test
    void postOkTest() throws IOException, DatabaseException, SQLException {
        URL url = new URL("http://localhost:8000/clients/cards");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        CardRequestDTO requestDTO = new CardRequestDTO("0000000000000000", "22222");
        String jsonRequest = objectMapper.writeValueAsString(requestDTO);
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(jsonRequest);
        out.flush();
        out.close();
        Assertions.assertEquals(connection.getResponseCode(), 200);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), "utf-8")
        );
        CardResponseDTO responseDTO = objectMapper.readValue(in, CardResponseDTO.class);
        in.close();
        Assertions.assertEquals(responseDTO.getCardNumber(), "0000000000000000");
        org.assertj.core.api.Assertions.assertThat(cardRepository.getAllCardsByBillNumber("22222").stream().
                map(cardResponseDTO ->
                        cardResponseDTO.getCardNumber()).collect(Collectors.toList())).
                containsExactly("1122223333444455", "0000000000000000");
    }

    @Test
    void postBillNotExistTest() throws IOException {
        URL url = new URL("http://localhost:8000/clients/cards");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        CardRequestDTO requestDTO = new CardRequestDTO("0000000000000000", "22221");
        String jsonRequest = objectMapper.writeValueAsString(requestDTO);
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(jsonRequest);
        out.flush();
        out.close();
        Assertions.assertEquals(connection.getResponseCode(), 400);
        Assertions.assertEquals(errorMessage(connection), "???????? 22221 ???? ????????????");
    }

    @Test
    void postCardExistTest() throws IOException {
        URL url = new URL("http://localhost:8000/clients/cards");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        CardRequestDTO requestDTO = new CardRequestDTO("1122223333444455", "22222");
        String jsonRequest = objectMapper.writeValueAsString(requestDTO);
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(jsonRequest);
        out.flush();
        out.close();
        Assertions.assertEquals(connection.getResponseCode(), 400);
        Assertions.assertEquals(errorMessage(connection), "?????????? 1122223333444455 ?????? ????????????????????");
    }

    @Test
    void postBadBody() throws IOException {
        URL url = new URL("http://localhost:8000/clients/cards");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        CardRequestDTO requestDTO = new CardRequestDTO("1122223333444455", "22222");
        String jsonRequest = objectMapper.writeValueAsString(requestDTO + "error");
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(jsonRequest);
        out.flush();
        out.close();
        Assertions.assertEquals(connection.getResponseCode(), 500);
        Assertions.assertEquals(errorMessage(connection), "???????????? ??????????????, ?????????????????? ???????? ??????????????");
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
}