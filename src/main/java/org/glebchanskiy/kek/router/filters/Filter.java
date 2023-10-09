package org.glebchanskiy.kek.router.filters;

import org.glebchanskiy.kek.utils.Request;

public abstract class Filter {
    Filter nextFilter = null;
    public Request filter(Request request) throws FilterRuntimeException {
        return request;
    }

    public void addNext(Filter filter) {
        if (nextFilter == null)
            nextFilter = filter;
        else
            nextFilter.addNext(filter);
    }

    Request next(Request request) {
        if (nextFilter == null)
            return request;
        return nextFilter.filter(request);
    }
}
