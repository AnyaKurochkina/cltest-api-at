package clp.steps;

import clp.core.exception.CustomException;
import clp.core.helpers.Configurier;
import clp.core.helpers.JsonHelper;
import clp.core.vars.LocalThead;
import clp.core.vars.TestVars;
import clp.core.utils.Waiting;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.ru.Тогда;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import static io.restassured.RestAssured.baseURI;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OrderSteps {

    private static final Logger log = LoggerFactory.getLogger(OrderSteps.class);
    private Scenario scenario;
    private Configurier configer = Configurier.getInstance();
    private static final String FILE = "file:";
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


    @Тогда("^Заказ продукта ([^\"]*) в проекте ([^\\s]*)")
    public void CreateOrder(String product, String project) throws IOException, org.json.simple.parser.ParseException {

        RestAssured.useRelaxedHTTPSValidation();
        baseURI = Configurier.getInstance().getAppProp("host");
        TestVars testVars = LocalThead.getTestVars();
        testVars.setVariables("project", project);
        String token = AuthSteps.getBearerToken();
        JSONObject request =  TemplateSteps.getRequest(product);

        log.info("Отправка запроса на создание заказа для");
        Response response = RestAssured
                .given()
                .contentType("application/json; charset=UTF-8")
                .header("Authorization", token)
                .header("Content-Type", "application/json")
                .body(request)
                .when()
                .post("order-service/api/v1/projects/" + project + "/orders");

        assertTrue("Код ответа не равен 201", response.statusCode() == 201);
        String order_id = response.jsonPath().get("[0].attrs.extra_mounts[0].path");
        testVars.setVariables("order_id", order_id);

    }

    @Тогда("^статус заказа - ([^\\s]*)$")
    public void CheckOrderStatus(String exp_status) throws IOException, ParseException {
        RestAssured.useRelaxedHTTPSValidation();
        baseURI = URL;
        TestVars testVars = LocalThead.getTestVars();
        String token = AuthSteps.getBearerToken();
        String order_id = testVars.getVariable("order_id");
        String status = "";
        int counter = 10;

        log.info("Проверка статуса заказа");
        while (status.equals("pending") || status.equals("") && counter > 0) {

            Waiting.sleep(120000);
            Response response = RestAssured
                    .given()
                    .contentType("application/json; charset=UTF-8")
                    .header("Authorization", token)
                    .header("Content-Type", "application/json")
                    .when()
                    .get("order-service/api/v1/orders/" + order_id);
            System.out.println("response = "  + response.getBody().asString());
            status = getStatusByItemId(response).toLowerCase();
            counter = counter - 1;
            System.out.println("counter = "  + counter);
        }

        assertEquals(exp_status.toLowerCase(), status);

    }

    /*@Тогда("^Выполнить действие - ([^\\s]*)$")
    public void ExecuteAction(String action) throws IOException, ParseException {
        RestAssured.useRelaxedHTTPSValidation();
        baseURI = URL;
        TestVars testVars = LocalThead.getTestVars();
        String token = AuthSteps.getBearerToken();
        String order_id = testVars.getVariable("order_id");
        String project = testValues.get("project_name");
        JSONObject request =  TemplateSteps.getActionRequest(order_id);
        Response response = RestAssured
                .given()
                .contentType("application/json; charset=UTF-8")
                .header("Authorization", token)
                .header("Content-Type", "application/json")
                .body(request)
                .when()
                .post("order-service/api/v1/projects/" + project + "/orders/" +order_id+ "/actions/" + action);
        System.out.println("response = "  + response.getBody().asString());

    }*/

    /*private String getItemIdByOrderId(String name) {

        String token = AuthSteps.getBearerToken();

        Response response = RestAssured
                .given()
                .contentType("application/json; charset=UTF-8")
                .header("Authorization", token)
                .header("Content-Type", "application/json")
                .when()
                .get("order-service/api/v1/projects/" + project + "/orders/e3aec8a7-8644-476f-bb5b-69081e3b2375");
        return response.jsonPath().get("list.find{it.name.contains('" + name.toLowerCase() + "')}.category");

    }*/


    private String getStatusByItemId(Response resp) {
        return resp.jsonPath().get("status");
    }
}
