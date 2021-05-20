package com.bubnov.controller;

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
    private static final String POST = "POST";
    private static final String GET = "GET";

    public ControllerHandler(CardsController cardsController, BillsController billsController,
                             DepositController depositController) {
        this.cardsController = cardsController;
        this.billsController = billsController;
        this.depositController = depositController;
    }

    public void startController(HttpServer server) throws IOException {

        server.createContext("/clients/cards", (exchange -> {
            String jsonOut;
            switch (exchange.getRequestMethod()) {
                case POST:
                    try {
                        jsonOut = cardsController.postCard(exchange.getRequestBody());
                        sendSuccessAnswer(exchange, jsonOut);
                    } catch (Exception e) {
                        catchException(e, exchange);
                    }
                    break;
                case GET:
                    try {
                        String billNumber = getPath(exchange);
                        jsonOut = cardsController.getCards(billNumber);
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
                        String billNumber = getPath(exchange);
                        jsonOut = billsController.getAmount(billNumber);
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
                        jsonOut = depositController.postDeposit(exchange.getRequestBody());
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
        if (e.getClass() == UnrecognizedPropertyException.class ||
                e.getClass() == ArrayIndexOutOfBoundsException.class) {
            sendBadAnswer(exchange, "Некорректный запрос", 400);
        } else if (e.getClass() == RequestException.class) {
            sendBadAnswer(exchange, e.getMessage(), 400);
        } else {
            sendBadAnswer(exchange, "Ошибка сервера, проверьте тело запроса", 500);
        }

    }

    private void sendSuccessAnswer(HttpExchange exchange, String jsonOut) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(200, jsonOut.getBytes().length);
        OutputStream output = exchange.getResponseBody();
        output.write(jsonOut.getBytes());
        output.flush();
        exchange.close();
    }

    private void sendBadAnswer(HttpExchange exchange, String jsonOut, int code) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/text");
        exchange.sendResponseHeaders(code, jsonOut.getBytes().length);
        OutputStream output = exchange.getResponseBody();
        output.write(jsonOut.getBytes());
        output.flush();
        exchange.close();
    }

    private String getPath(HttpExchange exchange) throws IOException, RequestException {
        String path = exchange.getRequestURI().getPath().split("/")[3];
        if (!path.matches("\\d+")) {
            sendBadAnswer(exchange, "Некорректно задан счет", 400);
            throw new RequestException("Некорректно задан счет");
        }
        return path;
    }

}
