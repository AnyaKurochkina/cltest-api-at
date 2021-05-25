package clp.core.helpers;

public class StubAliasNames {
    String host;
    String port;
    String endpoint;

    public StubAliasNames(String host, String port, String endpoint) {
        this.host = host;
        this.port = port;
        this.endpoint = endpoint;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getEndpoint() {
        return endpoint;
    }
}
