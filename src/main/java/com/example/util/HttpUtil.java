package com.example.util;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpUtil {

    public static String parseRequest(BufferedReader in) {
        try {
            String line;
            StringBuilder requestBuilder = new StringBuilder();
            while (!(line = in.readLine()).isEmpty()) {
                requestBuilder.append(line).append("\r\n");
            }
            return requestBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String readBody(BufferedReader in) {
        try {
            StringBuilder bodyBuilder = new StringBuilder();
            while (in.ready()) {
                bodyBuilder.append((char) in.read());
            }
            return bodyBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static Map<String, String> parseParams(String body) {
        if (body.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, String> params = new HashMap<>();
        String[] pairs = body.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                try {
                    params.put(URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8.name()),
                            URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8.name()));
                } catch (UnsupportedEncodingException e) {
                    //ignored
                }
            }
        }
        return params;
    }

    public static void sendResponse(PrintWriter out, int statusCode, String statusText, String body) {
        out.printf("HTTP/1.1 %d %s\r\n", statusCode, statusText);
        out.println("Content-Type: text/plain; charset=UTF-8");
        out.println("Content-Length: " + body.length());
        out.println();
        out.println(body);
    }
}
