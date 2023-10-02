package org.glebchanskiy;

import org.glebchanskiy.doughdelight.Server;

import java.io.IOException;
import java.net.URISyntaxException;

public class Application {

    public static void main(String... args) throws IOException, URISyntaxException {
        new Server().run();
    }
}
