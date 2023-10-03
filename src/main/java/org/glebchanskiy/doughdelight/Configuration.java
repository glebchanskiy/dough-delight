package org.glebchanskiy.doughdelight;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Data
@NoArgsConstructor
public class Configuration {

    private Integer port;
    private String hostname;
    private String location;
    private String cors;

    public static Configuration load(Path path) throws IOException {
        Configuration config = new Yaml().loadAs(Files.newInputStream(path), Configuration.class);

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

    @Override
    public String toString() {
        return "\nport: " + port  + '\n' +
                "hostname: " + hostname + '\n' +
                "location: " + location + '\n';
    }
}
