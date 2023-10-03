package org.glebchanskiy.doughdelight.utils;

import java.util.HashMap;
import java.util.stream.Collectors;


public class ResponseHeaders extends HashMap<String, String> {

    public ResponseHeaders() {
        this.put("Allow", "GET, POST, OPTIONS");
        this.put("Access-Control-Allow-Origin", "localhost:8080");
        this.put("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
    }

    @Override
    public String toString() {
        return this.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + '\n')
                .collect(Collectors.joining());
    }
}
