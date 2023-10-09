package org.glebchanskiy.doughdelight;

import lombok.SneakyThrows;

import java.io.*;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class Connection {
    private static final int BUFFER_SIZE = 4096;
    private final AsynchronousSocketChannel clientChannel;

    Connection(AsynchronousSocketChannel clientChannel) {
        this.clientChannel = clientChannel;
    }

    public boolean isOpen() {
        return clientChannel.isOpen();
    }

    public void close() throws IOException {
        clientChannel.close();
    }

    public void setKeepAliveOption() throws IOException {
        clientChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
    }

    public byte[] readRequest() throws ExecutionException, InterruptedException, IOException {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        boolean keepReading = true;

        while (keepReading) {
            int bytesRead = this.clientChannel.read(buffer).get();

            if (bytesRead > 0) {
                byte[] array = buffer.array();

                if (bytesRead < BUFFER_SIZE) {
                    array = Arrays.copyOfRange(array, 0, bytesRead);
                    keepReading = false;
                }

                outputStream.write(array);
                buffer.clear();
            } else if (bytesRead == -1) {
                keepReading = false;
            }
        }
        return outputStream.toByteArray();
    }

    public void writeResponse(byte[] response) throws ExecutionException, InterruptedException {
        var packet = ByteBuffer.wrap(response);
        this.clientChannel.write(packet, null, new CompletionHandler<Integer, Void>() {
            @Override
            public void completed(Integer bytesWritten, Void attachment) {
                if (bytesWritten > 0 && packet.hasRemaining()) {
                    clientChannel.write(packet, null, this);
                }
            }

            @SneakyThrows
            @Override
            public void failed(Throwable exc, Void attachment) {
                throw exc;
            }
        });
    }
}
