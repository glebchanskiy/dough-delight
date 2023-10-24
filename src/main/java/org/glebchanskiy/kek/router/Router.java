package org.glebchanskiy.kek.router;

import org.glebchanskiy.kek.router.controllers.AbstractController;
import org.glebchanskiy.kek.router.controllers.Controller;
import org.glebchanskiy.kek.utils.Request;
import org.glebchanskiy.kek.utils.Response;
import org.glebchanskiy.kek.utils.ResponseHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Router {
    private static final Logger log = LoggerFactory.getLogger("ServerDetailed");
    private final List<Controller> controllers;

    public Router() {
        this.controllers = new ArrayList<>();
    }

    public void addController(Controller controller) {
        this.controllers.add(controller);
    }

    public Response route(Request request) {
        Response response = null;
        log.info("\n\nRequest {}:{} Details:\n{}", request.getMethod(), request.getUrl(), request);
        for (var controller : controllers) {
            if (rightRoute(controller, request.getUrl())) {
                response = dispatchMethod(controller, request);
            }
        }
        if (response == null)
            response = Response.NOT_FOUNDED;

        log.info("\n\nResponse details:\n{}", response);
        return response;
    }

    private Response dispatchMethod(Controller controller, Request request) {
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

    private boolean rightRoute(AbstractController controller, String route) {
        int i = 0;
        while (i < route.length()) {
            if (route.charAt(i) == '&')
                break;
            else
                i++;
        }
        System.out.println("rightRoute : " + route.substring(0, i));
        return controller.getRoute().equals(route.substring(0, i));
    }
}
