import io.restassured.RestAssured;
import io.restassured.config.SSLConfig;
import io.restassured.path.json.JsonPath;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.json.JSONObject;
import ru.testit.properties.AppProperties;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static ru.testit.properties.AppProperties.TEST_IT_TOKEN;

public class Pipeline {
    final static AppProperties properties = new AppProperties();
    final static String TEST_RUN_ID = "testRunId";
    final static String TEST_PLAN_ID = "testPlanId";
//    final static String TEST_SECRET = "Secret";

    final static String pathTestResourcesDir = Paths.get("src/test/resources").toAbsolutePath().toString();
    static String ENV = "prod";

    @SuppressWarnings("deprecation")
    public static void main(String[] args) throws Exception {
        org.apache.http.conn.ssl.SSLSocketFactory clientAuthFactory = new org.apache.http.conn.ssl.SSLSocketFactory(new SSLContextBuilder().loadTrustMaterial(null, TrustAllStrategy.INSTANCE).build());
        SSLConfig config = new SSLConfig().with().sslSocketFactory(clientAuthFactory).and().allowAllHostnames();
        Set<String> externalIds = new HashSet<>();
        Map<String, String> argsMap = Arrays.stream(args).map(e -> e.split("=")).filter(s -> s.length > 1)
                .collect(Collectors.toMap(s -> s[0], s -> s[1]));

        if (System.getProperty(TEST_IT_TOKEN) != null)
            properties.setPrivateToken(System.getProperty(TEST_IT_TOKEN));
        if (argsMap.containsKey(TEST_IT_TOKEN))
            properties.setPrivateToken(argsMap.get(TEST_IT_TOKEN));

        String testPlanName = null;
        String threadCount = "";
        if ((argsMap.get(TEST_PLAN_ID)) != null) {
            JsonPath path = RestAssured.given()
                    .config(RestAssured.config().sslConfig(config))
                    .header("Authorization", "PrivateToken " + properties.getPrivateToken())
                    .get(properties.getUrl() + "/api/v2/testPlans/" + argsMap.get(TEST_PLAN_ID))
                    .then()
                    .statusCode(200)
                    .extract()
                    .response()
                    .jsonPath();
            testPlanName = path.getString("name");
            List<String> tags = path.getList("tags.name");
            threadCount = tags.stream().filter(s -> s.startsWith("thread_count=")).findFirst().orElse("thread_count=").substring(13);
            if (threadCount.length() > 0) {
                threadCount = " -Djunit.jupiter.execution.parallel.config.fixed.parallelism=" + threadCount;
            }
        }

        setEnv(testPlanName, Arrays.asList("dev", "ift"));

        JsonPath jsonPath = RestAssured.given()
                .config(RestAssured.config().sslConfig(config))
                .header("Authorization", "PrivateToken " + properties.getPrivateToken())
                .get(properties.getUrl() + "/api/v2/testRuns/" + argsMap.get(TEST_RUN_ID))
                .then()
                .statusCode(200)
                .extract()
                .response()
                .jsonPath();

        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(pathTestResourcesDir + "/configurations.txt", false)))) {
            List<Map<String, Object>> testResults = jsonPath.getList("testResults");
            for (Map<String, Object> resultMap : testResults) {
                JSONObject result = new JSONObject(resultMap);
                String externalId = (String) result.query("/autoTest/externalId");
                externalIds.add(externalId);
                writer.println(externalId + "=" + result.query("/configuration/id"));
            }
            String command = "-DCI -DfailIfNoTests=false -Dmaven.test.skip=false" + threadCount +
                    " -DtestItToken=" + properties.getPrivateToken() + " -Denv=" + ENV + " -DtestPlanId=" +
                    argsMap.get(TEST_PLAN_ID)  + " -DtestRunId=" +
                    argsMap.get(TEST_RUN_ID) + " -Dtest=" + String.join(",", externalIds);

            System.out.println("##teamcity[setParameter name='env.testArguments' value='" + command + "']");
            System.out.println("##teamcity[publishArtifacts '" + pathTestResourcesDir + "/configurations.txt => configurations']");
        }

    }

    private static void setEnv(String testPlanName, List<String> environments) {
        if (testPlanName == null)
            return;
        String name = testPlanName.toLowerCase();
        for (String env : environments) {
            if (name.contains(env)) {
                ENV = env;
                return;
            }
        }
    }
}
