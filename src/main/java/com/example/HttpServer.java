package com.example;

import com.example.controller.LoginController;
import com.example.controller.RegisterController;
import com.example.util.HttpUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpServer {
    private static final int PORT = 8080;
    private static final Map<String, DispatcherHandler> HANDLERS = new HashMap<>();

    static {
        HANDLERS.put("/register", new RegisterController());
        HANDLERS.put("/login", new LoginController());
    }

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
                String request = HttpUtil.parseRequest(in);
                if (request.trim().isEmpty()) {
                    HttpUtil.sendResponse(out, 400, "Invalid request", "The request invalid.");
                    return;
                }
                String[] requestLines = request.split("\r\n");
                String[] requestLine = requestLines[0].split(" ");
                String method = requestLine[0];
                String path = requestLine[1];
                String body = HttpUtil.readBody(in);
                Map<String, String> params = HttpUtil.parseParams(body);

                try {
                    if (method.equals("POST")) {
                        String response = HANDLERS.get(path).handlePost(params);
                        HttpUtil.sendResponse(out, 200, "ok", response);
                    } else if (method.equals("GET")) {
                        String response = HANDLERS.get(path).handleGet(params);
                        HttpUtil.sendResponse(out, 200, "ok", response);
                    } else {
                        HttpUtil.sendResponse(out, 404, "Not Found", "The requested resource was not found.");
                    }
                } catch (Exception e) {
                    HttpUtil.sendResponse(out, 500, "Internal Error", "Server Internal Error,Please try again.");
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
