package org.glebchanskiy.kek.router.controllers;

public abstract class AbstractController {
    private final String route;

    protected AbstractController(String route) {
        this.route = route;
    }

    public String getRoute() {
        return route;
    }

}
