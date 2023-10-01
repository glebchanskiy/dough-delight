package org.glebchanskiy.doughdelight;

import org.glebchanskiy.doughdelight.utils.Request;
import org.glebchanskiy.doughdelight.utils.RequestHeaders;
import org.glebchanskiy.doughdelight.utils.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Middleware {
    private static final Logger log = LoggerFactory.getLogger(Middleware.class);
    private final Router router = new Router();


    private RequestHeaders parseRequestHeaders(String headers) {
        RequestHeaders requestHeaders = new RequestHeaders();
        String[] rowHeaders = headers.split("\\r?\\n");
        String[] headersArray = new String[rowHeaders.length - 1];

        System.arraycopy(rowHeaders, 1, headersArray, 0, headersArray.length);

        for (var header: headersArray) {
            String[] headerPair = header.split(": ");
            requestHeaders.put(headerPair[0], headerPair[1]);
        }

        return requestHeaders;
    }
    private Request parseRequest(String rowRequest) {
        log.info("parseRequest:\n{}", rowRequest);
        String[] splitedRequest = rowRequest.split("\\r?\\n\\r?\\n");
        String headers = splitedRequest[0];


        String[] firstLine = headers.split("\\r?\\n")[0].split(" ");
        String method = firstLine[0];
        String url = firstLine[1];

        RequestHeaders requestHeaders = parseRequestHeaders(headers);
        Request request;

        if (splitedRequest.length > 1) {
            String body = splitedRequest[1];
            request = Request.builder()
                    .method(method)
                    .url(url)
                    .headers(requestHeaders)
                    .body(body)
                    .build();
        } else {
            request = Request.builder()
                    .method(method)
                    .url(url)
                    .headers(requestHeaders)
                    .build();
        }

        return request;
    }

    public Response process(String rowRequest) {
        Request request = parseRequest(rowRequest);
        log.info("Request: {} {}", request.getMethod(), request.getUrl());
        return router.dispatch(request);
    }
}
