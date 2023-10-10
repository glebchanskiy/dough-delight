package org.glebchanskiy.kek.controllers;

import lombok.Getter;
import org.glebchanskiy.kek.router.controllers.TemplateController;
import org.glebchanskiy.kek.templater.Model;
import org.glebchanskiy.kek.utils.Request;

import java.util.ArrayList;
import java.util.List;

@Getter
class User {
    public User(String username) {
        this.username = username;
    }
    private final String username;
}

public class TestTemplateController extends TemplateController {
    public TestTemplateController(String route) {
        super(route);
    }

    @Override
    public String getMapping(Model model, Request request) {
        List<User> users = new ArrayList<>(List.of(
                new User("Jotaro"),
                new User("Dio"),
                new User("Pournalef")
        ));

        model.put("users", users);
        return "/landing.html";
    }
}
