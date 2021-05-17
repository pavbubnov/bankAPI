package com.bubnov.controller;

import com.bubnov.service.AccountService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    public void startController() throws IOException {

        int serverPort = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);
        server.createContext("/clients", (exchange -> {
            switch (exchange.getRequestMethod()) {
//                case "POST":
//                    System.out.println("Привет");
//                    exchange.getRequestBody();
//                    account = objectMapper.readValue(exchange.getRequestBody(), Account.class);
//                    repository.postCity(account);
//                    String jsonOut = objectMapper.writeValueAsString(account);
//                    exchange.sendResponseHeaders(200, jsonOut.getBytes().length);
//                    OutputStream output = exchange.getResponseBody();
//                    output.write(jsonOut.getBytes());
//                    output.flush();
//                    exchange.close();
//                    break;
                case "GET":
                    try {
                        Long billNumber = Long.valueOf(exchange.getRequestURI().toString().split("/")[2]);
                        String jsonOut = accountService.getCardsByBillNumber(billNumber);
                        sendSuccessAnswer(exchange, jsonOut);
                    } catch (Exception e) {
                        if (e.getClass() == NumberFormatException.class) {
                            sendBadAnswer(exchange, "Некорректно задан url запрос");
                        }
                        sendBadAnswer(exchange, e.getMessage());
                    }
                    break;
                default:
                    exchange.sendResponseHeaders(405, -1); // 405 Method Not Allowed
            }
            exchange.close();
        }));
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    private void sendSuccessAnswer(HttpExchange exchange, String jsonOut) throws IOException {
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
