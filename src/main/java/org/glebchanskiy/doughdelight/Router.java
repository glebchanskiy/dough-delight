package org.glebchanskiy.doughdelight;

import org.glebchanskiy.doughdelight.controllers.ShareFilesController;
import org.glebchanskiy.doughdelight.utils.Request;
import org.glebchanskiy.doughdelight.utils.Response;
import org.glebchanskiy.doughdelight.utils.TextStatus;

import java.util.LinkedList;
import java.util.List;

public class Router {
    private final List<Controller> controllers;

    public Router() {
        this.controllers = new LinkedList<>();
        controllers.add(new ShareFilesController());
    }


    public Response dispatch(Request request) {
        for (Controller controller : controllers) {
            if (request.getUrl().startsWith(controller.getMapping())) {
                switch (request.getMethod()) {
                    case "GET" -> {
                        return controller.get(request);
                    }
                    case "POST" -> {
                        return controller.post(request);
                    }
                }
            }
        }
        return Response.builder()
                .status(404)
                .textStatus(TextStatus.BAD_REQUEST)
                .build();
    }
}
