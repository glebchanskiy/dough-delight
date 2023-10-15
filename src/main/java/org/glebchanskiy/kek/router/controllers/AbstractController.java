package org.glebchanskiy.kek.router.controllers;

import lombok.Getter;

@Getter
public abstract class AbstractController {
    private final String route;

    protected AbstractController(String route) {
        this.route = route;
    }
}
