package org.glebchanskiy;

import org.glebchanskiy.doughdelight.Configuration;
import org.glebchanskiy.doughdelight.ConnectionsManager;
import org.glebchanskiy.doughdelight.Server;
import org.glebchanskiy.doughdelight.controllers.ShareFilesController;
import org.glebchanskiy.doughdelight.router.FilterRouter;
import org.glebchanskiy.doughdelight.router.filters.CorsFilter;
import org.glebchanskiy.doughdelight.utils.Mapper;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;

public class Application {

    public static void main(String... args) throws IOException, URISyntaxException {
        Path configPath = Path.of(ClassLoader.getSystemClassLoader().getResource("config.yaml").getPath());
        Configuration configuration = Configuration.load(configPath);

        var connectionsManager = new ConnectionsManager(configuration);

        var mapper = new Mapper();

        var router = new FilterRouter();
        router.addFilter(new CorsFilter(configuration));
        router.addController(new ShareFilesController("/", configuration));

        new Server(
                connectionsManager,
                mapper,
                router
        ).run();
    }
}
