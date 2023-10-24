package org.glebchanskiy.kek.router.controllers;

import com.google.common.io.Files;
import org.glebchanskiy.kek.Configuration;
import org.glebchanskiy.kek.templater.Model;
import org.glebchanskiy.kek.templater.Templater;
import org.glebchanskiy.kek.utils.Request;
import org.glebchanskiy.kek.utils.Response;
import org.glebchanskiy.kek.utils.ResponseHeaders;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public abstract class TemplateController extends Controller {

    protected final Model model;
    private final Templater templater;
    private final Configuration configuration = Configuration.getInstance();
    protected TemplateController(String route) {
        super(route);
        model = new Model();
        templater = new Templater(model);
    }

    protected Response template(String page) {
        InputStream pageStream = getPageInputStream(configuration.getLocation() + page);

        if (page == null || pageStream == null) {
            return Response.NOT_FOUNDED;
        }

        String content;

        try {
            content = new String(pageStream.readAllBytes(), StandardCharsets.UTF_8);
            content = templater.template(content);
        } catch (IOException ignored) { return null;}

        ResponseHeaders responseHeaders = new ResponseHeaders();
        responseHeaders.put("Content-Type", "text/html");
        responseHeaders.put("Content-Length", String.valueOf(content.getBytes(StandardCharsets.UTF_8).length));
        return Response.builder()
                .status(200)
                .textStatus("OK")
                .headers(responseHeaders)
                .body(content)
                .build();
    }

    private InputStream getPageInputStream(String path) {
        InputStream pageStream = Configuration.class.getResourceAsStream(path);
        if (pageStream == null) {
            try {
                pageStream = Files.asByteSource(new File(path)).openStream();
            } catch (IOException ignored) {}
        }
        return pageStream;
    }
}
