package org.glebchanskiy.doughdelight;

import org.glebchanskiy.doughdelight.utils.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Server {
    private static final Logger log = LoggerFactory.getLogger(Server.class);

    private static final int BUFFER_SIZE = 2048;
    private static final int  DEFAULT_PORT = 8088;
    private static final String DEFAULT_HOSTNAME = "127.0.0.1";

    private Configuration configuration;
    private final Middleware middleware = new Middleware();
    private AsynchronousServerSocketChannel server;

    public void run(Configuration configuration) {
        this.configuration = configuration;
        bootstrap();
    }

    public void run() {
        this.configuration = new Configuration();
        this.configuration.setPort(DEFAULT_PORT);
        this.configuration.setHostname(DEFAULT_HOSTNAME);
        this.configuration.setLocation(System.getProperty("user.dir"));
    }

    private void bootstrap() {
        log.info("Server started");
        try {
            log.info("Configuration:\n{}", configuration);
            server = AsynchronousServerSocketChannel.open();
            server.bind(new InetSocketAddress(configuration.getHostname(), configuration.getPort()));
            log.info("listening on {}:{}", configuration.getHostname(), configuration.getPort());
            listen();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    private void listen() throws ExecutionException, InterruptedException, TimeoutException, IOException {
        while (true) {
            Future<AsynchronousSocketChannel> client = server.accept();
            handleClient(client);
        }
    }

    private void handleClient(Future<AsynchronousSocketChannel> client) throws ExecutionException, InterruptedException, TimeoutException, IOException {
        AsynchronousSocketChannel clientChannel = client.get(120, TimeUnit.SECONDS);
        if (clientChannel == null)
            return;

        log.info("New client connected: {}", clientChannel.getRemoteAddress());

        while (clientChannel.isOpen()) {
            String request = readRequest(clientChannel);
            Response response = middleware.process(request);
            log.info("response: {}", response);
            writeResponse(clientChannel, response);
            clientChannel.close();
        }
    }

    private String readRequest(AsynchronousSocketChannel clientChannel) throws ExecutionException, InterruptedException {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        StringBuilder builder = new StringBuilder();

        boolean keepReading = true;

        while (keepReading) {
            clientChannel.read(buffer).get();

            int position = buffer.position();
            keepReading = position == BUFFER_SIZE;

            byte[] array = keepReading
                    ? buffer.array()
                    : Arrays.copyOfRange(buffer.array(), 0, position);

            builder.append(new String(array));
            buffer.clear();
        }
        log.info("builder:\n{}", builder);
        return builder.toString();
    }

    private void writeResponse(AsynchronousSocketChannel clientChannel, Response response) throws IOException {
        log.info("final response: {}{}", response, response.getBinary());
        if (response.getBinary() == null) {
            var packet = ByteBuffer.wrap(response.toString().getBytes());
            clientChannel.write(packet);
        } else {
            var headers = ByteBuffer.wrap(response.toString().getBytes());
            var body = ByteBuffer.wrap(response.getBinary());
            clientChannel.write(headers);
            clientChannel.write(body);
        }

    }
}
