package tests.routes;

import core.helper.http.Path;

import static core.helper.Configure.KONG_URL;


public class SecretServiceAPI implements Api {
    public static String URL = KONG_URL + "secret-service/api";

    @Route(method = Method.GET, path = "/v1/health", status = 200)
    public static Path getV1Health;

    //Получение данных секрета
    @Route(method = Method.GET, path = "/v1/secrets", status = 200)
    public static Path getV1Secrets;

    @Route(method = Method.GET, path = "/v1/version", status = 200)
    public static Path getV1Version;

    @Override
    public String url() {
        return KONG_URL + "secret-service/api";
    }
}
