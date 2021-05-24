package com.bubnov.controller.controllerhandler;

import com.bubnov.controller.*;
import com.bubnov.controller.dto.account.AccountDTO;
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
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;

class AccountHandlerTest {

    private String databasePath = "jdbc:h2:mem:db;DB_CLOSE_DELAY=-1";
    private String databaseScript = "src/main/resources/tests/testCardDatabase.sql";
    private String databaseScriptDel = "src/main/resources/tests/deleteTestCardDatabase.sql";
    private AccountRepository accountRepository = AccountRepository.getInstance();
    private CardRepository cardRepository = CardRepository.getInstance();
    private BillRepository billRepository = BillRepository.getInstance();
    private DepositRepository depositRepository = DepositRepository.getInstance();
    private CounterpartyRepository counterpartyRepository = CounterpartyRepository.getInstance();
    private TransferRepository transferRepository = TransferRepository.getInstance();
    private ConfirmationRepository confirmationRepository = ConfirmationRepository.getInstance();
    private AccountService accountService;
    private CardService cardService;
    private BillService billService;
    private DepositService depositService;
    private CounterpartyService counterpartyService;
    private TransferService transferService;
    private ConfirmationService confirmationService;
    private AccountController accountController;
    private CardsController cardController;
    private BillsController billController;
    private DepositController depositController;
    private CounterpartyController counterpartyController;
    private TransferController transferController;
    private ConfirmationController confirmationController;
    private ControllerHandler controllerHandler;
    private int serverPort = 8000;
    private ObjectMapper objectMapper = new ObjectMapper();
    private HttpServer server;
    private H2Datasource datasource = new H2Datasource(databasePath);

    @BeforeEach
    void setUp() throws DatabaseException, IOException, SQLException {
        Connection db = datasource.setH2Connection();
        RunScript.execute(db, new FileReader(databaseScript));
        accountRepository.setH2Datasource(datasource);
        cardRepository.setH2Datasource(datasource);
        billRepository.setH2Datasource(datasource);
        depositRepository.setH2Datasource(datasource);
        counterpartyRepository.setH2Datasource(datasource);
        transferRepository.setH2Datasource(datasource);
        confirmationRepository.setH2Datasource(datasource);
        accountService = new AccountService(accountRepository);
        cardService = new CardService(cardRepository, billRepository);
        billService = new BillService(billRepository);
        depositService = new DepositService(depositRepository, billRepository);
        counterpartyService = new CounterpartyService(counterpartyRepository, accountRepository);
        transferService = new TransferService(transferRepository, billRepository, counterpartyRepository);
        confirmationService = new ConfirmationService(confirmationRepository);
        accountController = new AccountController(confirmationService);
        cardController = new CardsController(cardService);
        billController = new BillsController(billService, confirmationService);
        depositController = new DepositController(depositService);
        counterpartyController = new CounterpartyController(counterpartyService);
        transferController = new TransferController(transferService);
        confirmationController = new ConfirmationController(confirmationService, accountService, billService);
        controllerHandler = new ControllerHandler(cardController, billController, depositController,
                counterpartyController, transferController, confirmationController, accountController);
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
    void postBillOkTest() throws IOException, DatabaseException, SQLException {

        URL url = new URL("http://localhost:8000/clients/accounts");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        AccountDTO requestDTO = new AccountDTO("Irina");
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
        Assertions.assertEquals(confirmationResponseDTO.getEntityName(), "Account");
        Assertions.assertEquals(confirmationResponseDTO.getOperation(), "POST");
        Assertions.assertEquals(confirmationResponseDTO.getConfirmationStatus(), "NOT_CONFIRMED");
        Assertions.assertEquals(confirmationResponseDTO.getInfo(), jsonRequest);
        Assertions.assertEquals(confirmationRepository.getConfirmById(1), confirmationResponseDTO);
    }

}