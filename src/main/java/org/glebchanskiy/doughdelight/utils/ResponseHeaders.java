package org.glebchanskiy.doughdelight.utils;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.stream.Collectors;


public class ResponseHeaders extends HashMap<String, String> {

    public ResponseHeaders() {
        this.put("Access-Control-Allow-Origin", "https://google.com");
        this.put("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
    }

    @Override
    public String toString() {
        return this.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + '\n')
                .collect(Collectors.joining());
    }
}
