package org.glebchanskiy.kek.router;

import com.google.common.io.Files;
import org.glebchanskiy.kek.Configuration;
import org.glebchanskiy.kek.router.controllers.AbstractController;
import org.glebchanskiy.kek.router.controllers.Controller;
import org.glebchanskiy.kek.router.controllers.TemplateController;
import org.glebchanskiy.kek.router.filters.Filter;
import org.glebchanskiy.kek.router.filters.FilterRuntimeException;
import org.glebchanskiy.kek.templater.Model;
import org.glebchanskiy.kek.templater.Templater;
import org.glebchanskiy.kek.utils.Request;
import org.glebchanskiy.kek.utils.Response;
import org.glebchanskiy.kek.utils.ResponseHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FilterRouter {
    private static final Logger log = LoggerFactory.getLogger("ServerDetailed");
    private static final String SEP = "------------------------------------------------";

    private Filter filter;
    private Templater templater = new Templater(new Model());
    private final List<AbstractController> controllers = new ArrayList<>();
    private final Configuration configuration;

    public FilterRouter(Configuration config) {
        this.configuration = config;
    }

    public void addFilter(Filter filter) {
        if (this.filter != null)
            this.filter.addNext(filter);
        else this.filter = filter;
    }
    public void addController(AbstractController controller) {
        this.controllers.add(controller);

        this.controllers.sort(Comparator.comparingInt(o -> -o.getRoute().length()));
    }

    private Response dispatch(AbstractController controller, Request request) {
        if (controller instanceof  Controller) {
            return  dispatchController((Controller) controller, request);
        } else if (controller instanceof TemplateController) {
            return dispatchTemplateController((TemplateController) controller, request);
        }

        return null;
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

    private Response dispatchTemplateController(TemplateController controller, Request request) {
        log.info("dispatchTemplateController");
        String page;
        if  (request.getMethod().equals("GET"))
            page = controller.getMapping(templater.getModel(), request);
        else if (request.getMethod().equals("POST")) {
            page = controller.postMapping(templater.getModel(), request);
        } else {
            return null;
        }

        String content = null;

        InputStream pageStream = getPageInputStream(configuration.getLocation() + page);

        if (page == null || pageStream == null) {
            return null;
        }

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

    private Response dispatchController(Controller controller, Request request) {
        log.info("dispatchController");
        switch (request.getMethod()) {
            case "GET" -> {return controller.getMapping(request);}
            case "POST" ->  {return controller.postMapping(request);}
            case "OPTIONS" -> {return controller.optionsMapping(request);}
            default ->
            {return Response.builder()
                    .status(405)
                    .textStatus("Method Not Allowed")
                    .headers(new ResponseHeaders())
                    .build();}
        }
    }

    public Response process(Request request) {
        log.info("\n\nRequest:\n{}\n{}{}\n",SEP, request, SEP);
        try {
            Request pureRequest = this.filter.filter(request);

            Response response;

            for (AbstractController controller : this.controllers) {
                if (pureRequest.getUrl().startsWith(controller.getRoute())) {
                    log.info("Transferred to the controller route: {}", controller.getRoute());
                    response = dispatch(controller, pureRequest);
                    log.info("\n\nResponse:\n{}\n{}\n{}\n",SEP, response, SEP);
                    if (response != null)
                        return response;
                    else
                        return Response.NOT_FOUNDED;
                }
            }
            log.info("Route for URL - [{}] - Not Found", request.getUrl());
            return Response.NOT_FOUNDED;
        } catch (FilterRuntimeException e) {
            log.error("Filter Runtime Exception - [{}]", e.getMessage());
            return Response.builder()
                    .headers(new ResponseHeaders())
                    .status(400)
                    .textStatus("Bad Request")
                    .build();
        }
    }
}
