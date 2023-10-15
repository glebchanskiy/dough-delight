package org.glebchanskiy.kek.router.controllers.impl;

import org.glebchanskiy.kek.Configuration;
import org.glebchanskiy.kek.router.controllers.Controller;
import org.glebchanskiy.kek.utils.Request;
import org.glebchanskiy.kek.utils.Response;
import org.glebchanskiy.kek.utils.ResponseHeaders;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class ShareFilesController extends Controller {

    private final Configuration configuration;

    public ShareFilesController(String rout, Configuration configuration) {
        super(rout);
        this.configuration = configuration;
    }

    @Override
    public Response getMapping(Request request) {
        try {
            String url = URLDecoder.decode(request.getUrl(), StandardCharsets.UTF_8);
            String path = configuration.getLocation() + (url.equals("/") ? "" : url);

            File file = new File(path);

            String content = null;
            byte[] binary = null;
            String contentType;
            String fileName = file.getName();

            if (file.isDirectory()) {
                contentType = "text/html";
                content = generatePageWithFilesList(file, url);
            } else {
                if (fileName.endsWith("html")) {
                    content = readFile(file);
                    contentType = "text/html";
                } else if (fileName.endsWith("css")) {
                    content = readFile(file);
                    contentType = "text/css";
                } else if (fileName.endsWith("svg") || fileName.endsWith("xml")) {
                    content = readFile(file);
                    contentType = "image/svg+xml";
                } else if (fileName.endsWith("js")) {
                    content = readFile(file);
                    contentType = "text/javascript; charset=utf-8";
                } else if (fileName.endsWith("json")) {
                    content = readFile(file);
                    contentType = "application/json";
                } else if (fileName.endsWith("jpg") || fileName.endsWith("jpeg")) {
                    binary = java.nio.file.Files.readAllBytes(file.toPath());
                    contentType = "image/jpeg";
                } else if (fileName.endsWith("png")) {
                    binary =  java.nio.file.Files.readAllBytes(file.toPath());
                    contentType = "image/png";
                } else if (fileName.endsWith("webp")) {
                    binary =  java.nio.file.Files.readAllBytes(file.toPath());
                    contentType = "image/webp";
                } else if (fileName.endsWith("gif")) {
                    binary = java.nio.file.Files.readAllBytes(file.toPath());
                    contentType = "image/gif";
                } else if (fileName.endsWith("mp4")) {
                    binary =  java.nio.file.Files.readAllBytes(file.toPath());
                    contentType = "video/mp4";
                } else {
                    content = readFile(file);
                    contentType = "text/plain";
                }
            }

            ResponseHeaders responseHeaders = new ResponseHeaders();
            responseHeaders.put("Content-Type", contentType);
            responseHeaders.put("Content-Length", Integer.toString(binary != null ?  binary.length  : content.getBytes().length));
            responseHeaders.put("charset", "utf-8");

            return Response.builder()
                    .status(200)
                    .textStatus("OK")
                    .headers(responseHeaders)
                    .body(content)
                    .binary(binary)
                    .build();

        } catch (IOException e) {
            System.out.println(e.getMessage());
            return Response.builder()
                    .headers(new ResponseHeaders())
                    .status(404)
                    .textStatus("Bad Request")
                    .build();
        }
    }

    @Override
    public Response postMapping(Request request) {
        return null;
    }

    @Override
    public Response optionsMapping(Request request) {
        return Response.builder()
                .status(200)
                .textStatus("OK")
                .build();
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
}
