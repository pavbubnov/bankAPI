package com.bubnov.controller;

import com.bubnov.controller.dto.bill.BillRequestDTO;
import com.bubnov.exception.DatabaseException;
import com.bubnov.repository.BillRepository;
import com.bubnov.repository.CardRepository;
import com.bubnov.repository.DepositRepository;
import com.bubnov.repository.H2Datasource;
import com.bubnov.service.BillService;
import com.bubnov.service.CardService;
import com.bubnov.service.DepositService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.h2.tools.RunScript;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;

class ControllerHandlerTest {

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

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws DatabaseException, FileNotFoundException, SQLException {
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
    }

    @AfterEach
    void tearDown() throws DatabaseException, FileNotFoundException, SQLException {
        Connection db = datasource.setH2Connection(databasePath);
        RunScript.execute(db, new FileReader(databaseScriptDel));
    }

    @Test
    void startController() throws IOException {
        controllerHandler.startController();
        sendRequest();
    }

    private void sendRequest() {
        HttpURLConnection connection = null;

        try {

            BillRequestDTO billRequestDTO = new BillRequestDTO("11111");
            String jsonRequest = objectMapper.writeValueAsString(billRequestDTO);

            URL url = new URL("http://localhost:8000/clients/cards/22222");

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            System.out.println(connection.getRequestMethod());
            connection.setRequestProperty("Accept-Charset", "UTF-8");
            //connection.setRequestProperty("Content-Type", "application/json");
           // connection.setRequestProperty("Accept", "application/json");

            connection.setRequestProperty("Content-Length",
                    Integer.toString(jsonRequest.getBytes().length));
            //connection.setRequestProperty("Content-Length", Integer.valueOf(0).toString());
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setDoInput(true);
//
//            connection.setConnectTimeout(5000);
//            connection.setReadTimeout(5000);
//
//            Map<String, String> parameters = new HashMap<>();
//            parameters.put("param1", jsonRequest);
//
//            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
//            out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
//            out.flush();
//            out.close();
//
//            DataOutputStream wr = new DataOutputStream (
//                    connection.getOutputStream());
//            wr.writeBytes(jsonRequest);
//            wr.flush();
//            wr.close();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream())
            );
            int status = connection.getResponseCode();
            System.out.println(status);

//            InputStream is = connection.getInputStream();
//            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
//            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
//            String line;
//            while ((line = rd.readLine()) != null) {
//                response.append(line);
//                response.append('\r');
//            }
//            rd.close();
//            System.out.println(response.toString());

            //Get Response
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), "utf-8"));
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
            System.out.println("" + sb.toString());
            //InputStream is = connection.getInputStream();
//            List<CardResponseDTO> list = objectMapper.readValue(is, objectMapper.getTypeFactory().constructCollectionType(List.class, CardResponseDTO.class));
//            System.out.println(list);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}