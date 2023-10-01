package org.glebchanskiy.doughdelight;

import org.glebchanskiy.doughdelight.utils.Request;
import org.glebchanskiy.doughdelight.utils.Response;

public interface Controller {

    String getMapping();
    Response get(Request request);
    Response post(Request request);
}
