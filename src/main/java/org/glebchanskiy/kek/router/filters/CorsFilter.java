package org.glebchanskiy.kek.router.filters;

import org.glebchanskiy.kek.Configuration;
import org.glebchanskiy.kek.utils.Request;

public class CorsFilter extends Filter {

    private final Configuration configuration;
    public CorsFilter(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Request filter(Request request) {
        if (!request.getHeaders().containsKey("Host"))
            throw new FilterRuntimeException("Required header: Host");

        String host = request.getHeaders().get("Host");

        if (!configuration.getCors().equals("*") && !host.equals(configuration.getCors())) {
            throw new FilterRuntimeException("Cross-Origin Request");
        }

        return next(request);
    }
}
