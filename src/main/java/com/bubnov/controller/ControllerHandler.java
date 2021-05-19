package com.bubnov.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class ControllerHandler {

    private CardsController cardsController;
    private BillsController billsController;
    private DepositController depositController;
    private static final String POST = "POST";
    private static final String GET = "GET";

    public ControllerHandler(CardsController cardsController, BillsController billsController,
                             DepositController depositController) {
        this.cardsController = cardsController;
        this.billsController = billsController;
        this.depositController = depositController;
    }

    public void startController() throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        int serverPort = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);
        server.createContext("/clients/cards", (exchange -> {
            String jsonOut;
            switch (exchange.getRequestMethod()) {
                case POST:
                    try {
                        jsonOut = cardsController.postCard(exchange);
                        sendSuccessAnswer(exchange, jsonOut);
                    } catch (Exception e) {
                        catchException(e, exchange);
                    }
                    break;
                case GET:
                    try {
                        jsonOut = cardsController.getCards(exchange);
                        sendSuccessAnswer(exchange, jsonOut);
                    } catch (Exception e) {
                        catchException(e, exchange);
                    }
                    break;
                default:
                    exchange.sendResponseHeaders(405, -1);
            }
            exchange.close();
        }));

        server.createContext("/clients/bills", (exchange -> {
            String jsonOut;
            switch (exchange.getRequestMethod()) {
                case GET:
                    try {
                        jsonOut = billsController.getAmount(exchange);
                        sendSuccessAnswer(exchange, jsonOut);
                    } catch (Exception e) {
                        catchException(e, exchange);
                    }
                    break;
                default:
                    exchange.sendResponseHeaders(405, -1);
            }
            exchange.close();
        }));

        server.createContext("/clients/deposits", (exchange -> {
            String jsonOut;
            switch (exchange.getRequestMethod()) {
                case POST:
                    try {
                        jsonOut = depositController.postDeposit(exchange);
                        sendSuccessAnswer(exchange, jsonOut);
                    } catch (Exception e) {
                        catchException(e, exchange);
                    }
                    break;
                default:
                    exchange.sendResponseHeaders(405, -1);
            }
            exchange.close();
        }));

        server.setExecutor(null);
        server.start();
    }

    private void catchException(Exception e, HttpExchange exchange) throws IOException {
        if (e.getClass() == UnrecognizedPropertyException.class) {
            sendBadAnswer(exchange, "Некорректное тело запроса");
        }
        sendBadAnswer(exchange, e.getMessage());
    }

    private void sendSuccessAnswer(HttpExchange exchange, String jsonOut) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, jsonOut.getBytes().length);
        OutputStream output = exchange.getResponseBody();
        output.write(jsonOut.getBytes());
        output.flush();
        exchange.close();
    }

    private void sendBadAnswer(HttpExchange exchange, String jsonOut) throws IOException {
        exchange.sendResponseHeaders(400, jsonOut.getBytes().length);
        OutputStream output = exchange.getResponseBody();
        output.write(jsonOut.getBytes());
        output.flush();
        exchange.close();
    }

}
