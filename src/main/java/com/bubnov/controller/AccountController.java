package com.bubnov.controller;

import com.bubnov.controller.dto.BillRequestDTO;
import com.bubnov.controller.dto.CardRequestDTO;
import com.bubnov.entity.Card;
import com.bubnov.exception.RequestException;
import com.bubnov.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    public void startController() throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        int serverPort = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);
        server.createContext("/clients/bills", (exchange -> {
            String jsonOut;
            switch (exchange.getRequestMethod()) {
                case "POST":
                    try {
                        CardRequestDTO requestDTO = objectMapper.readValue(exchange.getRequestBody(), CardRequestDTO.class);
                        jsonOut = accountService.createCard(requestDTO);
                        sendSuccessAnswer(exchange, jsonOut);
                    } catch (Exception e) {
                        catchExeption(e, exchange);
                    }
                    break;
                case "GET":
                    try {
                        BillRequestDTO billNumber = objectMapper.readValue(exchange.getRequestBody(), BillRequestDTO.class);
                        jsonOut = accountService.getCardsByBillNumber(billNumber);
                        sendSuccessAnswer(exchange, jsonOut);
                    } catch (Exception e) {
                        catchExeption(e, exchange);
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

    private void catchExeption(Exception e, HttpExchange exchange) throws IOException {
        if (e.getClass() == UnrecognizedPropertyException.class) {
            sendBadAnswer(exchange, "Некорректное тело запроса");
        }
        sendBadAnswer(exchange, e.getMessage());
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
