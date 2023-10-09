package org.glebchanskiy;

import org.glebchanskiy.kek.Configuration;
import org.glebchanskiy.kek.ConnectionsManager;
import org.glebchanskiy.kek.Server;
import org.glebchanskiy.kek.controllers.ShareFilesController;
import org.glebchanskiy.kek.router.FilterRouter;
import org.glebchanskiy.kek.router.filters.CorsFilter;
import org.glebchanskiy.kek.utils.Mapper;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;

public class KekServer {

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
