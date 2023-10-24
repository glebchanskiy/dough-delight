package org.glebchanskiy.kek.utils;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class Request {
    private String method;
    private String url;
    private RequestHeaders headers;
    private String body;

    @Override
    public String toString() {
        return method + " " + url + " HTTP/1.1\n" + (headers != null ? headers : "") + (body != null ? "\n" + body : "");
    }

    public Map<String, String> formData() {
        Map<String, String> formData = new HashMap<>();
        String utf8EncodedString = URLDecoder.decode(body, StandardCharsets.UTF_8);

        String[] parts = utf8EncodedString.trim().split("&");

        for (var part : parts) {
            String[] split = part.split("=");
            formData.put(split[0], split[1]);
        }
        return formData;
    }
}
