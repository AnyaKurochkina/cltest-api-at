package clp.steps;

import clp.core.exception.CustomException;
import clp.core.helpers.Configurier;
import clp.core.vars.LocalThead;
import clp.core.vars.TestVars;
import com.jayway.jsonpath.JsonPath;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.ru.Тогда;
import gherkin.deps.com.google.gson.Gson;
import io.cucumber.datatable.DataTable;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static org.junit.Assert.assertTrue;

public class CustomSteps {
    private static final Logger log = LoggerFactory.getLogger(CustomSteps.class);
    private Scenario scenario;
    private Configurier configer = Configurier.getInstance();
    private static final String FILE = "file:";
    private static final String URL_KK = Configurier.getInstance().getAppProp("host_kk");
    private static final String URL = Configurier.getInstance().getAppProp("host");


    @Before
    public void beforeScenario(final Scenario scenario) throws CustomException {
        this.scenario = scenario;
        TestVars testVars = new TestVars();
        LocalThead.setTestVars(testVars);
        configer.loadApplicationPropertiesForSegment();
    }

    @After
    public void afterScenario() {
        LocalThead.setTestVars(null);
    }


    @Тогда("^Получение Token под пользователем$")
    public void getToken(DataTable dataTable) throws IOException, ParseException {
        TestVars testVars = LocalThead.getTestVars();
        baseURI = URL_KK;
        Map<String, String> account = dataTable.asMap(String.class, String.class);

        org.json.simple.parser.JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader("./src/test/resources/data/auth.json"));

        JSONObject jsonObject =  (JSONObject) obj;
        jsonObject.put("username", account.get("username"));
        jsonObject.put("password", account.get("password"));

        Map<String, String> map = new Gson().fromJson(jsonObject.toString(),Map.class);
        Response response = RestAssured
                .given()
                .config(RestAssured.config()
                        .encoderConfig(EncoderConfig.encoderConfig()
                                .encodeContentTypeAs("x-www-form-urlencoded", ContentType.URLENC)))
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .formParam("client_id", map.get("client_id"))
                .formParam("client_secret", map.get("client_secret"))
                .formParam("grant_type", map.get("grant_type"))
                .formParam("username", map.get("username"))
                .formParam("password", map.get("password"))
                .when()
                .post();

        String token = response.jsonPath().get("access_token");
        testVars.setVariables("token", token);
        String token_type = response.jsonPath().get("token_type");
        testVars.setVariables("token_type", token_type);

    }

    @Тогда("^Заказ продукта RHEL в проекте ([^\\s]*)$")
    public void RhelOrder(String project ,DataTable dataTable) throws IOException, ParseException {
        baseURI = URL;

        TestVars testVars = LocalThead.getTestVars();
        String token = testVars.getVariable("token");
        String tokenType = testVars.getVariable("token_type");
        String bearerToken = tokenType + " " + token;

        Map<String, String> account = dataTable.asMap(String.class, String.class);

        org.json.simple.parser.JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader("./src/test/resources/data/linux/order.json"));
        JSONObject request =  (JSONObject) obj;
        JsonPath.parse(request).set("$.order.count", Integer.parseInt(account.get("count")));
        JsonPath.parse(request).set("$.order.attrs.default_nic.net_segment", account.get("net_segment"));
        JsonPath.parse(request).set("$.order.attrs.platform", account.get("platform"));

        Response response = RestAssured
                .given()
                .contentType("application/json; charset=UTF-8")
                .header("Authorization", bearerToken)
                .header("Content-Type", "application/json")
                .body(request)
                .when()
                .post("order-service/api/v1/projects/proj-k7ua2iq6zh/orders");

        assertTrue("Код ответа не равен 201", response.statusCode() == 201);

    }
}
