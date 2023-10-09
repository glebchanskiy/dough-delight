package org.glebchanskiy.kek.router.controllers;

import org.glebchanskiy.kek.utils.Request;
import org.glebchanskiy.kek.utils.Response;

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
