package core.helper.http;

import api.routes.Routes;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Api {
    final public Routes.Method method;
    final public String path;
    final public int status;
}
