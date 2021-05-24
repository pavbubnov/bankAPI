package com.bubnov.controller.controllerhandler;

import com.bubnov.controller.CounterpartyController;
import com.sun.net.httpserver.HttpServer;

public class CounterPartyHandler {

    private static final String POST = "POST";
    private static final String GET = "GET";
    ControllerUtils utils = new ControllerUtils();

    public void handle(HttpServer server, CounterpartyController counterpartyController) {

        server.createContext("/clients/counterparty", (exchange -> {
            String jsonOut;
            switch (exchange.getRequestMethod()) {
                case POST:
                    try {
                        jsonOut = counterpartyController.postCounterparty(exchange.getRequestBody());
                        utils.sendSuccessAnswer(exchange, jsonOut);
                    } catch (Exception e) {
                        utils.catchException(e, exchange);
                    }
                    break;
                case GET:
                    try {
                        int id  = Integer.valueOf(utils.getPath(exchange));
                        jsonOut = counterpartyController.getCounterparties(id);
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
