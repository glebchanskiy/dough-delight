package org.glebchanskiy.doughdelight.utils;

import org.glebchanskiy.doughdelight.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public class Mapper {
    private static final Logger log = LoggerFactory.getLogger(Mapper.class);
    private static final String HEADER_SEPORATOR = ": ";

    public Request parseRequest(byte[] byteRequest) {
        String rowRequest = new String(byteRequest);
//        log.info("rowRequest: \n{}", rowRequest);
        String rowHeaders = getRowRequestHeader(rowRequest);
        String rowBody = getRowRequestBody(rowRequest);
        return Request.builder()
                .method(getRowHeaderMethod(rowHeaders))
                .url(getRowHeaderUrl(rowHeaders))
                .headers(parseRequestHeaders(rowHeaders))
                .body(rowBody)
                .build();
    }

    private RequestHeaders parseRequestHeaders(String rowHeaders) {
        RequestHeaders requestHeaders = new RequestHeaders();

        for (String header: getRowHeadersPairs(rowHeaders)) {
            String[] headerPair = header.split(HEADER_SEPORATOR);
            requestHeaders.put(headerPair[0], headerPair[1]);
        }

        return requestHeaders;
    }

    private String getRowRequestHeader(String rowRequest) {
        return rowRequest.split("\\r?\\n\\r?\\n")[0];
    }

    private String getRowRequestBody(String rowRequest) {
        var split = rowRequest.split("\\r?\\n\\r?\\n");
        return split.length > 1 ? split[1] : null;
    }

    private String getRowHeaderUrl(String rowHeader) {
        return rowHeader.split("\\r?\\n")[0].split(" ")[1];
    }

    private String getRowHeaderMethod(String rowHeader) {
        return rowHeader.split("\\r?\\n")[0].split(" ")[0];
    }

    private String[] getRowHeadersPairs(String rowHeader) {
        String[] headersPairsWithRequestLine = rowHeader.split("\\r?\\n");
        String[] headersPairs = new String[headersPairsWithRequestLine.length - 1];
        System.arraycopy(headersPairsWithRequestLine, 1, headersPairs, 0, headersPairs.length);

        return headersPairs;
    }

    public byte[] toBytes(Response response) {
        if (response.getBinary() == null) {
            return ByteBuffer.wrap(response.toString().getBytes()).array();
        } else {
            var headers = ByteBuffer.wrap(response.toString().getBytes());
            var body = ByteBuffer.wrap(response.getBinary());
            var packet = ByteBuffer.allocate(headers.array().length + body.array().length);
            packet.put(headers);
            packet.put(body);
            return packet.array();
        }
    }
}
