package com.bubnov;

import com.bubnov.entity.Account;
import com.bubnov.repository.Repository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;

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
                int serverPort = 8000;
                HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);

                Repository repository = Repository.getInstance();
                server.createContext("/post", (exchange -> {
                    Account account = null;
                    Account account1 = new Account(1, "not Pavel");

                    if ("POST".equals(exchange.getRequestMethod())) {

                        try {
                            repository.getH2Connection();
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }

                        System.out.println("Привет");
                        exchange.getRequestBody();
                        account = objectMapper.readValue(exchange.getRequestBody(), Account.class);
                        repository.postCity(account1);
                        repository.postCity(account);
                        System.out.println("this print never appear");

                        repository.closeConnection();
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

        private void start() {

        }
}
