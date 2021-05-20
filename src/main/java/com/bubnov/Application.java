package com.bubnov;

import com.bubnov.controller.BillsController;
import com.bubnov.controller.CardsController;
import com.bubnov.controller.controllerhandler.ControllerHandler;
import com.bubnov.controller.DepositController;
import com.bubnov.exception.DatabaseException;
import com.bubnov.repository.BillRepository;
import com.bubnov.repository.CardRepository;
import com.bubnov.repository.DepositRepository;
import com.bubnov.repository.H2Datasource;
import com.bubnov.service.BillService;
import com.bubnov.service.CardService;
import com.bubnov.service.DepositService;
import com.sun.net.httpserver.HttpServer;
import org.h2.tools.RunScript;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class Application {

    public static void main(String[] args) {

        FileInputStream fileInputStream;
        Properties property = new Properties();
        try {
            fileInputStream = new FileInputStream("src/main/resources/path.properties");
            property.load(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String databaseFile = property.getProperty("database.file");
        String databaseScript = property.getProperty("database.script");
        String databasePath = property.getProperty("database.path");
        CardRepository cardRepository = CardRepository.getInstance();
        cardRepository.setDatabasePath(databasePath);
        BillRepository billRepository = BillRepository.getInstance();
        billRepository.setDatabasePath(databasePath);
        DepositRepository depositRepository = DepositRepository.getInstance();
        depositRepository.setDatabasePath(databasePath);
        H2Datasource datasource = new H2Datasource();
        try {
            new FileReader(databaseFile);
        } catch (FileNotFoundException e) {
            try (Connection db = datasource.setH2Connection(databasePath);
            ) {
                RunScript.execute(db, new FileReader(databaseScript));
            } catch (SQLException | DatabaseException throwables) {
                throwables.printStackTrace();
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
        }
        CardService cardService = new CardService(cardRepository, billRepository);
        BillService billService = new BillService(billRepository);
        DepositService depositService = new DepositService(depositRepository, billRepository);
        CardsController cardsController = new CardsController(cardService);
        BillsController billsController = new BillsController(billService);
        DepositController depositController = new DepositController(depositService);
        ControllerHandler controllerHandler = new ControllerHandler(cardsController, billsController, depositController);
        try {
            int serverPort = 8000;
            HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);
            controllerHandler.startController(server);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
