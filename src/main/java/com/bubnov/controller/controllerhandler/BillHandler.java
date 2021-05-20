package com.bubnov.controller.controllerhandler;

import com.bubnov.controller.BillsController;
import com.sun.net.httpserver.HttpServer;

public class BillHandler {

    private static final String GET = "GET";
    ControllerUtils utils = new ControllerUtils();

    public void handle(HttpServer server, BillsController billsController) {

        server.createContext("/clients/bills", (exchange -> {
            String jsonOut;
            switch (exchange.getRequestMethod()) {
                case GET:
                    try {
                        String billNumber = utils.getPath(exchange);
                        jsonOut = billsController.getAmount(billNumber);
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
