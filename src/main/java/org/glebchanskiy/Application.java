package org.glebchanskiy;

import org.glebchanskiy.doughdelight.Configuration;
import org.glebchanskiy.doughdelight.Server;

import java.io.IOException;
import java.nio.file.Path;

public class Application {

    public static void main(String... args) throws IOException {
        Path path = Path.of("/Users/glebchanskiy/subjects/aipos/dough-delight/src/main/java/org/glebchanskiy/config.yaml");
        new Server().run(Configuration.load(path));
    }
}
