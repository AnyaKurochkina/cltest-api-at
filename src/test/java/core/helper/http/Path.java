package core.helper.http;

import lombok.AllArgsConstructor;
import tests.routes.Api;

@AllArgsConstructor
public class Path {
    final public Api.Method method;
    final public String path;
    final public int status;
    final public String url;
}
