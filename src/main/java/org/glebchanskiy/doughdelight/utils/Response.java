package org.glebchanskiy.doughdelight.utils;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Response {
    private int status;
    private String textStatus;
    private ResponseHeaders headers;
    private String body;
    private byte[] binary;

    @Override
    public String toString() {
        return "HTTP/1.1 " + status + " " + textStatus + "\n" +
                "Server: dough-delight\n" +
                (headers != null ? headers : new ResponseHeaders()) +
                "\n" + (body != null ? body : "");
    }

    public static final Response NOT_FOUNDED = Response.builder()
            .status(404)
            .textStatus("Not Found")
            .headers(new ResponseHeaders())
            .build();
}
