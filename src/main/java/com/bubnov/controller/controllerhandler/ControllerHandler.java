package com.bubnov.controller.controllerhandler;

import com.bubnov.controller.BillsController;
import com.bubnov.controller.CardsController;
import com.bubnov.controller.DepositController;
import com.bubnov.exception.RequestException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;

public class ControllerHandler {

    private CardsController cardsController;
    private BillsController billsController;
    private DepositController depositController;

    public ControllerHandler(CardsController cardsController, BillsController billsController,
                             DepositController depositController) {
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

        server.setExecutor(null);
        server.start();
    }

}
