package com.bubnov.controller.controllerhandler;

import com.bubnov.controller.BillsController;
import com.bubnov.controller.CardsController;
import com.bubnov.controller.DepositController;
import com.bubnov.controller.dto.bill.AmountResponseDTO;
import com.bubnov.controller.dto.bill.BillRequestDTO;
import com.bubnov.controller.dto.card.CardRequestDTO;
import com.bubnov.controller.dto.card.CardResponseDTO;
import com.bubnov.controller.dto.confirmation.ConfirmationResponseDTO;
import com.bubnov.exception.DatabaseException;
import com.bubnov.repository.*;
import com.bubnov.service.*;
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
import java.util.stream.Collectors;

class ControllerHandlerBillTest {

    private BillRepository billRepository = BillRepository.getInstance();
    private CardRepository cardRepository = CardRepository.getInstance();
    private ConfirmationRepository confirmationRepository = ConfirmationRepository.getInstance();
    private DepositRepository depositRepository = DepositRepository.getInstance();
    private AccountRepository accountRepository = AccountRepository.getInstance();
    private String databasePath = "jdbc:h2:mem:db;DB_CLOSE_DELAY=-1";
    private String databaseScript = "src/main/resources/tests/testCardDatabase.sql";
    private String databaseScriptDel = "src/main/resources/tests/deleteTestCardDatabase.sql";
    private H2Datasource datasource = new H2Datasource(databasePath);
    private BillService billService;
    private DepositService depositService;
    private CardService cardService;
    private AccountService accountService;
    private ConfirmationService confirmationService;
    private BillsController billsController;
    private CardsController cardsController;
    private DepositController depositController;
    private ControllerHandler controllerHandler;
    private int serverPort = 8000;
    private ObjectMapper objectMapper = new ObjectMapper();
    private HttpServer server;

    @BeforeEach
    void setUp() throws DatabaseException, IOException, SQLException {

        AccountRepository accountRepository = AccountRepository.getInstance();
        accountRepository.setH2Datasource(datasource);
        Connection db = datasource.setH2Connection();
        RunScript.execute(db, new FileReader(databaseScript));
        cardRepository.setH2Datasource(datasource);
        billRepository.setH2Datasource(datasource);
        depositRepository.setH2Datasource(datasource);
        confirmationRepository.setH2Datasource(datasource);
        accountRepository.setH2Datasource(datasource);
        cardService = new CardService(cardRepository, billRepository);
        billService = new BillService(billRepository);
        accountService = new AccountService(accountRepository);
        confirmationService = new ConfirmationService(confirmationRepository);
        depositService = new DepositService(depositRepository, billRepository);
        cardsController = new CardsController(cardService);
        billsController = new BillsController(billService, confirmationService);
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

    @Test
    void BillGetBadRequestTest() throws IOException {
        URL url = new URL("http://localhost:8000/clients/bills/2222d");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        Assertions.assertEquals(connection.getResponseCode(), 400);
        Assertions.assertEquals(errorMessage(connection), "Некорректно задан номер");
    }

    @Test
    void postBillOkTest() throws IOException, DatabaseException, SQLException {

        URL url = new URL("http://localhost:8000/clients/bills");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        BillRequestDTO requestDTO = new BillRequestDTO("12345", BigDecimal.valueOf(9000.98), 1);
        String jsonRequest = objectMapper.writeValueAsString(requestDTO);
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(jsonRequest);
        out.flush();
        out.close();
        Assertions.assertEquals(connection.getResponseCode(), 200);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), "utf-8")
        );
        ConfirmationResponseDTO confirmationResponseDTO = objectMapper.readValue(in, ConfirmationResponseDTO.class);
        in.close();
        Assertions.assertEquals(confirmationResponseDTO.getId(), 1);
        Assertions.assertEquals(confirmationResponseDTO.getEntityName(), "Bill");
        Assertions.assertEquals(confirmationResponseDTO.getOperation(), "POST");
        Assertions.assertEquals(confirmationResponseDTO.getConfirmationStatus(), "NOT_CONFIRMED");
        BillRequestDTO billDTO = new BillRequestDTO("12345", BigDecimal.valueOf(9000.98), 1);
        String info = objectMapper.writeValueAsString(billDTO);
        Assertions.assertEquals(confirmationResponseDTO.getInfo(), info);
    }

    @Test
    void postBillExistBillTest() throws IOException, DatabaseException, SQLException {

        URL url = new URL("http://localhost:8000/clients/bills");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        BillRequestDTO requestDTO = new BillRequestDTO("11111", BigDecimal.valueOf(9000.98), 1);
        String jsonRequest = objectMapper.writeValueAsString(requestDTO);
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(jsonRequest);
        out.flush();
        out.close();
        Assertions.assertEquals(connection.getResponseCode(), 400);
        Assertions.assertEquals(errorMessage(connection), "Счет: 11111 уже существует");
    }

    @Test
    void postBillNotCorrectTest() throws IOException, DatabaseException, SQLException {

        URL url = new URL("http://localhost:8000/clients/bills");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        BillRequestDTO requestDTO = new BillRequestDTO("111t1", BigDecimal.valueOf(9000.98), 1);
        String jsonRequest = objectMapper.writeValueAsString(requestDTO);
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(jsonRequest);
        out.flush();
        out.close();
        Assertions.assertEquals(connection.getResponseCode(), 400);
        Assertions.assertEquals(errorMessage(connection), "Номер счета задан некорректно");
    }

    @Test
    void postAccountNotExistTest() throws IOException, DatabaseException, SQLException {

        URL url = new URL("http://localhost:8000/clients/bills");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        BillRequestDTO requestDTO = new BillRequestDTO("12345", BigDecimal.valueOf(9000.98), 7);
        String jsonRequest = objectMapper.writeValueAsString(requestDTO);
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(jsonRequest);
        out.flush();
        out.close();
        int responseCode = connection.getResponseCode();
        Assertions.assertEquals(connection.getResponseCode(), 400);
        Assertions.assertEquals(errorMessage(connection), "Аккаунта с id: 7 не существует");
    }

    @Test
    void postAccountNotCorrectTest() throws IOException, DatabaseException, SQLException {

        URL url = new URL("http://localhost:8000/clients/bills");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        BillRequestDTO requestDTO = new BillRequestDTO("12345", BigDecimal.valueOf(9000.98), -4);
        String jsonRequest = objectMapper.writeValueAsString(requestDTO);
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(jsonRequest);
        out.flush();
        out.close();
        Assertions.assertEquals(connection.getResponseCode(), 400);
        Assertions.assertEquals(errorMessage(connection), "Номер аккаунта должен быть положитльным");
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
