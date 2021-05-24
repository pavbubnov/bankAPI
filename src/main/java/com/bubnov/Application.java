package com.bubnov;

import com.bubnov.controller.*;
import com.bubnov.controller.controllerhandler.ControllerHandler;
import com.bubnov.exception.DatabaseException;
import com.bubnov.repository.*;
import com.bubnov.service.*;
import com.sun.net.httpserver.HttpServer;
import org.h2.tools.RunScript;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class Application {

    public static void main(String[] args) {

        Properties property = new Properties();
        Reader reader = null;
        try {
            property.load(Application.class.getResourceAsStream("/path.properties"));
            InputStream in = Application.class.getResourceAsStream("/startDatabase.sql");
            reader = new BufferedReader(new InputStreamReader
                    (in, Charset.forName(StandardCharsets.UTF_8.name())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String databaseFile = property.getProperty("database.file");
        String databasePath = property.getProperty("database.path");
        H2Datasource h2Datasource = new H2Datasource(databasePath);
        AccountRepository accountRepository = AccountRepository.getInstance();
        accountRepository.setH2Datasource(h2Datasource);
        CardRepository cardRepository = CardRepository.getInstance();
        cardRepository.setH2Datasource(h2Datasource);
        BillRepository billRepository = BillRepository.getInstance();
        billRepository.setH2Datasource(h2Datasource);
        DepositRepository depositRepository = DepositRepository.getInstance();
        depositRepository.setH2Datasource(h2Datasource);
        CounterpartyRepository counterpartyRepository = CounterpartyRepository.getInstance();
        counterpartyRepository.setH2Datasource(h2Datasource);
        TransferRepository transferRepository = TransferRepository.getInstance();
        transferRepository.setH2Datasource(h2Datasource);
        ConfirmationRepository confirmationRepository = ConfirmationRepository.getInstance();
        confirmationRepository.setH2Datasource(h2Datasource);
        try {
            new FileReader(databaseFile);
        } catch (FileNotFoundException e) {
            try (Connection db = h2Datasource.setH2Connection();
            ) {
                RunScript.execute(db, reader);
            } catch (SQLException | DatabaseException throwables) {
                throwables.printStackTrace();
            }
        }
        AccountService accountService = new AccountService(accountRepository);
        CardService cardService = new CardService(cardRepository, billRepository);
        BillService billService = new BillService(billRepository);
        DepositService depositService = new DepositService(depositRepository, billRepository);
        CounterpartyService counterpartyService = new CounterpartyService(counterpartyRepository, accountRepository);
        TransferService transferService = new TransferService(transferRepository, billRepository, counterpartyRepository);
        ConfirmationService confirmationService = new ConfirmationService(confirmationRepository);
        CardsController cardsController = new CardsController(cardService);
        BillsController billsController = new BillsController(billService, confirmationService);
        DepositController depositController = new DepositController(depositService);
        CounterpartyController counterpartyController = new CounterpartyController(counterpartyService);
        TransferController transferController = new TransferController(transferService);
        ConfirmationController confirmationController = new ConfirmationController(confirmationService, accountService,
                billService);
        AccountController accountController = new AccountController(confirmationService);
        ControllerHandler controllerHandler = new ControllerHandler(cardsController, billsController,
                depositController, counterpartyController, transferController, confirmationController, accountController);
        try {
            int serverPort = 8000;
            HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);
            controllerHandler.startController(server);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
