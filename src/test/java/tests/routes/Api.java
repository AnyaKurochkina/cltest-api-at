package tests.routes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface Api {
    abstract String url();

    enum Method {
        GET,
        POST,
        PUT,
        PATCH,
        DELETE
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Route {
        Method method();

        String path();

        int status();
    }
}
