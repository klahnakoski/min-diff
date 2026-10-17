package com.mindiff.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindiff.model.FilterRequest;
import com.mindiff.model.FilterResponse;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class FilterServer {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DiffNoiseFilter diffNoiseFilter = new DiffNoiseFilter();
    private final RefactoringMinerService refactoringMinerService = new RefactoringMinerService();

    public void start(int port) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/api/health", exchange -> respond(exchange, 200,
                "{\"status\":\"ok\",\"engine\":\"" + refactoringMinerService.engineName() + "\"}"));
            server.createContext("/api/filter", this::filter);
            server.start();
            System.out.println("min-diff backend listening on http://localhost:" + port);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to start backend server", e);
        }
    }

    private void filter(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            respond(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
            return;
        }

        FilterRequest request = objectMapper.readValue(exchange.getRequestBody(), FilterRequest.class);
        String filteredDiff = diffNoiseFilter.removeSelectedRanges(request.diff(), request.refactorings());
        FilterResponse response = new FilterResponse(filteredDiff, refactoringMinerService.engineName());
        respond(exchange, 200, objectMapper.writeValueAsString(response));
    }

    private void respond(HttpExchange exchange, int status, String body) throws IOException {
        byte[] payload = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, payload.length);
        exchange.getResponseBody().write(payload);
        exchange.close();
    }
}
