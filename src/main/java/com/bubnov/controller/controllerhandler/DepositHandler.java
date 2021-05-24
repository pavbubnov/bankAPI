package com.bubnov.controller.controllerhandler;

import com.bubnov.controller.DepositController;
import com.sun.net.httpserver.HttpServer;

public class DepositHandler {

    private static final String POST = "POST";
    ControllerUtils utils = new ControllerUtils();

    public void handle(HttpServer server, DepositController depositController) {

        server.createContext("/clients/deposits", (exchange -> {
            String jsonOut;
            switch (exchange.getRequestMethod()) {
                case POST:
                    try {
                        jsonOut = depositController.postDeposit(exchange.getRequestBody());
                        utils.sendSuccessAnswer(exchange, jsonOut);
                    } catch (Exception e) {
                        utils.catchException(e, exchange);
                    }
                    break;
                default:
                    exchange.sendResponseHeaders(405, -1);
            }
            exchange.close();
        }));
    }
}
