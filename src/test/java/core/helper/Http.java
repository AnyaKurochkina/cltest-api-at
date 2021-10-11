package core.helper;

import io.qameta.allure.Allure;
import io.restassured.path.json.JsonPath;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import steps.keyCloak.KeyCloakSteps;


import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
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
import java.util.concurrent.Semaphore;

import static core.helper.JsonHelper.stringPrettyFormat;
import static org.junit.Assert.fail;
import static tests.Tests.putAttachLog;
//import static tests.Tests.putLog;

@Log4j2
public class Http {
    private final String host;
    private String path;
    private String body = "";
    private String method;
    private String token = "";
    private String contentType = "application/json";
    private boolean isUsedToken = true;
    private static final Semaphore SEMAPHORE = new Semaphore(4, true);

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

    public Response get(String path) {
        this.method = "GET";
        this.path = path;
        return request();
    }

    public Response get(String path, String body) {
        this.body = body;
        return get(path);
    }

    public Response delete(String path) {
        this.method = "DELETE";
        this.path = path;
        return request();
    }

    public Response patch(String path) {
        this.method = "PATCH";
        this.path = path;
        return request();
    }

    public Response patch(String path, JSONObject body) {
        this.body = body.toString();
        return patch(path);
    }

    public Http setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public Response post(String path, JSONObject body) {
        this.body = body.toString();
        return post(path);
    }

    public Response post(String path, String body) {
        this.body = body;
        return post(path);
    }

    public Response post(String path) {
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

    private Response request() {
        HttpURLConnection http = null;
        String responseMessage = null;
        StringBuilder sbLog = new StringBuilder();
        int status = 0;
        try {
            URL url = new URL(host + path);
            if(path.endsWith("/cost") || path.contains("order-service"))
                SEMAPHORE.acquire();
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
            sbLog.append(String.format("URL: %s\n", (host + path)));
            if (body.length() > 0) {
                sbLog.append(String.format("REQUEST: %s\n", stringPrettyFormat(body)));
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
            if(path.endsWith("/cost") || path.contains("order-service"))
                SEMAPHORE.release();
            if (responseMessage.length() > 10000)
                sbLog.append(String.format("RESPONSE: %s ...\n\n", stringPrettyFormat(responseMessage.substring(0, 10000))));
            else
                sbLog.append(String.format("RESPONSE: %s\n\n", stringPrettyFormat(responseMessage)));
            log.debug(sbLog.toString());
//            AllureLifecycle allureLifecycle = getLifecycle();
//            Attachment attachment = new Attachment().setSource(sbLog.toString()).setName(String.valueOf(new Date()));
//            allureLifecycle.getCurrentTestCaseOrStep().ifPresent(id -> allureLifecycle.updateStep(id, s -> s.getAttachments().add(attachment)));
//            Allure.getLifecycle().updateTestCase((t) -> {
//                StatusDetails  statusDetails = t.getStatusDetails();
//                if(statusDetails == null)
//                    statusDetails = new StatusDetails();
//                String message = statusDetails.getMessage();
//                if(message == null)
//                    message = "";
//                t.setStatusDetails( statusDetails.setMessage(message + sbLog));
//            });
            putAttachLog(sbLog.toString());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(String.format("Ошибка отправки http запроса %s. \nОшибка: %s\nСтатус: %s", (host + path), e.getMessage(), status));
        }
        return new Response(status, responseMessage);
    }

    public static class StatusResponseException extends RuntimeException {
        public StatusResponseException(String errorMessage) {
            super(errorMessage);
        }
    }

    public class Response {
        int status;
        String responseMessage;

        public Response(int status, String responseMessage) {
            this.status = status;
            this.responseMessage = responseMessage;
        }

        public Response assertStatus(int s) {
            if (s != status() || (path.endsWith("/orders") && method.equals("POST"))) {
                Allure.addAttachment("REQUEST", host + path + "\n\n" + stringPrettyFormat(body));
                Allure.addAttachment("RESPONSE", stringPrettyFormat(responseMessage));
            }
            if(s != status())
                throw new StatusResponseException(String.format("\nexpected:<%d>\nbut was:<%d>\nResponse: %s\nRequest: %s\n%s\n",s, status(), responseMessage, host + path, body));
            return this;
        }

        public int status() {
            return status;
        }

        public JSONObject toJson() {
            try {
                return new JSONObject(toString());
            } catch (Exception e) {
                throw new Error(e.getMessage());
            }
        }

        public JSONArray toJsonArray() {
            try {
                return new JSONArray(toString());
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


}
