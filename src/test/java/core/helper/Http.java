package core.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.path.json.JsonPath;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.keyCloak.KeyCloakSteps;

import javax.net.ssl.*;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Objects;
import java.util.concurrent.Semaphore;

import static core.helper.JsonHelper.stringPrettyFormat;
import static tests.Tests.putAttachLog;

@Log4j2
public class Http {
    private final String host;
    private String path;
    private String body = "";
    private String method;
    private String token = "";
    private String field = "";
    private String contentType = "application/json";
    private boolean isUsedToken = true;
    private boolean isLogged = true;
    private static final Semaphore SEMAPHORE = new Semaphore(1, true);
    private static final String boundary = "===KhdMbdyfgJSTmdmjBYJNmKstdrB===";
    private File file;

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
            SSLContext sc;
            sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HostnameVerifier allHostsValid = (hostname, session) -> true;
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
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

    public Http disableAttachmentLog() {
        this.isLogged = false;
        return this;
    }

    private static String format(String str, Object ... args){
        for (Object arg : args)
            str = str.replaceFirst("\\{\\}", Objects.requireNonNull(arg).toString());
        return str;
    }

    public Response get(String path, Object ... args) {
        this.method = "GET";
        this.path = format(path, args);
        return request();
    }

    public Response delete(String path, Object ... args) {
        this.method = "DELETE";
        this.path = format(path, args);
        return request();
    }

    public Response patch(String path, Object ... args) {
        this.method = "PATCH";
        this.path = format(path, args);
        return request();
    }

    public Http setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public Http body(Object body) {
        this.body = body.toString();
        return this;
    }

    public Response multiPart(String path, String field, File file) {
        contentType = "multipart/form-data; boundary=" + boundary;
        this.field = field;
        this.file = file;
        return post(path);
    }

    public Response post(String path, Object ... args) {
        this.method = "POST";
        this.path = format(path, args);
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

    @SneakyThrows
    private Response request() {
        Response response = null;
        for (int i = 0; i < 3; i++) {
            response = filterRequest();
            if (!(response.status() == 504 && method.equals("GET")))
                break;
            Thread.sleep(1000);
        }
        return response;
    }

    private final StringBuilder sbLog = new StringBuilder();

    private void log(String str) {
        if (isLogged)
            sbLog.append(str);
    }

    private Response filterRequest() {
        HttpURLConnection http;
        String responseMessage = null;
        int status = 0;
        try {
            URL url = new URL(host + path);
            if (path.endsWith("/cost") || path.contains("order-service"))
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
            log(String.format("%s URL: %s\n", method, (host + path)));
            if (field.length() > 0) {
                addFilePart(http.getOutputStream(), file);
            } else {
                if (body.length() > 0) {
                    log(String.format("REQUEST: %s\n", stringPrettyFormat(body)));
                    http.getOutputStream().write((body.trim()).getBytes(StandardCharsets.UTF_8));
                }
            }
            InputStream is;
            if (http.getResponseCode() >= 400)
                is = http.getErrorStream();
            else
                is = http.getInputStream();
            status = http.getResponseCode();
            if (status >= 400 && is == null)
                responseMessage = "";
            else
                responseMessage = IOUtils.toString(is, StandardCharsets.UTF_8);
            http.disconnect();
            if (responseMessage.length() > 10000)
                log(String.format("RESPONSE: %s ...\n\n", stringPrettyFormat(responseMessage.substring(0, 10000))));
            else
                log(String.format("RESPONSE: %s\n\n", stringPrettyFormat(responseMessage)));
            log.debug(sbLog.toString());
            putAttachLog(sbLog.toString());
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail(String.format("Ошибка отправки http запроса %s. \nОшибка: %s\nСтатус: %s", (host + path), e.getMessage(), status));
        } finally {
            if (path.endsWith("/cost") || path.contains("order-service"))
                SEMAPHORE.release();
        }
        return new Response(status, responseMessage);
    }

    public static class StatusResponseException extends AssertionError {
        public StatusResponseException(String errorMessage) {
            super(errorMessage);
        }
    }

    @SneakyThrows
    public void addFilePart(OutputStream outputStream, File uploadFile) {
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), true);
        String fileName = uploadFile.getName();
        writer.append("--" + boundary)
                .append("\r\n")
                .append("Content-Disposition: form-data; name=\"")
                .append(field)
                .append("\"; filename=\"")
                .append(fileName)
                .append("\"\r\n")
                .append("Content-Type: ")
                .append(URLConnection.guessContentTypeFromName(fileName))
                .append("\r\n")
                .append("Content-Transfer-Encoding: binary\r\n\r\n")
                .flush();

        FileInputStream inputStream = new FileInputStream(uploadFile);
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
        inputStream.close();
        writer.append("\r\n");
        writer.flush();
    }

    public class Response {
        int status;
        String responseMessage;

        public Response(int status, String responseMessage) {
            this.status = status;
            this.responseMessage = responseMessage;
        }

        public Response assertStatus(int s) {
            if (s != status())
                throw new StatusResponseException(String.format("\nexpected:<%d>\nbut was:<%d>\nMethod: %s\nToken: %s\nResponse: %s\nRequest: %s\n%s\n", s, status(), method, token, responseMessage, host + path, body));
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

        @SneakyThrows
        public <T> T extractAs(Class<T> clazz){
            JSONObject jsonObject = new JSONObject(responseMessage);
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.convertValue(jsonObject.toMap(), clazz);
        }

        public String toString() {
            return responseMessage;
        }

    }


}
