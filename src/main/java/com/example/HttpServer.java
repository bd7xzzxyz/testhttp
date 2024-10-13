package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {
    private static final int PORT = 8080;

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

                String line;
                StringBuilder requestBuilder = new StringBuilder();
                while (!(line = in.readLine()).isEmpty()) {
                    requestBuilder.append(line).append("\r\n");
                }

                String request = requestBuilder.toString();
                String[] requestLines = request.split("\r\n");
                String[] requestLine = requestLines[0].split(" ");
                String method = requestLine[0];
                String path = requestLine[1];

                if (method.equals("POST") && path.equals("/register")) {
                    handleRegister(in, out);
                } else if (method.equals("POST") && path.equals("/login")) {
                    handleLogin(in, out);
                } else {
                    sendResponse(out, 404, "Not Found", "The requested resource was not found.");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
