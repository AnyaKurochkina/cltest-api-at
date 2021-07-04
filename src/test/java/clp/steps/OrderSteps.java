package clp.steps;

import clp.core.exception.CustomException;
import clp.core.helpers.Configurier;
import clp.core.vars.LocalThead;
import clp.core.vars.TestVars;
import clp.core.utils.Waiting;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.ru.Тогда;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OrderSteps extends Specifications {

    private static final Logger log = LoggerFactory.getLogger(OrderSteps.class);
    private static final String FILE = "file:";
    private Scenario scenario;
    private Configurier configer = Configurier.getInstance();

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

        TestVars testVars = LocalThead.getTestVars();
        testVars.setVariables("project", project);
        JSONObject request = TemplateSteps.getRequest(product);

        log.info("Отправка запроса на создание заказа для " + product);
        Response response = RestAssured
                .given()
                .spec(getRequestSpecificationKong())
                .body(request)
                .when()
                .post("order-service/api/v1/projects/" + project + "/orders");

        assertTrue("Код ответа не равен 201", response.statusCode() == 201);
        String order_id = response.jsonPath().get("[0].id");
        testVars.setVariables("order_id", order_id);
        testVars.setResp(response);

    }

    @Тогда("^статус заказа - ([^\\s]*)$")
    public void CheckOrderStatus(String exp_status) throws ParseException {
        TestVars testVars = LocalThead.getTestVars();
        String order_id = testVars.getVariable("order_id");
        String status = "";
        int counter = 10;

        log.info("Проверка статуса заказа");
        while ((status.equals("pending") || status.equals("") || status.equals("changing")) && counter > 0) {

            Waiting.sleep(120000);
            Response response = RestAssured
                    .given()
                    .spec(getRequestSpecificationKong())
                    .when()
                    .get("order-service/api/v1/projects/" + testVars.getVariable("project") + "/orders/" + order_id);

            status = getStatusByOrderId(response).toLowerCase();
            counter = counter - 1;
            System.out.println("counter = " + counter);
        }

        if (!status.equals(exp_status.toLowerCase())) {
            StateServiceSteps.GetErrorFromOrch(order_id);
        }

    }

    @Тогда("^Выполнить действие - ([^\\s]*)$")
    public void ExecuteAction(String action) throws IOException, ParseException {

        TestVars testVars = LocalThead.getTestVars();

        JSONObject request = TemplateSteps.getActionRequest(action);
        log.info("Отправка запроса на выполнение действия - " + action);
        Response response = RestAssured
                .given()
                .spec(getRequestSpecificationKong())
                .body(request)
                .when()
                .patch("order-service/api/v1/projects/" + testVars.getVariable("project") + "/orders/"+ testVars.getVariable("order_id") +"/actions/"+ action);
        System.out.println("response = " + response.getBody().asString());
        assertTrue("Код ответа не равен 200", response.statusCode() == 200);

    }


    protected String getItemIdByOrderId() {
        TestVars testVars = LocalThead.getTestVars();
        return testVars.getResp().jsonPath().get("[0].attrs.preview_items[0].item_id");
    }


    private String getStatusByOrderId(Response resp) {
        return resp.jsonPath().get("status");
    }
}
