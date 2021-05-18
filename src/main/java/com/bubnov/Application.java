package com.bubnov;

import com.bubnov.controller.BillsController;
import com.bubnov.controller.CardsController;
import com.bubnov.controller.ControllerHandler;
import com.bubnov.controller.DepositController;
import com.bubnov.exception.DatabaseException;
import com.bubnov.repository.*;
import com.bubnov.service.BillService;
import com.bubnov.service.CardService;
import com.bubnov.service.DepositService;
import org.h2.tools.RunScript;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class Application {

    public static void main(String[] args) throws IOException {
        CardRepository cardRepository = CardRepository.getInstance();
        BillRepository billRepository = BillRepository.getInstance();
        DepositRepository depositRepository = DepositRepository.getInstance();
        try {
            H2ConnectionUtils connectionUtils = new H2ConnectionUtils();
            Connection connection = connectionUtils.getH2Connection();
            cardRepository.getConnection(connection);
            billRepository.getConnection(connection);
            depositRepository.getConnection(connection);
            RunScript.execute(connection, new FileReader("/Users/a19189145/Documents/workProjects/bankAPI/src/main/resources/startDatabase.sql"));
        } catch (DatabaseException | SQLException e) {
            e.printStackTrace();
        }
        CardService cardService = new CardService(cardRepository, billRepository);
        BillService billService = new BillService(billRepository);
        DepositService depositService = new DepositService(depositRepository, billRepository);
        CardsController cardsController = new CardsController(cardService);
        BillsController billsController = new BillsController(billService);
        DepositController depositController = new DepositController(depositService);
        ControllerHandler controllerHandler = new ControllerHandler(cardsController, billsController, depositController);
        controllerHandler.startController();
    }


}
