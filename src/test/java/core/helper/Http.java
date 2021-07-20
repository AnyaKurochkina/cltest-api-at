package core.helper;

import core.vars.LocalThead;
import core.vars.TestVars;
import io.restassured.path.json.JsonPath;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.Assertions;

import javax.net.ssl.*;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import static org.junit.jupiter.api.Assertions.fail;

public class Http {
    private final String host;
    private String path;
    private String body = "";
    private String method;
    private String methodOverride = "";
    private String contentType = "application/json";

    static {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() { return null; }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                public void checkServerTrusted(X509Certificate[] certs, String authType) {}
            }};
            SSLContext sc = null;
            sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HostnameVerifier allHostsValid = (hostname, session) -> true;
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public Http(String host) {
        this.host = host;
    }

    public Http(String host, JSONObject body) {
        this.host = host;
        this.body = body.toJSONString();
    }

    public HttpResponse get(String path) {
        this.method = "GET";
        this.path = path;
        return request();
    }

    public HttpResponse get(String path, String body) {
        this.body = body;
        return get(path);
    }

    public HttpResponse delete(String path) {
        this.method = "DELETE";
        this.path = path;
        return request();
    }

    public HttpResponse patch(String path) {
        this.method = "POST";
        this.methodOverride = "PATCH";
        this.path = path;
        return request();
    }

    public HttpResponse patch(String path, JSONObject body) {
        this.body = body.toJSONString();
        return patch(path);
    }

    public HttpResponse post(String path, String bodyFormUrlencoded) {
        this.body = bodyFormUrlencoded;
        contentType = "application/x-www-form-urlencoded";
        return post(path);
    }

    public HttpResponse post(String path, JSONObject body) {
        this.body = body.toJSONString();
        return post(path);
    }

    public HttpResponse post(String path) {
        this.method = "POST";
        this.path = path;
        return request();
    }

    private String getBearerToken() {
        TestVars testVars = LocalThead.getTestVars();
        return testVars.getVariable("token_type") + " " + testVars.getVariable("token");
    }

    private HttpResponse request() {
        HttpResponse responseMessage = null;
        try {
            URL url = new URL(host + path);
            URLConnection connection = url.openConnection();
            HttpURLConnection http = (HttpURLConnection) connection;
            http.setRequestProperty("Content-Type", contentType);
            if (getBearerToken().length() > 10)
                http.setRequestProperty("Authorization", getBearerToken());
            http.setDoOutput(true);
            http.setRequestMethod(method);
            if (methodOverride.length() > 0) {
                http.setRequestProperty("X-HTTP-Method-Override", methodOverride);
            }
            if (body.length() > 0) {
                http.getOutputStream().write((body.trim()).getBytes(StandardCharsets.UTF_8));
            }
            InputStream is;
            if (http.getResponseCode() >= 400)
                is = http.getErrorStream();
            else
                is = http.getInputStream();
            responseMessage = new HttpResponse(IOUtils.toString(is, StandardCharsets.UTF_8));
            responseMessage.status = http.getResponseCode();
            http.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        return responseMessage;
    }

    public class HttpResponse {
        int status;
        String response;

        public HttpResponse(String response) {
            this.response = response;
        }

        public HttpResponse assertStatus(int s) {
            Assertions.assertEquals(s, status, (String.format("\nResponse: %s\nRequest: %s\n", response, body)));
            return this;
        }

        public int status() {
            return status;
        }

        public JSONObject toJson() {
            try {
                return (JSONObject) new JSONParser().parse(response);
            } catch (Exception e) {
                throw new Error(e.getMessage());
            }
        }

        public JSONArray toJsonArray() {
            try {
                return (JSONArray) new JSONParser().parse(response);
            } catch (Exception e) {
                throw new Error(e.getMessage());
            }
        }

        public JsonPath jsonPath() {
            try {
                return new JsonPath(response);
            } catch (Exception e) {
                throw new Error(e.getMessage());
            }
        }

        public String toString() {
            return response;
        }
    }

}
