package org.glebchanskiy.kek.router.controllers;

import org.glebchanskiy.kek.utils.Request;
import org.glebchanskiy.kek.utils.Response;
import org.glebchanskiy.kek.utils.ResponseHeaders;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@FunctionalInterface
interface Function4<T, K, R> {
    public R apply(T t, K k);
}
public abstract class RestController extends Controller {
    public RestController(String route) {
        super(route);
    }

    @Override
    public Response getMapping(Request request) {
        return restify(request, this::get);
    }

    @Override
    public Response postMapping(Request request) {
        return restify(request, this::post);
    }

    @Override
    public Response deleteMapping(Request request) {
        return restify(request, this::delete);
    }

    @Override
    public Response updateMapping(Request request) {
        return restify(request, this::update);
    }

    public String get(String body, String param) {
        return null;
    }

    public String post(String body, String param) {
        return null;
    }

    public String update(String body, String param) {
        return null;
    }

    public String delete(String body, String param) {
        return null;
    }


    private Response restify(Request request, Function4<String, String, String> method) {
        try {
            var headers = new ResponseHeaders();
            String body = method.apply(request.getBody(), getParam(request.getUrl()));
            if (body != null && !body.isEmpty()) {
                headers.put("Content-Type", "application/json");
                headers.put("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
                return Response.builder().status(200).textStatus("OK").body(body).headers(headers).build();
            } else {
                if (request.getBody() != null && !request.getBody().isEmpty())
                    return Response.builder().status(202).textStatus("ACCEPTED").headers(new ResponseHeaders()).build();
                return Response.builder().status(200).textStatus("OK").headers(new ResponseHeaders()).build();
            }
        } catch (RuntimeException e) {
            return Response.builder().status(400).textStatus("BAD REQUEST").build();
        }

    }

    private String getParam(String url) {
        String utf8EncodedUrl = URLDecoder.decode(url, StandardCharsets.UTF_8);
        String route = this.getRoute().replace("*", "");

        if (!route.equals(utf8EncodedUrl)) {

            return utf8EncodedUrl.replace(route + "/", "");
        } else {
            return null;
        }
    }
}
