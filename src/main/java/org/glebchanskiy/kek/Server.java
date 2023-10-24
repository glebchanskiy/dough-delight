package org.glebchanskiy.kek;

import lombok.Builder;
//import org.glebchanskiy.kek.router.FilterRouter;
import org.glebchanskiy.kek.router.Router;
import org.glebchanskiy.kek.router.filters.Filter;
import org.glebchanskiy.kek.router.filters.FilterRuntimeException;
import org.glebchanskiy.kek.utils.Mapper;
import org.glebchanskiy.kek.utils.Request;
import org.glebchanskiy.kek.utils.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;

@Builder
public class Server {
    @Builder.Default
    private static final Logger log = LoggerFactory.getLogger("Server");
    private static final int THREAD_POOL_SIZE = 10;
    private final ExecutorService executorService;
    private final ConnectionsManager connectionsManager;
    private final Mapper mapper;
//    private final FilterRouter router;
    private final Router router;

    private Filter filter;


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
//                    Response response = router.process(request);
                    request = filter.filter(request);
                    Response response = router.route(request);
                    log.info("Response: {} {}", response.getStatus(), response.getTextStatus());
                    var byteResponse = mapper.toBytes(response);
                    connection.writeResponse(byteResponse);

                    if (shouldKeepAlive(request) && response.getStatus() < 300) {
                        response.getHeaders().put("Connection", "keep-alive");
                        connection.setKeepAliveOption();
                        log.info("keep-alive request");
                        duration += 3000;
                    } else {
                        connection.close();
                    }
                }
                log.info("Client disconnected");
            } catch (ExecutionException | InterruptedException | IOException | FilterRuntimeException e) {
                log.warn("Error received: {}", e.getMessage());
                throw new ServerRuntimeException(e.getMessage());
            }
    }

    private boolean shouldKeepAlive(Request request) {
        return request.getHeaders().getOrDefault("Connection", "keep-alive").equals("keep-alive");
    }
}
