package org.glebchanskiy.kek.router.controllers;

import org.glebchanskiy.kek.templater.Model;
import org.glebchanskiy.kek.utils.Request;

public abstract class TemplateController extends AbstractController {
    protected TemplateController(String route) {
        super(route);
    }

    public String getMapping(Model model, Request request) {
        return null;
    }
    public String postMapping(Model model, Request request) {
        return null;
    }
}
