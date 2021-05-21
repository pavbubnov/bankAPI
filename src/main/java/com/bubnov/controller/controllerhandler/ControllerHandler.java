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
    private ConfirmationController confirmationController;
    private AccountController accountController;

    public ControllerHandler(CardsController cardsController, BillsController billsController,
                             DepositController depositController, CounterpartyController counterpartyController,
                             TransferController transferController, ConfirmationController confirmationController,
                             AccountController accountController) {
        this.cardsController = cardsController;
        this.billsController = billsController;
        this.depositController = depositController;
        this.counterpartyController = counterpartyController;
        this.transferController = transferController;
        this.confirmationController = confirmationController;
        this.accountController = accountController;
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

        ConfirmationHandler confirmationHandler = new ConfirmationHandler();
        confirmationHandler.handle(server, confirmationController);

        AccountHandler accountHandler = new AccountHandler();
        accountHandler.handle(server, accountController);

        server.setExecutor(null);
        server.start();

    }

}
