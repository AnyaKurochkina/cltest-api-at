package core.helper.http;

import core.enums.Role;
import core.helper.StringUtils;
import core.utils.Waiting;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import steps.keyCloak.KeyCloakSteps;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.Semaphore;

import static core.helper.JsonHelper.stringPrettyFormat;
import static core.helper.http.ModifyHttpURLConnection.addHttpMethods;
import static core.helper.http.ModifyHttpURLConnection.disableHostnameVerifier;

@Log4j2
public class Http {
    String host;
    String path;
    String body = "";
    String method;
    String token = "";
    private String field = "";
    private Role role = Role.ADMIN;
    String contentType = "application/json";
    private boolean isUsedToken = true;
    private static final Semaphore SEMAPHORE = new Semaphore(1, true);
    private static final String boundary = "-83lmsz7nREiFUSFOC3d5RyOivB-NiG6_JoSkts";
    private String fileName;
    private byte[] bytes;
    boolean isLogged = true;
    private static final InheritableThreadLocal<Role> fixedRole = new InheritableThreadLocal<>();
    Map<String, String> headers = new HashMap<>();

    static {
        disableHostnameVerifier();
        addHttpMethods();
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

    public static void setFixedRole(Role role) {
        fixedRole.set(role);
    }

    private boolean isFixedRole() {
        return Objects.nonNull(fixedRole.get());
    }

    public static void removeFixedRole() {
        fixedRole.remove();
    }

    public Http addHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }

    @SneakyThrows
    public Response get(String path, Object... args) {
        this.method = "GET";
        for (Object arg : args)
            path = path.replaceFirst("\\{}", Objects.requireNonNull(arg).toString()
                    .replaceAll("#", "%23")
                    .replaceAll(" ", "%20"));
        this.path = StringUtils.format(path, args);
        return request();
    }

    public Response delete(String path, Object... args) {
        this.method = "DELETE";
        this.path = StringUtils.format(path, args);
        return request();
    }

    public Response patch(String path, Object... args) {
        this.method = "PATCH";
        this.path = StringUtils.format(path, args);
        return request();
    }

    public Response put(String path, Object... args) {
        this.method = "PUT";
        this.path = StringUtils.format(path, args);
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

    @SneakyThrows
    public Response multiPart(String path, String field, File file) {
        contentType = "multipart/form-data; boundary=" + boundary;
        this.field = field;
        this.bytes = Files.readAllBytes(file.toPath());
        this.fileName = file.getName();
        return post(path);
    }

    public Response multiPart(String path, String field, String fileName, byte[] bytes) {
        contentType = "multipart/form-data; boundary=" + boundary;
        this.field = field;
        this.fileName = fileName;
        this.bytes = bytes;
        return post(path);
    }

    public Response post(String path, Object... args) {
        this.method = "POST";
        this.path = StringUtils.format(path, args);
        return request();
    }

    public Http setProjectId(String projectId) {
        if (!isFixedRole())
            this.token = "bearer " + KeyCloakSteps.getServiceAccountToken(projectId);
        return this;
    }

    public Http setSourceToken(String token) {
        this.token = token;
        return this;
    }

    public Http setRole(Role role) {
        this.role = role;
        return this;
    }

    public Http setWithoutToken() {
        isUsedToken = false;
        return this;
    }

    public Http withServiceToken() {
        this.token = "bearer " + KeyCloakSteps.getServiceToken();
        return this;
    }

    private Response request() {
        Response response = null;
        for (int i = 0; i < 3; i++) {
            try {
                response = filterRequest();
            } catch (AssertionFailedError e) {
                Waiting.sleep(6000);
                continue;
            }
            if (response.status() == 504 && method.equals("GET")) {
                Waiting.sleep(2000);
                continue;
            }
            break;
        }
        if (Objects.isNull(response))
            throw new ConnectException(String.format("Ошибка отправки http запроса %s. (Connection refused)", (host + path)));
        return response;
    }

    private Response filterRequest() {
        HttpURLConnection http;
        List<String> responseHeaders = new ArrayList<>();
        String responseMessage = null;
        int status = 0;
        try {
            URL url = new URL(host + path);
            if (path.endsWith("/cost") || path.contains("order-service"))
                SEMAPHORE.acquire();
            URLConnection connection = url.openConnection();
            http = (HttpURLConnection) connection;
            http.setRequestProperty("Content-Type", contentType);
            headers.forEach(http::setRequestProperty);
            http.setRequestProperty("Accept", "application/json, text/plain, */*");
            if (isUsedToken) {
                if (isFixedRole())
                    token = "bearer " + KeyCloakSteps.getUserToken(fixedRole.get());
                if (token.length() == 0)
                    token = "bearer " + KeyCloakSteps.getUserToken(role);
                http.setRequestProperty("Authorization", token);
            }
            http.setDoOutput(true);
            http.setRequestMethod(method);
            if (isLogged)
                log.debug(String.format("%s URL: %s\n", method, (host + path)));
            if (field.length() > 0) {
                addFilePart(http.getOutputStream(), fileName, bytes);
            } else {
                if (body.length() > 0 || method.equals("POST")) {
                    if (isLogged)
                        log.debug(String.format("REQUEST: %s\n", stringPrettyFormat(body)));
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

            String xRequestId = null;
            for (Map.Entry<String, List<String>> entries : http.getHeaderFields().entrySet()) {
                StringJoiner values = new StringJoiner(",");
                for (String value : entries.getValue()) {
                    values.add(value);
                }
                if (entries.getKey() == null)
                    continue;
                if (entries.getKey().equals("x-request-id"))
                    xRequestId = values.toString();
                responseHeaders.add(String.format("\t\t%s: %s", entries.getKey(), values));
            }

            http.disconnect();
            if (isLogged)
                log.debug(String.format("RESPONSE (%s): %s\n\n", xRequestId, stringPrettyFormat(responseMessage)));

        } catch (Exception e) {
            Assertions.fail(String.format("Ошибка отправки http запроса %s. \nОшибка: %s\nСтатус: %s", (host + path), e.getMessage(), status));
        } finally {
            if (path.endsWith("/cost") || path.contains("order-service"))
                SEMAPHORE.release();
        }
        return new Response(status, responseMessage, responseHeaders, this);
    }

//    public Map<String,String> getQueryParams() {
//        String[] params = new URL(host + path).getQuery().split("&");
//        Map<String, String> map = new HashMap<>();
//        String value = null;
//        for (String param : params) {
//            String name = param.split("=")[0];
//            String value = param.split("=")[1];
//            map.put(name, value);
//        }
//        return map;
//    }

    public static class StatusResponseException extends AssertionError {
        @Getter
        int status;

        public StatusResponseException(String errorMessage, int status) {
            super(errorMessage);
            this.status = status;
        }
    }

    public static class ConnectException extends AssertionError {
        public ConnectException(String errorMessage) {
            super(errorMessage);
        }
    }

    @SneakyThrows
    public void addFilePart(OutputStream outputStream, String fileName, byte[] bytes) {
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), true);
        writer.append("--" + boundary)
                .append("\r\n")
                .append("Content-Disposition: form-data; name=\"")
                .append(field)
                .append("\"; filename=\"")
                .append(fileName)
                .append("\"\r\n")
                .append("Content-Type: ")
                .append(/*URLConnection.guessContentTypeFromName(fileName)*/ "application/octet-stream")
                .append("\r\n")
                .append("Content-Transfer-Encoding: binary\r\n\r\n")
                .flush();

        InputStream inputStream = new ByteArrayInputStream(bytes);
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
        inputStream.close();
        writer.append("\r\n--")
                .append(boundary)
                .append("--")
                .flush();
    }
}
