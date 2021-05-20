package com.bubnov.controller;

import com.bubnov.controller.dto.deposit.DepositRequestDTO;
import com.bubnov.controller.dto.deposit.DepositResponseDTO;
import com.bubnov.controller.controllerhandler.ControllerHandler;
import com.bubnov.entity.Deposit;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

class ControllerHandlerDepositTest {

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
    void postOkTest() throws IOException, DatabaseException, SQLException {
        URL url = new URL("http://localhost:8000/clients/deposits");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        DepositRequestDTO requestDTO = new DepositRequestDTO("22222", BigDecimal.valueOf(50.51));
        String jsonRequest = objectMapper.writeValueAsString(requestDTO);
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(jsonRequest);
        out.flush();
        out.close();
        Assertions.assertEquals(connection.getResponseCode(), 200);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), "utf-8")
        );
        DepositResponseDTO responseDTO = objectMapper.readValue(in, DepositResponseDTO.class);
        in.close();
        DepositResponseDTO responseDTOExpect = new DepositResponseDTO("22222",BigDecimal.valueOf(50.51),
                BigDecimal.valueOf(100050.51));
        Assertions.assertEquals(responseDTO, responseDTOExpect);
        Assertions.assertEquals(getDeposit("22222").get(0).getId(), 1);
        Assertions.assertEquals(getDeposit("22222").get(0).getBillNumber(), "22222");
        Assertions.assertEquals(getDeposit("22222").get(0).getAmount(), BigDecimal.valueOf(50.51));
    }

    @Test
    void postDepositBillNotExist() throws IOException {
        URL url = new URL("http://localhost:8000/clients/deposits");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        DepositRequestDTO requestDTO = new DepositRequestDTO("22221", BigDecimal.valueOf(50.51));
        String jsonRequest = objectMapper.writeValueAsString(requestDTO);
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(jsonRequest);
        out.flush();
        out.close();
        Assertions.assertEquals(connection.getResponseCode(), 400);
        Assertions.assertEquals(errorMessage(connection), "Не удалось найти счет: 22221");
    }

    @Test
    void postDepositBadAmount() throws IOException {
        URL url = new URL("http://localhost:8000/clients/deposits");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        DepositRequestDTO requestDTO = new DepositRequestDTO("22222", BigDecimal.valueOf(0));
        String jsonRequest = objectMapper.writeValueAsString(requestDTO);
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(jsonRequest);
        out.flush();
        out.close();
        Assertions.assertEquals(connection.getResponseCode(), 400);
        Assertions.assertEquals(errorMessage(connection), "Сумма пополнения должна быть положительной");
    }

    @Test
    void postDepositBadAmount2() throws IOException {
        URL url = new URL("http://localhost:8000/clients/deposits");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        DepositRequestDTO requestDTO = new DepositRequestDTO("22222", BigDecimal.valueOf(-50.90));
        String jsonRequest = objectMapper.writeValueAsString(requestDTO);
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(jsonRequest);
        out.flush();
        out.close();
        Assertions.assertEquals(connection.getResponseCode(), 400);
        Assertions.assertEquals(errorMessage(connection), "Сумма пополнения должна быть положительной");
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

    private static final String SELECT_DEPOSITS_BY_BILL = "SELECT * FROM DEPOSITS WHERE BILL_NUMBER = ?";
    private static final String COLUMN_ID = "ID";
    private static final String COLUMN_BILL_NUMBER = "BILL_NUMBER";
    private static final String COLUMN_AMOUNT = "AMOUNT";

    private List<Deposit> getDeposit(String billNumber) throws SQLException, DatabaseException {
        try (Connection db = datasource.setH2Connection(databasePath);
             PreparedStatement preparedStatement = db.prepareStatement(SELECT_DEPOSITS_BY_BILL);
        ) {
            preparedStatement.setString(1, billNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Deposit> deposits = new ArrayList<>();
            while (resultSet.next()) {
                Deposit deposit = new Deposit();
                deposit.setId(resultSet.getInt(COLUMN_ID));
                deposit.setBillNumber(resultSet.getString(COLUMN_BILL_NUMBER));
                deposit.setAmount(resultSet.getBigDecimal(COLUMN_AMOUNT));
                deposits.add(deposit);
            }
            return deposits;
        }
    }


}