package com.bubnov.controller.controllerhandler;

import com.bubnov.controller.BillsController;
import com.bubnov.controller.CardsController;
import com.bubnov.controller.CounterpartyController;
import com.bubnov.controller.DepositController;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;

public class ControllerHandler {

    private CardsController cardsController;
    private BillsController billsController;
    private DepositController depositController;
    private CounterpartyController counterpartyController;

    public ControllerHandler(CardsController cardsController, BillsController billsController,
                             DepositController depositController, CounterpartyController counterpartyController) {
        this.cardsController = cardsController;
        this.billsController = billsController;
        this.depositController = depositController;
        this.counterpartyController = counterpartyController;
    }

    public ControllerHandler(CardsController cardsController, BillsController billsController, DepositController depositController) {
        this.cardsController = cardsController;
        this.billsController = billsController;
        this.depositController = depositController;
    }

    public void startController(HttpServer server) throws IOException {

        CardHandler cardHandler = new CardHandler();
        cardHandler.handle(server, cardsController);

        BillHandler billHandler = new BillHandler();
        billHandler.handle(server, billsController);

        DepositHandler depositHandler = new DepositHandler();
        depositHandler.handle(server, depositController);

        CounterPartyHandler counterPartyHandler = new CounterPartyHandler();
        counterPartyHandler.handle(server, counterpartyController);

        server.setExecutor(null);
        server.start();

    }

}
