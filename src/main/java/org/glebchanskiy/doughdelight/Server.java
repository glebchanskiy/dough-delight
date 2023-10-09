package org.glebchanskiy.doughdelight;

import org.glebchanskiy.doughdelight.router.FilterRouter;
import org.glebchanskiy.doughdelight.utils.Mapper;
import org.glebchanskiy.doughdelight.utils.Request;
import org.glebchanskiy.doughdelight.utils.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

public class Server {
    private static final Logger log = LoggerFactory.getLogger("Server");
    private static final int THREAD_POOL_SIZE = 10;
    private final ExecutorService executorService;
    private final ConnectionsManager connectionsManager;
    private final Mapper mapper;
    private final FilterRouter router;

    public Server(ConnectionsManager connectionsManager, Mapper mapper, FilterRouter router) {
        this.connectionsManager = connectionsManager;
        this.executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        this.mapper = mapper;
        this.router = router;
    }

    @SuppressWarnings("squid:S2189")
    public void run() {
        while (true) {
            try {
                Connection connection = connectionsManager.getConnection();
                executorService.execute(() -> handleConnection(connection));
            } catch (ExecutionException | InterruptedException | TimeoutException | IOException e) {
                log.warn("Error received: {}", e.getMessage());
                throw new ServerRuntimeException(e.getMessage());
            }
        }
    }

    public void handleConnection(Connection connection) {
            try {
                log.info("Client connected");
                long startTime = System.currentTimeMillis();
                long duration = 3000;

                while (connection.isOpen()) {
                    if (System.currentTimeMillis() - startTime > duration) {
                        connection.close();
                        break;
                    }

                    var byteRequest = connection.readRequest();

                    if (byteRequest.length == 0)
                        continue;

                    Request request = mapper.parseRequest(byteRequest);
                    log.info("Request: {} {}", request.getMethod(), request.getUrl());
                    Response response = router.process(request);
                    log.info("Response: {} {}", response.getStatus(), response.getTextStatus());
                    var byteResponse = mapper.toBytes(response);
                    connection.writeResponse(byteResponse);

                    if (shouldKeepAlive(request)) {
                        response.getHeaders().put("Connection", "keep-alive");
                        connection.setKeepAliveOption();
                        log.info("keep-alive request");
                        duration += 3000;
                    } else {
                        connection.close();
                    }
                }
                log.info("Client disconnected");
            } catch (ExecutionException | InterruptedException | IOException e) {
                log.warn("Error received: {}", e.getMessage());
                throw new ServerRuntimeException(e.getMessage());
            }
    }

    private boolean shouldKeepAlive(Request request) {
        return request.getHeaders().getOrDefault("Connection", "keep-alive").equals("keep-alive");
    }
}
