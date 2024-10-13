package com.example;

import java.util.Map;

public interface DispatcherHandler{


    String handlePost(Map<String, String> params);

    String handleGet(Map<String, String> params);

}
