package com.bubnov;

import com.bubnov.entity.Account;
import com.bubnov.entity.Card;
import com.bubnov.exception.RequestException;
import com.bubnov.repository.Query;
import com.bubnov.repository.Repository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

public class Application {

    //public static void main(String[] args) throws IOException {
//            int serverPort = 8000;
//            HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);
//            server.createContext("/api/hello", (exchange -> {
//                String respText = "Hello!";
//                exchange.sendResponseHeaders(200, respText.getBytes().length);
//                OutputStream output = exchange.getResponseBody();
//                output.write(respText.getBytes());
//                output.flush();
//                exchange.close();
//            }));
//            server.setExecutor(null); // creates a default executor
//            server.start();

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {

        Repository repository = Repository.getInstance();
        repository.getH2Connection();
        repository.createStart(Query.startQueryList());
       // repository.closeConnection();


        int serverPort = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);


        server.createContext("/clients", (exchange -> {
            Account account = null;

            if ("POST".equals(exchange.getRequestMethod())) {
                repository.getH2Connection();
                System.out.println("Привет");
                exchange.getRequestBody();
                account = objectMapper.readValue(exchange.getRequestBody(), Account.class);
                repository.postCity(account);
                String jsonOut = objectMapper.writeValueAsString(account);
                exchange.sendResponseHeaders(200, jsonOut.getBytes().length);
                OutputStream output = exchange.getResponseBody();
                output.write(jsonOut.getBytes());
                output.flush();
                exchange.close();
                repository.closeConnection();
            } else if ("GET".equals(exchange.getRequestMethod())) {
                //repository.getH2Connection();
                try {
                    int billId = Integer.valueOf(exchange.getRequestURI().toString().split("/")[2]);
                    List<Card> cards = repository.getAllCardsByBillId(billId);
                    String jsonOut = objectMapper.writeValueAsString(cards);
                    exchange.sendResponseHeaders(200, jsonOut.getBytes().length);
                    OutputStream output = exchange.getResponseBody();
                    output.write(jsonOut.getBytes());
                    output.flush();
                    exchange.close();
                   // repository.closeConnection();
                } catch (NumberFormatException e) {
                    throw new RequestException("Некорректно задан url запрос");
                }
            } else {
                exchange.sendResponseHeaders(405, -1); // 405 Method Not Allowed
            }

            exchange.close();
        }));

        server.setExecutor(null); // creates a default executor
        server.start();


        //repository.createStart();
        //Account account = new Account(1,"Pavel");
        //repository.postCity(account);


    }

}
