package org.glebchanskiy.kek.utils;

import java.util.HashMap;
import java.util.stream.Collectors;


public class ResponseHeaders extends HashMap<String, String> {

    public ResponseHeaders() {
        this.put("Allow", "GET, OPTIONS");
        this.put("Access-Control-Allow-Origin", "*");
        this.put("Access-Control-Allow-Methods", "GET, OPTIONS");
    }

    @Override
    public String toString() {
        return this.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + '\n')
                .collect(Collectors.joining());
    }
}
