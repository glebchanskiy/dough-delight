package org.glebchanskiy.kek.utils;


import java.util.HashMap;
import java.util.stream.Collectors;

public class RequestHeaders extends HashMap<String, String> {

    @Override
    public String toString() {
        return this.entrySet().stream().map(entry -> entry.getKey() + ": " + entry.getValue() + '\n').collect(Collectors.joining());
    }
}
