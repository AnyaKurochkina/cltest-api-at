package clp.steps;

import clp.core.exception.CustomException;
import clp.core.helpers.Configurier;
import clp.core.helpers.JsonHelper;
import clp.core.vars.LocalThead;
import clp.core.vars.TestVars;
import static clp.core.helpers.JsonHelper.testValues;

import cucumber.api.Scenario;
import cucumber.api.java.Before;
import cucumber.api.java.ru.Тогда;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import static io.restassured.RestAssured.baseURI;

import org.json.simple.parser.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;


public class AuthSteps {

    private static final Logger log = LoggerFactory.getLogger(OrderSteps.class);
    private Scenario scenario;
    private Configurier configer = Configurier.getInstance();
    private static final String URL_KK = Configurier.getInstance().getAppProp("host_kk");

    @Before
    public void beforeScenario(final Scenario scenario) throws CustomException {
        this.scenario = scenario;
        TestVars testVars = new TestVars();
        LocalThead.setTestVars(testVars);
        configer.loadApplicationPropertiesForSegment();
    }

    @Тогда("^Получение Token$")
    public void getToken() throws IOException, ParseException {
        TestVars testVars = LocalThead.getTestVars();
        baseURI = URL_KK;
        RestAssured.useRelaxedHTTPSValidation();
        JsonHelper.getAllTestDataValues("token" + ".json", "Токен" );  // Читаем тестовые данные для получения токена
        log.info("Получение токена");
        Response response = RestAssured
                .given()
                .config(RestAssured.config()
                        .encoderConfig(EncoderConfig.encoderConfig()
                                .encodeContentTypeAs("x-www-form-urlencoded", ContentType.URLENC)))
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .formParam("client_id", testValues.get("client_id"))
                .formParam("client_secret", testValues.get("client_secret"))
                .formParam("grant_type", testValues.get("grant_type"))
                .formParam("username", testValues.get("username"))
                .formParam("password", testValues.get("password"))
                .when()
                .post();

        String token = response.getBody().jsonPath().getString("access_token");
        testVars.setVariables("token", token);
        String token_type = response.jsonPath().get("token_type");
        testVars.setVariables("token_type", token_type);

    }

    public static String getBearerToken() {
        TestVars testVars = LocalThead.getTestVars();
        String token = testVars.getVariable("token");
        String tokenType = testVars.getVariable("token_type");
        String bearerToken = tokenType + " " + token;
        return bearerToken;
    }
}
