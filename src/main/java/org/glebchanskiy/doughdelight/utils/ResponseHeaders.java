package org.glebchanskiy.doughdelight.utils;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.stream.Collectors;


public class ResponseHeaders extends HashMap<String, String> {

    @Override
    public String toString() {
        return this.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + '\n')
                .collect(Collectors.joining());
    }
}
