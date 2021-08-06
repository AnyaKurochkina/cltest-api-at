package core.helper;

import io.restassured.path.json.JsonPath;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import steps.keyCloak.KeyCloakSteps;


import javax.net.ssl.*;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@Log4j2
public class Http {
    private final String host;
    private String path;
    private String body = "";
    private String method;
    private String token = "";
    private String contentType = "application/json";
    private String accept = "application/json, text/plain, */*";
    private boolean isUsedToken = true;

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
        try {
            Field methodsField = HttpURLConnection.class.getDeclaredField("methods");
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(methodsField, methodsField.getModifiers() & ~Modifier.FINAL);
            methodsField.setAccessible(true);
            methodsField.set(null, new String[]{"GET", "POST", "HEAD", "OPTIONS", "PUT", "DELETE", "TRACE", "PATCH"});
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    public Http(String host) {
        this.host = host;
    }

    public Http(String host, JSONObject body) {
        this.host = host;
        this.body = body.toString();
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
        this.method = "PATCH";
        this.path = path;
        return request();
    }

    public HttpResponse patch(String path, JSONObject body) {
        this.body = body.toString();
        return patch(path);
    }

    public Http setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public HttpResponse post(String path, JSONObject body) {
        this.body = body.toString();
        return post(path);
    }

    public HttpResponse post(String path, String body) {
        this.body = body;
        return post(path);
    }

    public HttpResponse post(String path) {
        this.method = "POST";
        this.path = path;
        return request();
    }

    public Http setProjectId(String projectId) {
        this.token = "bearer " + KeyCloakSteps.getServiceAccountToken(projectId);
        return this;
    }

    public Http setWithoutToken() {
        isUsedToken = false;
        return this;
    }

    private HttpResponse request() {
        HttpResponse responseMessage = null;
        try {
            URL url = new URL(host + path);
            URLConnection connection = url.openConnection();
            HttpURLConnection http = (HttpURLConnection) connection;
            http.setRequestProperty("Content-Type", contentType);
            http.setRequestProperty("Accept", accept);
            if(isUsedToken) {
                if (token.length() > 0)
                    http.setRequestProperty("Authorization", token);
                else {
                    KeyCloakSteps keyCloakSteps = new KeyCloakSteps();
                    token = "bearer " + keyCloakSteps.getUserToken();
                    http.setRequestProperty("Authorization", token);
                }
            }
            http.setDoOutput(true);
            http.setRequestMethod(method);
            log.debug("URL: {}", (host + path));
            if (body.length() > 0) {
                log.debug("REQUEST: {}", body);
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
            assertEquals(String.format("\nResponse: %s\nRequest: %s\n%s\n", response, host + path, body), s, status);
            return this;
        }

        public int status() {
            return status;
        }

        public JSONObject toJson() {
            try {
                return (JSONObject) new JSONObject(response);
            } catch (Exception e) {
                throw new Error(e.getMessage());
            }
        }

        public JSONArray toJsonArray() {
            try {
                return (JSONArray) new JSONArray(response);
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
