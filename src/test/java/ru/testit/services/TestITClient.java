package ru.testit.services;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import ru.testit.model.request.*;
import ru.testit.model.response.ConfigurationResponse;
import ru.testit.model.response.CreateTestItemResponse;
import ru.testit.model.response.GetTestItemResponse;
import ru.testit.model.response.StartLaunchResponse;
import ru.testit.properties.AppProperties;
import ru.testit.utils.Outcome;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.TimeZone;



@Log4j2
public class TestITClient
{
//    private static final Logger log;
    public static AppProperties properties;
    public static StartLaunchResponse startLaunchResponse;
    
    public TestITClient() {
    }
    
    private static ObjectMapper getObjectMapper(){
        return  new ObjectMapper().setTimeZone(TimeZone.getTimeZone("GMT+3"));
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
    
    public void startLaunch() {
        final HttpPost post = new HttpPost(TestITClient.properties.getUrl() + "/api/v2/testRuns");
        post.addHeader("Authorization", "PrivateToken " + TestITClient.properties.getPrivateToken());
        try {
            final StartTestRunRequest request = new StartTestRunRequest();
            request.setProjectId(getProjectID());
            final StringEntity requestEntity = new StringEntity(getObjectMapper().writeValueAsString((Object)request), ContentType.APPLICATION_JSON);
            post.setEntity((HttpEntity)requestEntity);
            final CloseableHttpClient httpClient = getHttpClient();
            try {
                final CloseableHttpResponse response = httpClient.execute((HttpUriRequest)post);
                try {

                    //////////////////
                    String str = EntityUtils.toString(response.getEntity());
                    TestITClient.log.info(IOUtils.toString(requestEntity.getContent(), StandardCharsets.UTF_8) +
                            " :: " + requestEntity + " :: " + str);
                    //////////////////

                    this.startLaunchResponse = (StartLaunchResponse)getObjectMapper().readValue(str, (Class)StartLaunchResponse.class);
                    if (response != null) {
                        response.close();
                    }
                }
                catch (Throwable t) {
                    if (response != null) {
                        try {
                            response.close();
                        }
                        catch (Throwable t2) {
                            t.addSuppressed(t2);
                        }
                    }
                    throw t;
                }
                if (httpClient != null) {
                    httpClient.close();
                }
            }
            catch (Throwable t3) {
                if (httpClient != null) {
                    try {
                        httpClient.close();
                    }
                    catch (Throwable t4) {
                        t3.addSuppressed(t4);
                    }
                }
                throw t3;
            }
        }
        catch (IOException e) {
            TestITClient.log.error("Exception while starting test run", (Throwable)e);
        }
    }
    
//    public void sendTestItems(final Collection<CreateTestItemRequest> createTestRequests) {
//        for (final CreateTestItemRequest createTestRequest : createTestRequests) {
//            final GetTestItemResponse getTestItemResponse = this.getTestItem(createTestRequest);
//            if (getTestItemResponse == null || StringUtils.isBlank((CharSequence)getTestItemResponse.getId())) {
////                createTestRequest.setExternalId(createTestRequest.getExternalId().replaceAll("#(\\d+)$", ""));
//                this.createTestItem(createTestRequest);
//            }
//            else {
//                if (createTestRequest.getOutcome().equals(Outcome.FAILED)) {
//                    createTestRequest.setId(getTestItemResponse.getId());
//                    createTestRequest.setName(getTestItemResponse.getName());
//                    createTestRequest.setExternalId(getTestItemResponse.getExternalId());
//                    createTestRequest.setDescription(getTestItemResponse.getDescription());
//                    createTestRequest.setNameSpace(getTestItemResponse.getNameSpace());
//                    createTestRequest.setClassName(getTestItemResponse.getClassName());
//                    createTestRequest.setLabels(getTestItemResponse.getLabels());
//                    createTestRequest.setSetUp(getTestItemResponse.getSetUp());
//                    createTestRequest.setSteps(getTestItemResponse.getSteps());
//                    createTestRequest.setTearDown(getTestItemResponse.getTearDown());
//                    createTestRequest.setProjectId(getTestItemResponse.getProjectId());
//                    createTestRequest.setTitle(getTestItemResponse.getTitle());
//                }
//                this.updatePostItem(createTestRequest, getTestItemResponse.getId());
//            }
//        }
//    }

    public void sendTestItemsUniqueTest(final CreateTestItemRequest createTestRequest) {
            final GetTestItemResponse getTestItemResponse = this.getTestItem(createTestRequest);
            if (getTestItemResponse == null || StringUtils.isBlank((CharSequence)getTestItemResponse.getId())) {
//                createTestRequest.setExternalId(createTestRequest.getExternalId().replaceAll("#(\\d+)$", ""));
                this.createTestItem(createTestRequest);
            }
            else {
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

    public ConfigurationResponse getConfiguration(String configurationId) {
        final HttpGet get = new HttpGet(TestITClient.properties.getUrl() + "/api/v2/configurations/" + configurationId);
        get.addHeader("Authorization", "PrivateToken " + TestITClient.properties.getPrivateToken());
        ConfigurationResponse configurationResponse = null;
        try {
            final CloseableHttpClient httpClient = getHttpClient();
            try {
                final CloseableHttpResponse response = httpClient.execute((HttpUriRequest)get);
                try {

                    //////////////////
                    String str = EntityUtils.toString(response.getEntity());
                    TestITClient.log.info(get + " :: " + str);
                    //////////////////

                    configurationResponse = (ConfigurationResponse)getObjectMapper().readValue(str, (Class)ConfigurationResponse.class);
                    if (response != null) {
                        response.close();
                    }
                }
                catch (Throwable t) {
                    if (response != null) {
                        try {
                            response.close();
                        }
                        catch (Throwable t2) {
                            t.addSuppressed(t2);
                        }
                    }
                    throw t;
                }
                if (httpClient != null) {
                    httpClient.close();
                }
            }
            catch (Throwable t3) {
                if (httpClient != null) {
                    try {
                        httpClient.close();
                    }
                    catch (Throwable t4) {
                        t3.addSuppressed(t4);
                    }
                }
                throw t3;
            }
        }
        catch (IOException e) {
            TestITClient.log.error("Exception while sending test item", (Throwable)e);
        }
//        if (configurationResponse != null && StringUtils.isNotBlank((CharSequence)createTestItemResponse.getId())) {
//            this.linkAutoTestWithTestCase(createTestItemResponse.getId(), new LinkAutoTestRequest(createTestItemRequest.getTestPlanId()));
//        }
        return configurationResponse;
    }







    public GetTestItemResponse getTestItem(final CreateTestItemRequest createTestItemRequest) {

//        createTestItemRequest.setExternalId(createTestItemRequest.getExternalId().replaceAll("#(\\d+)$", ""));


        final HttpGet get = new HttpGet(TestITClient.properties.getUrl() + "/api/v2/autoTests?projectId=" + TestITClient.properties.getProjectID() + "&externalId=" + createTestItemRequest.getExternalId());
        get.addHeader("Authorization", "PrivateToken " + TestITClient.properties.getPrivateToken());
        GetTestItemResponse getTestItemResponse = null;
        try {
            final CloseableHttpClient httpClient = getHttpClient();
            try {
                final CloseableHttpResponse response = httpClient.execute((HttpUriRequest)get);
                try {

                    //////////////////
                    String str = EntityUtils.toString(response.getEntity());
                    TestITClient.log.info(get + " :: " + str);
                    //////////////////

                    final TypeFactory typeFactory = getObjectMapper().getTypeFactory();
                    final CollectionType collectionType = typeFactory.constructCollectionType((Class)List.class, (Class)GetTestItemResponse.class);
                    final List<GetTestItemResponse> listTestItems = (List<GetTestItemResponse>)getObjectMapper().readValue(str, (JavaType)collectionType);
                    if (!listTestItems.isEmpty()) {
                        getTestItemResponse = listTestItems.get(0);
                    }
                    if (response != null) {
                        response.close();
                    }
                }
                catch (Throwable t) {
                    if (response != null) {
                        try {
                            response.close();
                        }
                        catch (Throwable t2) {
                            t.addSuppressed(t2);
                        }
                    }
                    throw t;
                }
                if (httpClient != null) {
                    httpClient.close();
                }
            }
            catch (Throwable t3) {
                if (httpClient != null) {
                    try {
                        httpClient.close();
                    }
                    catch (Throwable t4) {
                        t3.addSuppressed(t4);
                    }
                }
                throw t3;
            }
        }
        catch (IOException e) {
            TestITClient.log.error("Exception while sending test item", (Throwable)e);
        }
        return getTestItemResponse;
    }
    
    public void createTestItem(final CreateTestItemRequest createTestItemRequest) {
//        createTestItemRequest.setExternalId(createTestItemRequest.getExternalId().replaceAll("#(\\d+)$", ""));
        final HttpPost post = new HttpPost(TestITClient.properties.getUrl() + "/api/v2/autoTests");
        post.addHeader("Authorization", "PrivateToken " + TestITClient.properties.getPrivateToken());
        CreateTestItemResponse createTestItemResponse = null;
        try {
            final StringEntity requestEntity = new StringEntity(getObjectMapper().writeValueAsString((Object)createTestItemRequest), ContentType.APPLICATION_JSON);
            post.setEntity((HttpEntity)requestEntity);
            final CloseableHttpClient httpClient = getHttpClient();
            try {
                final CloseableHttpResponse response = httpClient.execute((HttpUriRequest)post);
                try {

                    //////////////////
                    String str = EntityUtils.toString(response.getEntity());
                    TestITClient.log.info(IOUtils.toString(requestEntity.getContent(), StandardCharsets.UTF_8) +
                            " :: " + requestEntity + " :: " + str);
                    //////////////////

                    createTestItemResponse = (CreateTestItemResponse)getObjectMapper().readValue(str, (Class)CreateTestItemResponse.class);
                    if (response != null) {
                        response.close();
                    }
                }
                catch (Throwable t) {
                    if (response != null) {
                        try {
                            response.close();
                        }
                        catch (Throwable t2) {
                            t.addSuppressed(t2);
                        }
                    }
                    throw t;
                }
                if (httpClient != null) {
                    httpClient.close();
                }
            }
            catch (Throwable t3) {
                if (httpClient != null) {
                    try {
                        httpClient.close();
                    }
                    catch (Throwable t4) {
                        t3.addSuppressed(t4);
                    }
                }
                throw t3;
            }
        }
        catch (IOException e) {
            TestITClient.log.error("Exception while sending test item", (Throwable)e);
        }
        if (createTestItemResponse != null && StringUtils.isNotBlank((CharSequence)createTestItemResponse.getId())) {
            this.linkAutoTestWithTestCase(createTestItemResponse.getId(), new LinkAutoTestRequest(createTestItemRequest.getTestPlanId()));
        }
    }
    
    public void updatePostItem(final CreateTestItemRequest createTestItemRequest, final String testId) {
//        createTestItemRequest.setExternalId(createTestItemRequest.getExternalId().replaceAll("#(\\d+)$", ""));
        createTestItemRequest.setId(testId);
        final HttpPut put = new HttpPut(TestITClient.properties.getUrl() + "/api/v2/autoTests");
        put.addHeader("Authorization", "PrivateToken " + TestITClient.properties.getPrivateToken());
        final CreateTestItemResponse createTestItemResponse = null;
        try {
            final StringEntity requestEntity = new StringEntity(getObjectMapper().writeValueAsString((Object)createTestItemRequest), ContentType.APPLICATION_JSON);
            put.setEntity((HttpEntity)requestEntity);
            final CloseableHttpClient httpClient = getHttpClient();
            try {
                final CloseableHttpResponse response = httpClient.execute((HttpUriRequest)put);

                //////////
                String res = response.toString();
                if(response.getEntity() != null)
                    res += EntityUtils.toString(response.getEntity());
                TestITClient.log.info(IOUtils.toString(requestEntity.getContent(), StandardCharsets.UTF_8) +
                        " :: " + requestEntity + " :: " + res);
                ///////////

                final Throwable t2 = null;
                if (response != null) {
                    if (t2 != null) {
                        try {
                            response.close();
                        }
                        catch (Throwable t3) {
                            t2.addSuppressed(t3);
                        }
                    }
                    else {
                        response.close();
                    }
                }
                if (httpClient != null) {
                    httpClient.close();
                }
            }
            catch (Throwable t4) {
                if (httpClient != null) {
                    try {
                        httpClient.close();
                    }
                    catch (Throwable t5) {
                        t4.addSuppressed(t5);
                    }
                }
                throw t4;
            }
        }
        catch (IOException e) {
            TestITClient.log.error("Exception while sending test item", (Throwable)e);
        }
        if (StringUtils.isNotBlank((CharSequence)createTestItemRequest.getTestPlanId())) {
            this.linkAutoTestWithTestCase(testId, new LinkAutoTestRequest(createTestItemRequest.getTestPlanId()));
        }
    }
    
    private void linkAutoTestWithTestCase(final String autoTestId, final LinkAutoTestRequest linkAutoTestRequest) {
        final HttpPost post = new HttpPost(TestITClient.properties.getUrl() + "/api/v2/autoTests/" + autoTestId + "/workItems");
        post.addHeader("Authorization", "PrivateToken " + TestITClient.properties.getPrivateToken());
        try {
            final StringEntity requestEntity = new StringEntity(getObjectMapper().writeValueAsString((Object)linkAutoTestRequest), ContentType.APPLICATION_JSON);
            post.setEntity((HttpEntity)requestEntity);
            final CloseableHttpClient httpClient = getHttpClient();
            try {
                final CloseableHttpResponse response = httpClient.execute((HttpUriRequest)post);

                //////////////
                String res = response.toString();
                if(response.getEntity() != null)
                    res += EntityUtils.toString(response.getEntity());
                TestITClient.log.info(IOUtils.toString(requestEntity.getContent(), StandardCharsets.UTF_8) +
                        " :: " + requestEntity + " :: " + res);
                /////////////


                final Throwable t2 = null;
                if (response != null) {
                    if (t2 != null) {
                        try {
                            response.close();
                        }
                        catch (Throwable t3) {
                            t2.addSuppressed(t3);
                        }
                    }
                    else {
                        response.close();
                    }
                }
                if (httpClient != null) {
                    httpClient.close();
                }
            }
            catch (Throwable t4) {
                if (httpClient != null) {
                    try {
                        httpClient.close();
                    }
                    catch (Throwable t5) {
                        t4.addSuppressed(t5);
                    }
                }
                throw t4;
            }
        }
        catch (IOException e) {
            TestITClient.log.error("Exception while linking auto test", (Throwable)e);
        }
    }
    
    public void finishLaunch(final TestResultsRequest request) {
//        this.sendTestResult(request);
        this.sendCompleteTestRun();
    }
    
    public static void sendTestResult(final TestResultsRequest request) {
        final HttpPost post = new HttpPost(TestITClient.properties.getUrl() + "/api/v2/testRuns/" + startLaunchResponse.getId() + "/testResults");
        post.addHeader("Authorization", "PrivateToken " + TestITClient.properties.getPrivateToken());
        try {
            List<TestResultRequest> list = request.getTestResults();
            final StringEntity requestEntity = new StringEntity(getObjectMapper().writeValueAsString((Object)list), ContentType.APPLICATION_JSON);
            post.setEntity((HttpEntity)requestEntity);
            final CloseableHttpClient httpClient = getHttpClient();
            try {
                final CloseableHttpResponse response = httpClient.execute((HttpUriRequest)post);

                /////////////////
                String res = response.toString();
                if(response.getEntity() != null)
                    res += EntityUtils.toString(response.getEntity());
                TestITClient.log.info(IOUtils.toString(requestEntity.getContent(), StandardCharsets.UTF_8) +
                        " :: " + requestEntity + " :: " + res);
                ////////////////

                final Throwable t2 = null;
                if (response != null) {
                    if (t2 != null) {
                        try {
                            response.close();
                        }
                        catch (Throwable t3) {
                            t2.addSuppressed(t3);
                        }
                    }
                    else {
                        response.close();
                    }
                }
                if (httpClient != null) {
                    httpClient.close();
                }
            }
            catch (Throwable t4) {
                if (httpClient != null) {
                    try {
                        httpClient.close();
                    }
                    catch (Throwable t5) {
                        t4.addSuppressed(t5);
                    }
                }
                throw t4;
            }
        }
        catch (IOException e) {
            TestITClient.log.error("Exception while sending test result", (Throwable)e);
        }
    }
    
    public void sendCompleteTestRun() {
        final HttpPost post = new HttpPost(TestITClient.properties.getUrl() + "/api/v2/testRuns/" + startLaunchResponse.getId() + "/complete");
        post.addHeader("Authorization", "PrivateToken " + TestITClient.properties.getPrivateToken());
        try {
            final StringEntity requestEntity = new StringEntity(getObjectMapper().writeValueAsString((Object)""), ContentType.APPLICATION_JSON);
            post.setEntity((HttpEntity)requestEntity);
            final CloseableHttpClient httpClient = getHttpClient();
            try {
                final CloseableHttpResponse response = httpClient.execute((HttpUriRequest)post);

                /////////////////
                String res = response.toString();
                if(response.getEntity() != null)
                    res += EntityUtils.toString(response.getEntity());
                TestITClient.log.info(IOUtils.toString(requestEntity.getContent(), StandardCharsets.UTF_8) +
                        " :: " + requestEntity + " :: " + res);
                ////////////////

                final Throwable t2 = null;
                if (response != null) {
                    if (t2 != null) {
                        try {
                            response.close();
                        }
                        catch (Throwable t3) {
                            t2.addSuppressed(t3);
                        }
                    }
                    else {
                        response.close();
                    }
                }
                if (httpClient != null) {
                    httpClient.close();
                }
            }
            catch (Throwable t4) {
                if (httpClient != null) {
                    try {
                        httpClient.close();
                    }
                    catch (Throwable t5) {
                        t4.addSuppressed(t5);
                    }
                }
                throw t4;
            }
        }
        catch (IOException e) {
            TestITClient.log.error("Exception while sending complete test run", (Throwable)e);
        }
    }
    
    @AddLink
    public static void addLink(final LinkItem linkItem) {
    }
    
    static {
//        log = LoggerFactory.getLogger((Class)TestITClient.class);
        TestITClient.properties = new AppProperties();
    }
}
