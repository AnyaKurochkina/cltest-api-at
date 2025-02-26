package core.helper.http;

import core.enums.Role;
import core.helper.DataFileHelper;
import core.helper.StringUtils;
import core.utils.Waiting;
import io.restassured.RestAssured;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.SSLConfig;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.SpecificationQuerier;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.ssl.SSLContextBuilder;
import org.json.JSONObject;
import org.junit.TestsExecutionListener;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import ru.testit.services.TestITClient;
import steps.keyCloak.KeyCloakSteps;

import java.io.File;
import java.net.ConnectException;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static core.helper.JsonHelper.stringPrettyFormat;

@Log4j2
public class Http {
    public static SSLConfig sslConfig;
    String host;
    String path;
    String body = "";
    String method;
    String token = "";
    private String field = "";
    Role role;
    String contentType = "application/json";
    private boolean isUsedToken = true;
    private static final Semaphore SEMAPHORE = new Semaphore(1, true);
    private String fileName;
    private byte[] bytes;
    private static final String boundary = "-83lmsz7nREiFUSFOC3d5RyOivB-NiG6_JoSkts";
    boolean isLogged = true;
    private static final InheritableThreadLocal<Role> fixedRole = new InheritableThreadLocal<>();
    Map<String, String> headers = new HashMap<>();
    Map<String, String> pathParams = new HashMap<>();
    Map<String, String> queryParams = new HashMap<>();

    static {
        org.apache.http.conn.ssl.SSLSocketFactory clientAuthFactory = null;
        try {
            clientAuthFactory = new org.apache.http.conn.ssl.SSLSocketFactory(new SSLContextBuilder().loadTrustMaterial(null, TrustAllStrategy.INSTANCE).build());
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            e.printStackTrace();
        }
        sslConfig = new SSLConfig().with().sslSocketFactory(clientAuthFactory).and().allowAllHostnames();
    }

    public Http(String host) {
        this.host = host;
    }

    private Http() {}

    public static Http builder() {
        return new Http();
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

    public Response get(String path, Object... args) {
        this.method = "GET";
        for (Object arg : args) {
            path = path.replaceFirst("\\{}", Objects.requireNonNull(arg).toString()
                    .replaceAll("#", "%23")
                    .replaceAll(" ", "%20"));
        }
        this.path = StringUtils.format(path, args);
        return request();
    }

    public Response getOrThrow(String path, Object... args) throws ConnectException {
        return get(path, args);
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

    @SneakyThrows
    public Response api(Path api, Object... args) {
        this.host = api.url;
        this.method = api.method.toString();
        this.path = api.path;
        int i = 0;
        Pattern pattern = Pattern.compile("\\{([^}]*)}");
        Matcher matcher = pattern.matcher(path.replaceFirst("\\?(.*)", ""));
        while (matcher.find()) {
            pathParams.put(matcher.group(1), args[i].toString());
            i++;
        }
        for (Object o : args) {
            if (o instanceof QueryBuilder) {
                queryParams = ((QueryBuilder) o).toMap();
            }
            if (o instanceof Attachment) {
                setContentType("multipart/form-data; boundary=" + boundary);
                this.field = ((Attachment) o).getField();
                this.bytes = ((Attachment) o).getBytes();
                this.fileName = ((Attachment) o).getFileName();
            }
        }
        Response request = request();
        request.assertStatus(api.status);
        return request;
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
        setContentType("multipart/form-data; boundary=" + boundary);
        this.field = field;
        this.bytes = Files.readAllBytes(file.toPath());
        this.fileName = file.getName();
        return post(path);
    }

    public Response multiPart(String path, String field, String fileName, byte[] bytes) {
        setContentType("multipart/form-data; boundary=" + boundary);
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

    public Http setProjectId(String projectId, Role role) {
        if (!isFixedRole())
            this.token = "bearer " + KeyCloakSteps.getServiceAccountToken(projectId, role);
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
                Waiting.sleep(5000);
                continue;
            }
            if (response.status() == 502 && method.equals("GET")) {
                Waiting.sleep(5000);
                continue;
            }
            break;
        }
        if (Objects.isNull(response)) {
            Waiting.sleep(5000);
            response = filterRequest();
        }
        return response;
    }

    @SneakyThrows
    @SuppressWarnings("deprecation")
    Response filterRequest() {
        Assertions.assertFalse(host.isEmpty(), "Не задан host");
        int status = 0;
//        host = StringUtils.findByRegex("(.*//[^/]*)/", host + path);
//        path = url.getFile();
        RequestSpecification specification = null;
        io.restassured.response.Response response = null;
        try {
            RequestSpecBuilder build = new RequestSpecBuilder();
            build.setBaseUri(host);
            build.build();

            specification = RestAssured.given()
                    .spec(build.build())
                 //   .filter(new SwaggerCoverage())
                    .config(RestAssured.config().sslConfig(sslConfig).httpClient(HttpClientConfig.httpClientConfig()
                            .setParam(CoreConnectionPNames.CONNECTION_TIMEOUT, 120000)
                            .setParam(CoreConnectionPNames.SO_TIMEOUT, 120000)))
                    .contentType(contentType)
                    .headers(headers)
                    .header("Accept", "application/json, text/plain, */*");

            if (isUsedToken) {
                if (isFixedRole())
                    token = "bearer " + KeyCloakSteps.getUserToken(fixedRole.get());
                if (token.isEmpty()) {
                    Assertions.assertNotNull(role, "Не задана роль для запроса");
                    token = "bearer " + KeyCloakSteps.getUserToken(role);
                }
                specification.header("Authorization", token);
            }
            SEMAPHORE.tryAcquire(1, TimeUnit.MINUTES);
            if (!field.isEmpty()) {
                String mimeType = URLConnection.guessContentTypeFromName(fileName);
                if (Objects.isNull(mimeType))
                    mimeType = "application/octet-stream";
                specification.multiPart(new MultiPartSpecBuilder(bytes)
                        .fileName(fileName)
                        .controlName(field)
                        .mimeType(mimeType)
                        .build());
//                specification.multiPart(field, fileName, bytes);

            }
            if (body.length() > 0)
                specification.body(body);

            if(!pathParams.isEmpty())
                specification.pathParams(pathParams);
            if(!queryParams.isEmpty())
                specification.queryParams(queryParams);

//            specification.params(getParamsUrl(StringUtils.findByRegex("\\?(.*)", path)));
            String params = StringUtils.findByRegex("\\?(.*)", path);
            List<String> values;
            for (String key : getParamsUrl(params)) {
                values = URLEncodedUtils.parse(params, StandardCharsets.UTF_8).stream().filter(s -> s.getName().equals(key)).map(NameValuePair::getValue).collect(Collectors.toList());
                specification.queryParam(key, values);
            }

            String pathWithoutParameters = path.replaceFirst("\\?.*", "");
            switch (method) {
                case "POST":
                    response = specification.post(pathWithoutParameters);
                    break;
                case "PUT":
                    response = specification.put(pathWithoutParameters);
                    break;
                case "DELETE":
                    response = specification.delete(pathWithoutParameters);
                    break;
                case "PATCH":
                    response = specification.patch(pathWithoutParameters);
                    break;
                case "GET":
                default:
                    response = specification.get(pathWithoutParameters);
            }

            if (isLogged)
                log.debug(String.format("%s URL: %s\n", method, SpecificationQuerier.query(specification).getURI()));
            if (field.length() == 0) {
                if (body.length() > 0 || method.equals("POST")) {
                    if (isLogged)
                        log.debug(String.format("REQUEST: %s\n", stringPrettyFormat(body)));
                }
            }
        } catch (Throwable e) {
            if (e instanceof ConnectException)
                throw e;
            if (response != null)
                status = response.getStatusCode();
            Assertions.fail(String.format("Ошибка отправки http запроса (%s) %s. \nОшибка: %s\nСтатус: %s", role, SpecificationQuerier.query(specification).getURI(), e, status));
        } finally {
            SEMAPHORE.release();
            if (response != null)
                if (response.getTime() > 1000)
                    if (!((host + path).contains(TestITClient.properties.getUrl())))
                        DataFileHelper.appendToFile(TestsExecutionListener.responseTimeLog,
                                String.format("[%s ms] %s %s (%s)\n", response.getTime(), method, SpecificationQuerier.query(specification).getURI(), response.getHeader("x-request-id")));
        }
        if (isLogged)
            log.debug(String.format("RESPONSE (%d) (%s): %s\n\n", Objects.requireNonNull(response).getStatusCode(), response.getHeader("x-request-id"), response.getBody().asPrettyString()));
        return new Response(response, this);
    }

    @SneakyThrows
    public static Set<String> getParamsUrl(String params) {
        return URLEncodedUtils.parse(params, StandardCharsets.UTF_8).stream().map(NameValuePair::getName).collect(Collectors.toSet());
    }

//    public static class ConnectException extends AssertionError {
//        public ConnectException(String errorMessage) {
//            super(errorMessage);
//        }
//
//    }
}
