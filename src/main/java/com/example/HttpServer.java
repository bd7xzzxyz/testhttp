package com.example;

import com.example.controller.LoginController;
import com.example.controller.RegisterController;
import com.example.exception.BizException;
import com.example.util.HttpUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * http服务启动类
 */
public class HttpServer {
    private static final int PORT = 8080; //监听8080端口
    private static final Map<String, DispatcherHandler> HANDLERS = new HashMap<>(); //保存了URI对应的业务处理类

    static {
        HANDLERS.put("/register", new RegisterController()); // 注册/register 到 RegisterController
        HANDLERS.put("/login", new LoginController()); // 注册/register 到 LoginController
    }

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) { //启动serverSocket
            System.out.println("Server started on port " + PORT);

            //BIO+多线程方式处理网络请求
            while (true) {
                Socket clientSocket = serverSocket.accept(); //接受客户端链接
                new Thread(new ClientHandler(clientSocket)).start(); //启动线程处理链接
            }
        } catch (Exception e) {
            System.err.println("init failed!");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * 客户端线程处理器
     */
    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
                String request = HttpUtil.parseRequest(in); //从流里读取http请求
                if (request.trim().isEmpty()) { //请求没有任何报文，返回参数错误 400请求参数错误
                    HttpUtil.sendResponse(out, 400, "Invalid request", "The request invalid.");
                    return;
                }
                //解析http协议
                String[] requestLines = request.split("\r\n");
                String[] requestLine = requestLines[0].split(" ");
                String method = requestLine[0]; //请求头部的method 即GET、POST等
                String path = requestLine[1]; //请求头部的URI
                String body = HttpUtil.readBody(in); //读取请求体
                Map<String, String> params = HttpUtil.parseParams(body); //解析http请求参数

                try {
                    //根据注册的URI路由到各个controller
                    if (method.equals("POST")) {
                        String response = HANDLERS.get(path).handlePost(params); //执行POST请求
                        HttpUtil.sendResponse(out, 200, "ok", response); //返回结果 状态码为200
                    } else if (method.equals("GET")) {
                        String response = HANDLERS.get(path).handleGet(params); //执行GET请求
                        HttpUtil.sendResponse(out, 200, "ok", response);
                    } else { //没有找到URI可路由的controller，返回不存在 状态为404
                        HttpUtil.sendResponse(out, 404, "Not Found", "The requested resource was not found.");
                    }
                } catch (BizException e) { //已知错误
                    HttpUtil.sendResponse(out, e.getErrorCode(), "Internal Error", e.getMessage());
                } catch (Exception e) { //未知的服务器内部错误 状态码为500
                    HttpUtil.sendResponse(out, 500, "Internal Error", "Server Internal Error,Please try again.");
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
