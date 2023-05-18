package core.helper.http;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Api {
    final public Routes.Method method;
    final public String path;
    final public int status;
}
