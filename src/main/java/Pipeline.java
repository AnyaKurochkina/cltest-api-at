import io.restassured.RestAssured;
import io.restassured.config.SSLConfig;
import io.restassured.path.json.JsonPath;
import org.apache.http.conn.ssl.SSLSocketFactory;
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

public class Pipeline {
    final static AppProperties properties = new AppProperties();
    final static String TEST_RUN_ID = "testRunId";
    final static String pathTestResourcesDir = Paths.get("src/test/resources").toAbsolutePath().toString();

    @SuppressWarnings("deprecation")
    public static void main(String[] args) throws Exception {
        SSLSocketFactory clientAuthFactory = new SSLSocketFactory(new SSLContextBuilder().loadTrustMaterial(null, TrustAllStrategy.INSTANCE).build());
        SSLConfig config = new SSLConfig().with().sslSocketFactory(clientAuthFactory).and().allowAllHostnames();
        Set<String> externalIds = new HashSet<>();

        Map<String, String> argsMap = Arrays.stream(args).map(e -> e.split("=")).collect(Collectors.toMap(s -> s[0], s -> s[1]));
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
            String command = "-Dsecret=123456 -Denv=IFT -Dtest=" + String.join(",", externalIds);
            try (PrintWriter writerCommand = new PrintWriter(new BufferedWriter(new FileWriter("run.sh", false)))) {
                writerCommand.println("mvn clean install -DskipTests=false " + command);
                System.out.println("COMMAND_LINE: " + command);
            }
        }

    }
}
