package org.glebchanskiy.doughdelight;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class Connection {
    private static final int BUFFER_SIZE = 2048;
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

    public void writeResponse(byte[] response) {
        var packet = ByteBuffer.wrap(response);
        this.clientChannel.write(packet);
    }
}

//    public void writeResponse(byte[] response) {
//        if (response.getBinary() == null) {
//            var packet = ByteBuffer.wrap(response.toString().getBytes());
//            this.clientChannel.write(packet);
//        } else {
//            var headers = ByteBuffer.wrap(response.toString().getBytes());
//            var body = ByteBuffer.wrap(response.getBinary());
//            this.clientChannel.write(headers);
//            this.clientChannel.write(body);
//        }
//
//    }
