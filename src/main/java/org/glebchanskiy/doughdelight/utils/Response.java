package org.glebchanskiy.doughdelight.utils;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Response {
    private int status;
    private TextStatus textStatus;
    private ResponseHeaders headers;
    private String body;
    private byte[] binary;

    @Override
    public String toString() {
        return "HTTP/1.1 " + status + " " + textStatus.name() + "\n" +
                "Server: dough-delight\n" +
                (headers != null ? headers : "") +
                "\n" + (body != null ? body : "");
    }
}
