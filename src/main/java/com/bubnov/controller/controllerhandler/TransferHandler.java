package com.bubnov.controller.controllerhandler;

import com.bubnov.controller.CounterpartyController;
import com.bubnov.controller.TransferController;
import com.sun.net.httpserver.HttpServer;

public class TransferHandler {

    private static final String POST = "POST";
    ControllerUtils utils = new ControllerUtils();

    public void handle(HttpServer server, TransferController transferController) {

        server.createContext("/clients/transfer", (exchange -> {
            String jsonOut;
            switch (exchange.getRequestMethod()) {
                case POST:
                    try {
                        jsonOut = transferController.postTransfer(exchange.getRequestBody());
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
