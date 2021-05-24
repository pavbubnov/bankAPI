package com.bubnov.controller.controllerhandler;

import com.bubnov.controller.AccountController;
import com.sun.net.httpserver.HttpServer;

public class AccountHandler {

    private static final String POST = "POST";
    ControllerUtils utils = new ControllerUtils();

    public void handle(HttpServer server, AccountController accountController) {

        server.createContext("/clients/accounts", (exchange -> {
            String jsonOut;
            switch (exchange.getRequestMethod()) {
                case POST:
                    try {
                        jsonOut = accountController.postCreateAccountConfirmation(exchange.getRequestBody());
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
