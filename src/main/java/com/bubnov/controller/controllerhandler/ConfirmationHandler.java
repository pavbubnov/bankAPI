package com.bubnov.controller.controllerhandler;

import com.bubnov.controller.CardsController;
import com.bubnov.controller.ConfirmationController;
import com.sun.net.httpserver.HttpServer;

public class ConfirmationHandler {

    private static final String PUT = "PUT";
    private static final String GET = "GET";
    ControllerUtils utils = new ControllerUtils();

    public void handle(HttpServer server, ConfirmationController confirmationController) {

        server.createContext("/bank/confirmation", (exchange -> {
            String jsonOut;
            switch (exchange.getRequestMethod()) {
                case PUT:
                    try {
                        jsonOut = confirmationController.updateStatus(exchange.getRequestBody());
                        utils.sendSuccessAnswer(exchange, jsonOut);
                    } catch (Exception e) {
                        utils.catchException(e, exchange);
                    }
                    break;
                case GET:
                    try {
                        jsonOut = confirmationController.getAllUnconfirm();
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
