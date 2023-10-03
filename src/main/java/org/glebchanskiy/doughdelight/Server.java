package org.glebchanskiy.doughdelight;

import org.glebchanskiy.doughdelight.router.FilterRouter;
import org.glebchanskiy.doughdelight.utils.Mapper;
import org.glebchanskiy.doughdelight.utils.Request;
import org.glebchanskiy.doughdelight.utils.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class Server {
    private static final Logger log = LoggerFactory.getLogger(Server.class);
    private final ConnectionsManager connectionsManager;
    private final Mapper mapper;
    private final FilterRouter router;

    public Server(ConnectionsManager connectionsManager, Mapper mapper, FilterRouter router) {
        this.connectionsManager = connectionsManager;
        this.mapper = mapper;
        this.router = router;
    }

    @SuppressWarnings("squid:S2189")
    public void run() {
        while (true) {
            try {
                Connection connection = connectionsManager.getConnection();
                log.info("Client connected");
                while (connection.isOpen()) {
                    log.info("Connection is open");
                    var byteRequest = connection.readRequest();
                    log.info("Bytes read");
                    Request request = mapper.parseRequest(byteRequest);
                    log.info("Request parsed:\n{}", request);
                    Response response = router.process(request);
                    log.info("Response received :\n{}", response);
                    var byteResponse = mapper.toBytes(response);
                    log.info("Response bytes received");
                    connection.writeResponse(byteResponse);
                    log.info("Response written");
                    connection.close();
                }
                log.info("Client disconnected");
            } catch (ExecutionException | InterruptedException | TimeoutException | IOException e) {
                log.warn("Error received: {}", e.getMessage());
                throw new ServerRuntimeException(e.getMessage());
            }
        }
    }
}
