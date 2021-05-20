package com.bubnov.controller.controllerhandler;

import com.bubnov.exception.RequestException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;

public class ControllerUtils {

    void catchException(Exception e, HttpExchange exchange) throws IOException {
        if (e.getClass() == UnrecognizedPropertyException.class ||
                e.getClass() == ArrayIndexOutOfBoundsException.class) {
            sendBadAnswer(exchange, "Некорректный запрос", 400);
        } else if (e.getClass() == RequestException.class) {
            sendBadAnswer(exchange, e.getMessage(), 400);
        } else {
            sendBadAnswer(exchange, "Ошибка сервера, проверьте тело запроса", 500);
        }

    }

    void sendSuccessAnswer(HttpExchange exchange, String jsonOut) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(200, jsonOut.getBytes().length);
        OutputStream output = exchange.getResponseBody();
        output.write(jsonOut.getBytes());
        output.flush();
        exchange.close();
    }

    void sendBadAnswer(HttpExchange exchange, String jsonOut, int code) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/text");
        exchange.sendResponseHeaders(code, jsonOut.getBytes().length);
        OutputStream output = exchange.getResponseBody();
        output.write(jsonOut.getBytes());
        output.flush();
        exchange.close();
    }

    String getPath(HttpExchange exchange) throws IOException, RequestException {
        String path = exchange.getRequestURI().getPath().split("/")[3];
        if (!path.matches("\\d+")) {
            sendBadAnswer(exchange, "Некорректно задан счет", 400);
            throw new RequestException("Некорректно задан счет");
        }
        return path;
    }

}
