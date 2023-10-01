package org.glebchanskiy.doughdelight.utils;

import lombok.Builder;
import lombok.Data;

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
}
