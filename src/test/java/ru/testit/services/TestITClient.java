package ru.testit.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import core.helper.Http;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import ru.testit.model.request.*;
import ru.testit.model.response.ConfigurationResponse;
import ru.testit.model.response.CreateTestItemResponse;
import ru.testit.model.response.GetTestItemResponse;
import ru.testit.model.response.StartLaunchResponse;
import ru.testit.properties.AppProperties;
import ru.testit.utils.Outcome;

import java.util.List;
import java.util.TimeZone;


@Log4j2
public class TestITClient {
    public final static AppProperties properties = new AppProperties();
    public static StartLaunchResponse startLaunchResponse = new StartLaunchResponse();

    private static ObjectMapper getObjectMapper() {
        return new ObjectMapper().setTimeZone(TimeZone.getTimeZone("GMT+3"));
    }

    public static String getProjectID() {
        return TestITClient.properties.getProjectID();
    }

    public static String getConfigurationId() {
        return TestITClient.properties.getConfigurationId();
    }

    @SneakyThrows
    public static CloseableHttpClient getHttpClient() {
        return HttpClients
                .custom()
                .setSSLContext(new SSLContextBuilder().loadTrustMaterial(null, TrustAllStrategy.INSTANCE).build())
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .build();
    }

    //старт тест-рана
    public static void startLaunch() {
        String body;
        Http.Response response;
        try {
            final StartTestRunRequest request = new StartTestRunRequest();
            request.setProjectId(getProjectID());
            body = getObjectMapper().writeValueAsString(request);
            response = new Http(properties.getUrl())
                    .disableAttachmentLog()
                    .setSourceToken("PrivateToken " + properties.getPrivateToken())
                    .body(body)
                    .post("/api/v2/testRuns")
                    .assertStatus(201);
            startLaunchResponse = response.extractAs(StartLaunchResponse.class);
        } catch (Throwable e) {
            log.error(e.toString());
            return;
        }
        log.info("[{}] Response :{}\nRequest :{}", response.status(), response.toString(), body);
    }


    public void sendTestItemsUniqueTest(final CreateTestItemRequest createTestRequest) {
        final GetTestItemResponse getTestItemResponse = this.getTestItem(createTestRequest);
        if (getTestItemResponse == null || StringUtils.isBlank(getTestItemResponse.getId())) {
//                createTestRequest.setExternalId(createTestRequest.getExternalId().replaceAll("#(\\d+)$", ""));
            this.createTestItem(createTestRequest);
        } else {
            if (createTestRequest.getOutcome().equals(Outcome.FAILED)) {
                createTestRequest.setId(getTestItemResponse.getId());
                createTestRequest.setName(getTestItemResponse.getName());
                createTestRequest.setExternalId(getTestItemResponse.getExternalId());
                createTestRequest.setDescription(getTestItemResponse.getDescription());
                createTestRequest.setNameSpace(getTestItemResponse.getNameSpace());
                createTestRequest.setClassName(getTestItemResponse.getClassName());
                createTestRequest.setLabels(getTestItemResponse.getLabels());
                createTestRequest.setSetUp(getTestItemResponse.getSetUp());
                createTestRequest.setSteps(getTestItemResponse.getSteps());
                createTestRequest.setTearDown(getTestItemResponse.getTearDown());
                createTestRequest.setProjectId(getTestItemResponse.getProjectId());
                createTestRequest.setTitle(getTestItemResponse.getTitle());
            }
            this.updatePostItem(createTestRequest, getTestItemResponse.getId());
        }
    }

    //получение конфигурации по ид
    public ConfigurationResponse getConfiguration(String configurationId) {
        Http.Response response;
        try {
            response = new Http(properties.getUrl())
                    .disableAttachmentLog()
                    .setSourceToken("PrivateToken " + properties.getPrivateToken())
                    .get("/api/v2/configurations/{}", configurationId)
                    .assertStatus(200);
        } catch (Throwable e) {
            log.error(e.toString());
            throw e;
        }
        log.info("[{}] Response :{}", response.status(), response.toString());
//        if (configurationResponse != null && StringUtils.isNotBlank((CharSequence)createTestItemResponse.getId())) {
//            this.linkAutoTestWithTestCase(createTestItemResponse.getId(), new LinkAutoTestRequest(createTestItemRequest.getTestPlanId()));
//        }
        return response.extractAs(ConfigurationResponse.class);
    }


    @SneakyThrows
    //получение автотеста по ид
    public GetTestItemResponse getTestItem(final CreateTestItemRequest createTestItemRequest) {
        Http.Response response;
        try {
            response = new Http(properties.getUrl())
                    .disableAttachmentLog()
                    .setSourceToken("PrivateToken " + properties.getPrivateToken())
                    .get("/api/v2/autoTests?projectId={}&externalId={}", properties.getProjectID(), createTestItemRequest.getExternalId())
                    .assertStatus(200);
            final CollectionType collectionType = getObjectMapper().getTypeFactory().constructCollectionType(List.class, GetTestItemResponse.class);
            final List<GetTestItemResponse> listTestItems = getObjectMapper().readValue(response.toString(), collectionType);
            if (!listTestItems.isEmpty()) {
                log.info("[{}] Response :{}", response.status(), response.toString());
                return listTestItems.get(0);
            }
        } catch (Throwable e) {
            log.error(e.toString());
            throw e;
        }
        log.info("[{}] Response :{}", response.status(), response.toString());
        return null;
    }

    private String filterTestName(String name){
        return name.replaceAll("(\\(super=\\w+\\(\\w+\\)[,.\\-\\s\\w]+\\))", "");
    }

    //создание автотеста
    public void createTestItem(final CreateTestItemRequest createTestItemRequest) {
        String body;
        Http.Response response;
        CreateTestItemResponse createTestItemResponse = null;
        try {
            createTestItemRequest.setTitle(filterTestName(createTestItemRequest.getTitle()));
            createTestItemRequest.setName(filterTestName(createTestItemRequest.getName()));
            body = getObjectMapper().writeValueAsString(createTestItemRequest);
            response = new Http(properties.getUrl())
                    .disableAttachmentLog()
                    .setSourceToken("PrivateToken " + properties.getPrivateToken())
                    .body(body)
                    .post("/api/v2/autoTests")
                    .assertStatus(201);
            createTestItemResponse = response.extractAs(CreateTestItemResponse.class);
        } catch (Throwable e) {
            log.error(e.toString());
            return;
        }
        log.info("[{}] Response :{}\nRequest :{}", response.status(), response.toString(), body);

        if (createTestItemResponse != null && StringUtils.isNotBlank(createTestItemResponse.getId())) {
            this.linkAutoTestWithTestCase(createTestItemResponse.getId(), new LinkAutoTestRequest(createTestItemRequest.getTestPlanId()));
        }
    }

    //обновление автотеста
    public void updatePostItem(final CreateTestItemRequest createTestItemRequest, final String testId) {
        createTestItemRequest.setId(testId);
        String body;
        Http.Response response;
        try {
            createTestItemRequest.setTitle(filterTestName(createTestItemRequest.getTitle()));
            createTestItemRequest.setName(filterTestName(createTestItemRequest.getName()));
            body = getObjectMapper().writeValueAsString(createTestItemRequest);
            response = new Http(properties.getUrl())
                    .disableAttachmentLog()
                    .setSourceToken("PrivateToken " + properties.getPrivateToken())
                    .body(body)
                    .put("/api/v2/autoTests")
                    .assertStatus(204);
        } catch (Throwable e) {
            log.error(e.toString());
            return;
        }
        log.info("[{}] Response :{}\nRequest :{}", response.status(), response.toString(), body);
        if (StringUtils.isNotBlank(createTestItemRequest.getTestPlanId())) {
            this.linkAutoTestWithTestCase(testId, new LinkAutoTestRequest(createTestItemRequest.getTestPlanId()));
        }
    }

    private void linkAutoTestWithTestCase(final String autoTestId, final LinkAutoTestRequest linkAutoTestRequest) {
        if(linkAutoTestRequest.getId() == null)
            return;
        String body;
        Http.Response response;
        try {
            body = getObjectMapper().writeValueAsString(linkAutoTestRequest);
            response = new Http(properties.getUrl())
                    .disableAttachmentLog()
                    .setSourceToken("PrivateToken " + properties.getPrivateToken())
                    .body(body)
                    .post("/api/v2/autoTests/{}/workItems", autoTestId)
                    .assertStatus(204);
        } catch (Throwable e) {
            log.error(e.toString());
            return;
        }
        log.info("[{}] Response :{}\nRequest :{}", response.status(), response.toString(), body);
    }

    public void finishLaunch(final TestResultsRequest request) {
//        this.sendTestResult(request);
        this.sendCompleteTestRun();
    }

    public static String sendAttachment(Attachment attachment, String testResultId) {
        Http.Response response;
        try {
            response = new Http(properties.getUrl())
                    .disableAttachmentLog()
                    .setSourceToken("PrivateToken " + properties.getPrivateToken())
                    .multiPart("/api/v2/testResults/" + testResultId + "/attachments", "file", attachment.getFileName(), attachment.getBytes())
                    .assertStatus(200);
            log.info("[{}] Response :{}", response.status(), response.toString());
            return response.toString();
        } catch (Throwable e) {
            log.error(e.toString());
            throw e;
        }
    }

    @SneakyThrows
    public static String sendTestResult(final TestResultsRequest request) {
        String body;
        Http.Response response;
        try {
            List<TestResultRequest> list = request.getTestResults();
            body = getObjectMapper().writeValueAsString(list);
            response = new Http(properties.getUrl())
                    .disableAttachmentLog()
                    .setSourceToken("PrivateToken " + properties.getPrivateToken())
                    .body(body)
                    .post("/api/v2/testRuns/{}/testResults", startLaunchResponse.getId())
                    .assertStatus(200);
        } catch (Throwable e) {
            log.error(e.toString());
            throw e;
        }
        log.info("[{}] Response :{}\nRequest :{}", response.status(), response.toString(), body);
        return core.helper.StringUtils.findByRegex("([\\w-]+)", response.toString());
    }


    public static void sendStartTestRun() {
        Http.Response response;
        try {
            response = new Http(properties.getUrl())
                    .disableAttachmentLog()
                    .setSourceToken("PrivateToken " + properties.getPrivateToken())
                    .body("")
                    .post("/api/v2/testRuns/{}/start", startLaunchResponse.getId())
                    .assertStatus(204);
        } catch (Throwable e) {
            log.error(e.toString());
            return;
        }
        log.info("[{}] Response :{}", response.status(), response.toString());
    }

    public void sendCompleteTestRun() {
        Http.Response response;
        try {
            response = new Http(properties.getUrl())
                    .disableAttachmentLog()
                    .setSourceToken("PrivateToken " + properties.getPrivateToken())
                    .body("")
                    .post("/api/v2/testRuns/{}/complete", startLaunchResponse.getId())
                    .assertStatus(204);
        } catch (Throwable e) {
            log.error(e.toString());
            return;
        }
        log.info("[{}] Response :{}", response.status(), response.toString());
    }

    @AddLink
    public static void addLink(final LinkItem linkItem) {
    }

}
