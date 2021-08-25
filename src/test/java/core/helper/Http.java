package core.helper;

import io.qameta.allure.Allure;
import io.restassured.path.json.JsonPath;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
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


import static core.helper.JsonHelper.stringPrettyFormat;
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
    private boolean isUsedToken = true;

    int status;
    String responseMessage = "";

    static {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
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

    public Http get(String path) {
        this.method = "GET";
        this.path = path;
        return request();
    }

    public Http get(String path, String body) {
        this.body = body;
        return get(path);
    }

    public Http delete(String path) {
        this.method = "DELETE";
        this.path = path;
        return request();
    }

    public Http patch(String path) {
        this.method = "PATCH";
        this.path = path;
        return request();
    }

    public Http patch(String path, JSONObject body) {
        this.body = body.toString();
        return patch(path);
    }

    public Http setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public Http post(String path, JSONObject body) {
        this.body = body.toString();
        return post(path);
    }

    public Http post(String path, String body) {
        this.body = body;
        return post(path);
    }

    public Http post(String path) {
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

    private Http request() {
        HttpURLConnection http = null;
        try {
            URL url = new URL(host + path);
            URLConnection connection = url.openConnection();
            http = (HttpURLConnection) connection;
            http.setRequestProperty("Content-Type", contentType);
            http.setRequestProperty("Accept", "application/json, text/plain, */*");
            if (isUsedToken) {
                if (token.length() > 0)
                    http.setRequestProperty("Authorization", token);
                else {
                    token = "bearer " + KeyCloakSteps.getUserToken();
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
            status = http.getResponseCode();
            if(status >= 400 && is == null)
                responseMessage = "";
            else
                responseMessage = IOUtils.toString(is, StandardCharsets.UTF_8);
            http.disconnect();
            if (responseMessage.length() > 10000)
                log.debug("RESPONSE: {} ...", responseMessage.substring(0, 10000));
            else
                log.debug("RESPONSE: {}", responseMessage);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(String.format("Ошибка отправки http запроса %s. \nОшибка: %s\nСтатус: %s", (host + path), e.getMessage(), status));
        }
        return this;
    }

    public Http assertStatus(int s) {
        if (s != status) {
            Allure.addAttachment("REQUEST", host + path + "\n\n" + stringPrettyFormat(body));
            Allure.addAttachment("RESPONSE", stringPrettyFormat(responseMessage));
        }
        assertEquals(String.format("\nResponse: %s\nRequest: %s\n%s\n", responseMessage, host + path, body), s, status);
        return this;
    }

    public int status() {
        return status;
    }

    public JSONObject toJson() {
        try {
            return (JSONObject) new JSONObject(toString());
        } catch (Exception e) {
            throw new Error(e.getMessage());
        }
    }

    public JSONArray toJsonArray() {
        try {
            return (JSONArray) new JSONArray(toString());
        } catch (Exception e) {
            throw new Error(e.getMessage());
        }
    }

    public JsonPath jsonPath() {
        try {
            return new JsonPath(toString());
        } catch (Exception e) {
            throw new Error(e.getMessage());
        }
    }

    public String toString() {
        return responseMessage;
    }


}
