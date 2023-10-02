package org.glebchanskiy.doughdelight.controllers;

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

public class ShareFilesController implements Controller {
    private static final Logger log = LoggerFactory.getLogger(ShareFilesController.class);

    @Override
    public String getMapping() {
        return "/";
    }

    @Override
    public Response get(Request request) {


        try {
            File file = new File(ClassLoader.getSystemClassLoader().getResource("static").getPath() +
                    (request.getUrl().equals("/") ? "" : request.getUrl())
            );
            String content = null;
            byte[] binary = null;
            String contentType;
            String fileName = file.getName();

            if (file.isDirectory()) {
                contentType = "text/html";
                content = generatePageWithFilesList(file, request.getUrl());
            } else {
                if (fileName.endsWith("html")) {
                    content = readFile(file);
                    contentType = "text/html";
                } else if (fileName.endsWith("css")) {
                    content = readFile(file);
                    contentType = "text/css";
                } else if (fileName.endsWith("json")) {
                    content = readFile(file);
                    contentType = "application/json";
                } else if (fileName.endsWith("jpg")) {
                    binary = java.nio.file.Files.readAllBytes(file.toPath());
                    contentType = "image/jpeg";
                }    else if (fileName.endsWith("mp4")) {
                    binary =  java.nio.file.Files.readAllBytes(file.toPath());
                    contentType = "video/mp4";
                } else {
                    content = readFile(file);
                    contentType = "text/plain";
                }
            }

            ResponseHeaders responseHeaders = new ResponseHeaders();
            responseHeaders.put("Content-Type", contentType);
            responseHeaders.put("Content-Length", Integer.toString(binary == null ? content.getBytes().length : binary.length));
            responseHeaders.put("charset", "utf-8");

            return Response.builder()
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
    }

    private String generatePageWithFilesList(File file, String link) {
        StringBuilder fileList = new StringBuilder();

        for (var f : Objects.requireNonNull(file.listFiles())) {
            appendLink(fileList, f.getName(), link);
        }
        return String.format("<html><body>%s<body/><html/>", fileList);
    }

    private void appendLink(StringBuilder sb, String fileName, String link) {
        sb.append("<a href='")
                .append(link.equals("/") ? link : link + "/")
                .append(fileName)
                .append("'>")
                .append(link.equals("/") ? link : link + "/")
                .append(fileName)
                .append("<a/>").append("<br>");
    }

    private String readFile(File file) throws IOException {
        return com.google.common.io.Files.asCharSource(file, StandardCharsets.UTF_8).read();
    }

    @Override
    public Response post(Request request) {
        return null;
    }
}
