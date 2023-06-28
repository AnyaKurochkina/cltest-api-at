package core.helper.http;

import api.routes.Api;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Path {
    final public Api.Method method;
    final public String path;
    final public int status;
    final public String url;
}
