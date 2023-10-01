package org.glebchanskiy.doughdelight;

import org.glebchanskiy.doughdelight.utils.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Server {
    private static final Logger log = LoggerFactory.getLogger(Server.class);
    private static final int BUFFER_SIZE = 2048;
    private Configuration configuration;
    private final Middleware middleware = new Middleware();

    // Custom config
    public void run(Configuration configuration) {
        this.configuration = configuration;
        bootstrap();
    }

    // Use default config
    public void run() throws URISyntaxException, IOException {
        this.configuration = Configuration.load(
                Path.of(ClassLoader.getSystemClassLoader()
                        .getResource("config.yaml").toURI())
        );
        bootstrap();
    }

    private void bootstrap() {
        log.info("Server started");
        try (var server = AsynchronousServerSocketChannel.open()) {
            server.bind(new InetSocketAddress(configuration.getHostname(), configuration.getPort()));
            log.info("Listening [{}] on {}:{}", configuration.getLocation(), configuration.getHostname(), configuration.getPort());
            listen(server);
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
    @SuppressWarnings("squid:S2189")
    private void listen(AsynchronousServerSocketChannel server) throws ExecutionException, InterruptedException, TimeoutException, IOException {
        while (true) {
            Future<AsynchronousSocketChannel> client = server.accept();
            handleClient(client);
        }
    }

    private void handleClient(Future<AsynchronousSocketChannel> client) throws ExecutionException, InterruptedException, TimeoutException, IOException {
        AsynchronousSocketChannel clientChannel = client.get(10, TimeUnit.MINUTES);
        if (clientChannel == null)
            return;

        log.info("Client connected: {}", clientChannel.getRemoteAddress());

        while (clientChannel.isOpen()) {
            String request = readRequest(clientChannel);
            log.info("clientChannel.isOpen()");
            Response response = middleware.process(request);
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
        return builder.toString();
    }

    private void writeResponse(AsynchronousSocketChannel clientChannel, Response response) throws IOException {

        if (response.getBinary() == null) {
            log.info("Response:\n{}", response);
            var packet = ByteBuffer.wrap(response.toString().getBytes());
            clientChannel.write(packet);
        } else {
            log.info("Response:\n{}[BINARY]", response);
            var headers = ByteBuffer.wrap(response.toString().getBytes());
            var body = ByteBuffer.wrap(response.getBinary());
            clientChannel.write(headers);
            clientChannel.write(body);
        }

    }
}
