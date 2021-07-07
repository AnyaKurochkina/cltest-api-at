package clp.steps;

import clp.core.exception.CustomException;
import clp.core.helpers.Configurier;
import clp.core.vars.LocalThead;
import clp.core.vars.TestVars;
import clp.core.utils.Waiting;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.ru.Если;
import cucumber.api.java.ru.Тогда;

import io.restassured.RestAssured;
import io.restassured.path.json.exception.JsonPathException;
import io.restassured.response.Response;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class OrderServiceSteps extends Specifications {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceSteps.class);
    private static final String FILE = "file:";
    private static final String folder_logs = Configurier.getInstance().getAppProp("folder.logs");
    private Scenario scenario;
    private Configurier configer = Configurier.getInstance();

    @Before
    public void beforeScenario(final Scenario scenario) throws CustomException {
        this.scenario = scenario;
        TestVars testVars = new TestVars();
        LocalThead.setTestVars(testVars);
        configer.loadApplicationPropertiesForSegment();
        for (File myFile : new File(folder_logs).listFiles()) {
            if (myFile.isFile()) myFile.delete();
        }
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
        String order_id = "";
        log.info("Отправка запроса на создание заказа для " + product);
        Response response = RestAssured
                .given()
                .spec(getRequestSpecificationKong())
                .body(request)
                .when()
                .post("order-service/api/v1/projects/" + project + "/orders");
        assertTrue("Код ответа не равен 201", response.statusCode() == 201);

        /*ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true);
        List <clp.models.response.postOrders.ResponseItem> resp = objectMapper.readValue(response.asString(), new TypeReference<List<clp.models.response.postOrders.ResponseItem>>(){});
        for (int i = 0; i < resp.size(); i++) {
            order_id = resp.get(i).getId();
        }*/

        order_id = response.jsonPath().get("[0].id");
        testVars.setVariables("order_id", order_id);
        testVars.setResp(response);

    }

    @Если("^Статус заказа - ([^\\s]*)$")
    public void CheckOrderStatus(String exp_status) throws CustomException {
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
        assertTrue("Код ответа не равен 200", response.statusCode() == 200);

        testVars.setVariables("action_id", response.jsonPath().get("action_id"));

    }

    @Если("^Статус выполнения последнего действия - ([^\\s]*)$")
    public void CheckActionStatus(String exp_status) throws CustomException {
        TestVars testVars = LocalThead.getTestVars();
        String order_id = testVars.getVariable("order_id");
        String action_id = testVars.getVariable("action_id");
        String action_status = "";
        int counter = 10;

        log.info("Проверка статуса выполнения действия");
        while ((action_status.equals("pending") ||action_status.equals("")) && counter > 0) {
            Waiting.sleep(60000);
            try {
                action_status = RestAssured
                        .given()
                        .spec(getRequestSpecificationKong())
                        .when()
                        .get("order-service/api/v1/projects/" + testVars.getVariable("project") + "/orders/" + order_id + "/actions/history/" + action_id)
                        .jsonPath()
                        .get("status");
            } catch (JsonPathException e) {
                log.error("Error get status " + e.getMessage());
            };
            counter = counter - 1;
        }

        if (!action_status.equals(exp_status.toLowerCase())) {
            StateServiceSteps.GetErrorFromOrch(order_id);
        }
    }


    protected String getItemIdByOrderId(String action) {
        TestVars testVars = LocalThead.getTestVars();
        Actions actions = new Actions();
        String order_id = testVars.getVariable("order_id");
        String type = actions.getTypeByAction(action);
        log.info("Получение item_id для " + action);
        Response response = RestAssured
                .given()
                .spec(getRequestSpecificationKong())
                .when()
                .get("order-service/api/v1/projects/" + testVars.getVariable("project") + "/orders/" + order_id);
        return response.jsonPath().get("data.find{it.type == '" + type +"'}.item_id");

    }


    private String getStatusByOrderId(Response resp) {
        String status = "";
        try {
            status = resp.jsonPath().get("status");
        } catch (JsonPathException e){
            log.error(e.getMessage());
        }
        return status;
    }


}
