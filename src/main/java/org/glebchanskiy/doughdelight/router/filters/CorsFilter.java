package org.glebchanskiy.doughdelight.router.filters;

import org.glebchanskiy.doughdelight.Configuration;
import org.glebchanskiy.doughdelight.utils.Request;

public class CorsFilter extends Filter {

    private final Configuration configuration;
    public CorsFilter(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Request filter(Request request) {
        String host = request.getHeaders().get("Host");
        if (!configuration.getCors().equals("*") && !host.equals(configuration.getCors())) {
            throw new FilterRuntimeException("Cross-Origin Request");
        }

        return next(request);
    }
}
