package com.example.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class HttpUtil {

    public static Map<String, String> getRequestParameter(InputStream inputStream) throws IOException {
        Map<String, String> result = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        int ch;
        while ((ch = inputStream.read()) != -1) {
            sb.append((char) ch);
        }
        String[] pairs = sb.toString().split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length > 1) {
                result.put(keyValue[0], keyValue[1]);
            } else {
                result.put(keyValue[0], "");
            }
        }
        return result;
    }
}
