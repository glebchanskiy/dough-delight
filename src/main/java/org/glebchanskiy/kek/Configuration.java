package org.glebchanskiy.kek;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

@Data
@NoArgsConstructor
public class Configuration {
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

    private Integer port;
    private String hostname;
    private String location;
    private String cors;

    @SuppressWarnings("java:S3011")
    public static Configuration load(Path path) throws IOException, IllegalAccessException {
        Configuration configuration = new Yaml().loadAs(Files.newInputStream(path), Configuration.class);
        Configuration defaultConfiguration = loadDefault();

        Field[] fields = defaultConfiguration.getClass().getDeclaredFields();

        for (Field field : fields) {
            var defaultValue = field.get(defaultConfiguration);
            var loadedValue = field.get(configuration);

            if (loadedValue == null) {
                field.set(configuration, defaultValue);
            }
        }

        return configuration;
    }

    public static Configuration loadDefault() {
        InputStream configFileStream = Configuration.class.getResourceAsStream("/config.yaml");
        Configuration config = new Yaml().loadAs(configFileStream, Configuration.class);

        if (config.port == null)
            config.port = 80;
        if (config.hostname == null)
            config.hostname = "127.0.0.1";
        if (config.location == null)
            config.location = System.getProperty("user.dir");
        if (config.cors == null)
            config.cors = config.hostname;

        return config;
    }

    public static Configuration parseArgs(String... args) {
        var options = new Configuration.Options();
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
        } catch (CmdLineException | IllegalAccessException | IOException ignored) {
            parser.printUsage(System.out);
            throw new ServerRuntimeException();
        }

        return config;
    }

    @Override
    public String toString() {
        return "\nport: " + port  + '\n' +
                "hostname: " + hostname + '\n' +
                "location: " + location + '\n' +
                "cors: " + cors + '\n';
    }
}
