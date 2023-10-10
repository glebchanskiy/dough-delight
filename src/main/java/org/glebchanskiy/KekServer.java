package org.glebchanskiy;

import org.glebchanskiy.kek.Configuration;
import org.glebchanskiy.kek.ConnectionsManager;
import org.glebchanskiy.kek.Server;
import org.glebchanskiy.kek.controllers.ShareFilesController;
import org.glebchanskiy.kek.router.FilterRouter;
import org.glebchanskiy.kek.router.filters.CorsFilter;
import org.glebchanskiy.kek.utils.Mapper;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.IOException;
import java.nio.file.Path;

public class KekServer {

    static class Options {

        @Option(name = "-c", aliases = "--config",
                usage = "Configuration path", metaVar = "config")
        public String configPath;

        @Option(name = "-p", aliases = "--port",
                usage = "Port", metaVar = "port")
        public Integer port;

        @Option(name = "-l", aliases = "--location",
                usage = "Work directory", metaVar = "location")
        public String location;

        @Option(name = "-a", aliases = "--cors",
                usage = "Set cors hostname", metaVar = "cors")
        public String cors;
    }

    public static void main(String... args) throws IOException, IllegalAccessException {

        var options = new Options();
        var parser = new CmdLineParser(options);
        Configuration config = null;

        try {
            parser.parseArgument(args);
            if (options.configPath != null) {
                config = Configuration.load(Path.of(options.configPath));
            } else {
                config = Configuration.loadDefault();
            }
            if (options.port != null) {
                config.setPort(options.port);
            }
            if (options.location != null) {
                config.setLocation(options.location);
            }
            if (options.cors != null) {
                config.setCors(options.cors);
            }
        } catch (CmdLineException ignored) {
            parser.printUsage(System.out);
            return;
        }

        System.out.println("[configuration]:" + config);
        bootstrap(config);
    }

    private static void bootstrap(Configuration configuration) throws IOException {
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
