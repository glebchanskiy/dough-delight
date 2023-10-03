package org.glebchanskiy.doughdelight.router;

import org.glebchanskiy.doughdelight.Server;
import org.glebchanskiy.doughdelight.router.controllers.Controller;
import org.glebchanskiy.doughdelight.router.filters.Filter;
import org.glebchanskiy.doughdelight.utils.Request;
import org.glebchanskiy.doughdelight.utils.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FilterRouter {
    private static final Logger log = LoggerFactory.getLogger(FilterRouter.class);
    private Filter filter;
    private final List<Controller> controllers = new ArrayList<>();
    public void addFilter(Filter filter) {
        if (this.filter != null)
            this.filter.addNext(filter);
        else this.filter = filter;
    }
    public void addController(Controller controller) {
        this.controllers.add(controller);

        this.controllers.sort(Comparator.comparingInt(o -> o.getRoute().length()));
    }

    private Response dispatch(Controller controller, Request request) {
        switch (request.getMethod()) {
            case "GET" -> {return controller.getMapping(request);}
            case "POST" ->  {return controller.postMapping(request);}
            case "OPTIONS" -> {return controller.optionsMapping(request);}
            default ->
            {return Response.builder()
                        .status(405)
                        .textStatus("Method Not Allowed")
                        .build();}
        }
    }

    public Response process(Request request) {
        Request pureRequest = this.filter.filter(request);
        Response response;

        for (Controller controller : this.controllers) {
            if (pureRequest.getUrl().startsWith(controller.getRoute())) {
                response = dispatch(controller, pureRequest);
                if (response != null)
                    return response;
                else
                    return Response.NOT_FOUNDED;
            }
        }

        return Response.NOT_FOUNDED;
    }
}
