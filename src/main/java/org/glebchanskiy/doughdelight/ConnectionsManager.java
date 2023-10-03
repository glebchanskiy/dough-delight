package org.glebchanskiy.doughdelight;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ConnectionsManager {
    private static final Logger log = LoggerFactory.getLogger(ConnectionsManager.class);
    private final AsynchronousServerSocketChannel server;

    public ConnectionsManager(Configuration configuration) throws IOException {
        this.server = AsynchronousServerSocketChannel.open();
        server.bind(new InetSocketAddress(configuration.getHostname(), configuration.getPort()));
        log.info("Server started");
        log.info("Listening [{}] on {}:{}", configuration.getLocation(), configuration.getHostname(), configuration.getPort());
    }

    public Connection getConnection() throws ExecutionException, InterruptedException, TimeoutException, IOException {
        return new Connection(this.server.accept().get(10, TimeUnit.MINUTES));
    }
}
