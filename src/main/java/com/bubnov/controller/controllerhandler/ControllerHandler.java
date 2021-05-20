package com.bubnov.controller.controllerhandler;

import com.bubnov.controller.*;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;

public class ControllerHandler {

    private CardsController cardsController;
    private BillsController billsController;
    private DepositController depositController;
    private CounterpartyController counterpartyController;
    private TransferController transferController;

    public ControllerHandler(CardsController cardsController, BillsController billsController,
                             DepositController depositController, CounterpartyController counterpartyController,
                             TransferController transferController) {
        this.cardsController = cardsController;
        this.billsController = billsController;
        this.depositController = depositController;
        this.counterpartyController = counterpartyController;
        this.transferController = transferController;
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

        TransferHandler transferHandler = new TransferHandler();
        transferHandler.handle(server, transferController);

        server.setExecutor(null);
        server.start();

    }

}
