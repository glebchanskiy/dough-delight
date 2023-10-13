package org.glebchanskiy;

import org.glebchanskiy.kek.Configuration;
import org.glebchanskiy.kek.ConnectionsManager;
import org.glebchanskiy.kek.Server;
import org.glebchanskiy.kek.controllers.ShareFilesController;
import org.glebchanskiy.kek.controllers.TestTemplateController;
import org.glebchanskiy.kek.router.FilterRouter;
import org.glebchanskiy.kek.router.filters.CorsFilter;
import org.glebchanskiy.kek.utils.Mapper;
import java.util.concurrent.Executors;

public class KekServer {


    public static void main(String... args)  {

        var config = Configuration.parseArgs(args);

        System.out.println("[configuration]:" + config);
        var router = new FilterRouter();
        router.addFilter(new CorsFilter(config));
        router.addController(new ShareFilesController("/", config));
        router.addController(new TestTemplateController("/cheburek"));

        var server = Server.builder()
                .connectionsManager(new ConnectionsManager(config))
                .executorService(Executors.newFixedThreadPool(10))
                .mapper(new Mapper())
                .router(router)
                .build();

        server.run();

    }
}
