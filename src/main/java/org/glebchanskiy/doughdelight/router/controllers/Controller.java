package org.glebchanskiy.doughdelight.router.controllers;

import org.glebchanskiy.doughdelight.utils.Request;
import org.glebchanskiy.doughdelight.utils.Response;

public abstract class Controller {

    private final String route;

    protected Controller(String route) {
        this.route = route;
    }

    public String getRoute() {
        return route;
    }

    public Response getMapping(Request request) {
        return null;
    }

    public Response postMapping(Request request) {
        return null;
    }

    public Response optionsMapping(Request request) {
        return null;
    }

}
