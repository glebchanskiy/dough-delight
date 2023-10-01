package org.glebchanskiy.doughdelight.controllers;

import com.google.common.io.Files;
import org.glebchanskiy.doughdelight.Controller;
import org.glebchanskiy.doughdelight.utils.Request;
import org.glebchanskiy.doughdelight.utils.Response;
import org.glebchanskiy.doughdelight.utils.ResponseHeaders;
import org.glebchanskiy.doughdelight.utils.TextStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class TestController implements Controller {
    private static final Logger log = LoggerFactory.getLogger(TestController.class);

    @Override
    public String getMapping() {
        return "/";
    }

    @Override
    public Response get(Request request) {
        Response response;
        log.info("/GET");
        File file = new File(
                "/Users/glebchanskiy/subjects/aipos/dough-delight/src/main/java/org/glebchanskiy/resurces" +
                        (request.getUrl().equals("/") ? "" : request.getUrl())
                        );

        try {
            String content = null;
            byte[] binary = null;
            String contentType;
            String fileName = file.getName();
            if (file.isDirectory()) {
                contentType = "text/html";
                StringBuilder list = new StringBuilder();
                for (var i : Objects.requireNonNull(file.listFiles())) {
                    list.append("<a href='")
                            .append(request.getUrl().equals("/") ? request.getUrl() : request.getUrl() + "/")
                            .append(i.getName())
                            .append("'>")
                            .append(request.getUrl().equals("/") ? request.getUrl() : request.getUrl() + "/")
                            .append(i.getName())
                            .append("<a/>").append("<br>");
                }

                content = String.format("<html><body>%s<body/><html/>", list);
            } else {

                if (fileName.endsWith("html")) {
                    content = com.google.common.io.Files.asCharSource(file, StandardCharsets.UTF_8).read();
                    contentType = "text/html";
                } else if (fileName.endsWith(".json")) {
                    content = com.google.common.io.Files.asCharSource(file, StandardCharsets.UTF_8).read();
                    contentType = "application/json";
                } else if (fileName.endsWith("jpg")) {
//                    var encoder =  Base64.getEncoder();
//                    content = encoder.encodeToString(Files.asByteSource(file).read());
                    binary =  java.nio.file.Files.readAllBytes(file.toPath());
                    contentType = "image/jpeg";
                } else {
                    content = com.google.common.io.Files.asCharSource(file, StandardCharsets.UTF_8).read();
                    contentType = "text/plain";
                }
            }

            ResponseHeaders responseHeaders = new ResponseHeaders();
            responseHeaders.put("Content-Type", contentType);
            responseHeaders.put("Content-Length", Integer.toString(binary == null ? content.getBytes().length : binary.length));
            responseHeaders.put("charset", "utf-8");

            response = Response.builder()
                    .status(200)
                    .textStatus(TextStatus.OK)
                    .headers(responseHeaders)
                    .body(content)
                    .binary(binary)
                    .build();

        } catch (IOException e) {
            log.info("read ex");
            return Response.builder()
                    .status(404)
                    .textStatus(TextStatus.BAD_REQUEST)
                    .build();
        }

        return response;
    }

    @Override
    public Response post(Request request) {
        return null;
    }
}
