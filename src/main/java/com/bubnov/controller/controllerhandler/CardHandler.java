package com.bubnov.controller.controllerhandler;

import com.bubnov.controller.CardsController;
import com.sun.net.httpserver.HttpServer;

public class CardHandler {

    private static final String POST = "POST";
    private static final String GET = "GET";
    ControllerUtils utils = new ControllerUtils();

    public void handle(HttpServer server, CardsController cardsController) {

        server.createContext("/clients/cards", (exchange -> {
            String jsonOut;
            switch (exchange.getRequestMethod()) {
                case POST:
                    try {
                        jsonOut = cardsController.postCard(exchange.getRequestBody());
                        utils.sendSuccessAnswer(exchange, jsonOut);
                    } catch (Exception e) {
                        utils.catchException(e, exchange);
                    }
                    break;
                case GET:
                    try {
                        String billNumber = utils.getPath(exchange);
                        jsonOut = cardsController.getCards(billNumber);
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
