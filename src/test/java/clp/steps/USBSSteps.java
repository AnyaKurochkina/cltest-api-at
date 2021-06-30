package clp.steps;

import clp.core.exception.CustomException;
import clp.core.helpers.Configurier;
import clp.core.helpers.JsonHelper;
import clp.core.helpers.NetworkUtils;
import clp.core.messages.HttpMessage;
import clp.core.messages.Message;
import clp.core.testdata.PrepareBody;
import clp.core.testdata.Templater;
import clp.core.vars.LocalThead;
import clp.core.vars.TestVars;
import com.jayway.jsonpath.JsonPath;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.ru.Тогда;
import io.cucumber.datatable.DataTable;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import org.apache.velocity.runtime.parser.ParseException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static clp.core.helpers.JsonHelper.testValues;
import static clp.steps.SystemCommonSteps.getValueFromJsonPath;
import static io.restassured.RestAssured.baseURI;
import static org.junit.Assert.assertTrue;

public class USBSSteps {
    private static final Logger log = LoggerFactory.getLogger(USBSSteps.class);
    private Scenario scenario;
    private Configurier configer = Configurier.getInstance();
    private static final String FILE = "file:";
    private static final String STUB_ALIASES_PROP="stub.aliases";
    private static final String HEADER_PROPERTIES_FILE="header.properties";
    private static final String TEMPLATE_PROPERTIES_FILE="template.properties";


    @Before
    public void beforeScenario(final Scenario scenario) throws CustomException {
        this.scenario = scenario;
        TestVars testVars = new TestVars();
        LocalThead.setTestVars(testVars);
        configer.loadApplicationPropertiesForSegment();
        createAllurePropertyFile();

    }

    @After
    public void afterScenario() {
        LocalThead.setTestVars(null);
    }


    //Этот метод нужен для отображения ENVIRONMENT в отчете allure
    public void createAllurePropertyFile() {
        String path = "target/allure-results";
        try {
            Properties props = new Properties();
            FileOutputStream fos = new FileOutputStream(path + "/environment.properties");
            props.setProperty("Environment", System.getProperty("env"));
            props.store(fos, "See https://github.com");
            fos.close();
        } catch (Exception ex) {
            log.error("IO problem");
            ex.printStackTrace();
        }
    }

    @Тогда("^Получение Token для пользователя$")
    public void getTokenRest(DataTable dataTable) throws IOException, org.json.simple.parser.ParseException {

        TestVars testVars = LocalThead.getTestVars();
        String testNum = SystemCommonSteps.getTagName();

        JsonHelper.getAllTestDataValues(testNum + ".json", "Токен" );  // Читаем тестовые данные для получения токена

        baseURI = Configurier.getInstance().getAppProp("host_kk");
        Map<String, String> account = dataTable.asMap(String.class, String.class);

        RestAssured.defaultParser = Parser.JSON;

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
                .formParam("username", account.get("username"))
                .formParam("password", account.get("password"))
                .when()
                .post();

        String jsonTokenVal = getValueFromJsonPath(response.asString(), "access_token");
        testVars.setVariables("access_token", jsonTokenVal);
        String jsonTokenType = getValueFromJsonPath(response.asString(), "token_type");
        testVars.setVariables("token_type", jsonTokenType);

    }

    @Тогда("^Получение токена для пользователя$")
    public void AuthHttp(DataTable dataTable) throws IOException, ParseException, CustomException {

        TestVars testVars = LocalThead.getTestVars();
        String testNum = SystemCommonSteps.getTagName();
        log.debug(testNum);

        String endPoint = Configurier.getInstance().getAppProp("host_kk");   // Читаем ендпоинт кейклок из конфигурационного файла
        JsonHelper.getAllTestDataValues(testNum + ".json", "Токен" );  // Читаем тестовые данные для получения токена

        Map<String, String> map = dataTable.asMap(String.class, String.class);
      
        Message message;
        if (checkVars(endPoint)) {
            endPoint = endPoint.replace("${env}", configer.getEnviroment().toLowerCase());
        }
        if (!testValues.isEmpty()) {

            String filledBody = "client_id=" + testValues.get("client_id") + "&client_secret="+ testValues.get("client_secret") + "&grant_type=" + testValues.get("grant_type") + "&username=" + map.get("username") + "&password=" + map.get("password");
            log.info("filledBody=" + filledBody);
            message = new Message(filledBody);
        } else {
            message = new Message("");
        }

        message.setHeader("Content-Type","application/x-www-form-urlencoded");  // Собираем заголовок
        if(checkVars(endPoint)) {
            endPoint = replaceTestVariableValue(endPoint, testVars);
        }

        testVars.setResponse(NetworkUtils.sendHttp(message, endPoint));       // Отправляем запрос

        String messagebody = testVars.getResponse().getBody();                // Получаем ответ
        log.debug("Get response with body: {}", messagebody);

        String jsonTokenVal = getValueFromJsonPath(messagebody, "access_token");
        testVars.setVariables("access_token", jsonTokenVal);                // Записываем токен в переменную
        String jsonTokenType = getValueFromJsonPath(messagebody, "token_type");
        testVars.setVariables("token_type", jsonTokenType);                 // Записываем тип токена в переменную
        log.debug(String.format("Variable with value %s stored to %s", jsonTokenType, "token_type"));
        log.debug(String.format("Variable with value %s stored to %s", jsonTokenVal, "access_token"));

    }

    @Тогда("^Заказ продукта \"([^\"]*)\" в проекте ([^\\s]*)")
    public void RhelOrder(String product, String project, DataTable dataTable) throws IOException, org.json.simple.parser.ParseException {
    
        baseURI = Configurier.getInstance().getAppProp("host");
        String datafolder = Configurier.getInstance().getAppProp("data.folder");

        TestVars testVars = LocalThead.getTestVars();
        String token = testVars.getVariable("token");
        String tokenType = testVars.getVariable("token_type");
        String bearerToken = tokenType + " " + token;

        Map<String, String> order = dataTable.asMap(String.class, String.class);

        org.json.simple.parser.JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader(datafolder + "/" + product.toLowerCase() + ".json"));
        JSONObject request =  (JSONObject) obj;
        // Дополнительные настройки продукта
        com.jayway.jsonpath.JsonPath.parse(request).set("$.order.count", Integer.parseInt(order.get("count")));
        com.jayway.jsonpath.JsonPath.parse(request).set("$.order.attrs.default_nic.net_segment", order.get("net_segment"));
        JsonPath.parse(request).set("$.order.attrs.platform", order.get("platform"));

        System.out.println(request);
//
//        Response response = RestAssured
//                .given()
//                .contentType("application/json; charset=UTF-8")
//                .header("Authorization", bearerToken)
//                .header("Content-Type", "application/json")
//                .body(request)
//                .when()
//                .post("order-service/api/v1/projects/" + project + "/orders");
//
//        assertTrue("Код ответа не равен 201", response.statusCode() == 201);

    }

    @Тогда("^Послать HTTP запрос ?(.*) в эндпоинт ([^\\s]*)$")
    public void sendHttp(String bodyFile, String endPoint, DataTable dataTable) throws IOException, ParseException, CustomException {
        TestVars testVars = LocalThead.getTestVars();
        Message message;
        if (!bodyFile.equals("")) {
            String body = new PrepareBody(this.scenario.getUri().replaceFirst("file:", ""), bodyFile).loadBody();
            String filledBody = new Templater(body, testVars.getVariables()).fillTemplate();
            message = new Message(filledBody);
        } else {
            message = new Message("");
        }

        Map<String, String> headers = dataTable.asMap(String.class, String.class);
        for(Map.Entry<String,String> entry : headers.entrySet()) {
            message.setHeader(entry.getKey(), entry.getValue());
      }
        if(checkVars(endPoint)) {
            endPoint = replaceTestVariableValue(endPoint, testVars);
        }

        testVars.setResponse(NetworkUtils.sendHttp(message, endPoint));

        log.debug("Get response with body: {}", testVars.getResponse().getBody());
        LocalThead.setTestVars(testVars);
    }

    @Тогда("^Послать HTTP запрос ?(.*) в эндпоинт ([^\\s]*) c дефолтными заголовками$")
    public void sendHttp(String bodyFile, String endPoint) throws IOException, ParseException, CustomException {
        TestVars testVars = LocalThead.getTestVars();
        Message message;
        if (checkVars(endPoint)) {
            endPoint = endPoint.replace("${env}", configer.getEnviroment().toLowerCase());
        }
        if (!bodyFile.equals("")) {
            String body = new PrepareBody(this.scenario.getUri().replaceFirst("file:", ""), bodyFile).loadBody();
            String filledBody = new Templater(body, testVars.getVariables()).fillTemplate();
            message = new Message(filledBody);
        } else {
            message = new Message("");
        }
        message.setHeader("Content-Type","text/xml");
        if(checkVars(endPoint)) {
            endPoint = replaceTestVariableValue(endPoint, testVars);
        }
        testVars.setResponse(NetworkUtils.sendHttp(message, endPoint));
        log.debug("Get response with body: {}", testVars.getResponse().getBody());
        LocalThead.setTestVars(testVars);
    }

    @Тогда("Проверить что код ответа ([\\d+]*)")
    public void checkResponseStatus(String responseCode) {
        TestVars testVars = LocalThead.getTestVars();
        HttpMessage response = (HttpMessage) testVars.getResponse();
        assertTrue(String.format("ResponseCode doesn't match %s, its value is %d", responseCode, response.getStatusCode()), Integer.parseInt(responseCode) == response.getStatusCode());
        LocalThead.setTestVars(testVars);
    }


    @Тогда("Сохранить значение заголовка ([^\\s]*) из ответного сообщения в переменную ([^\\s]*)")
    public void saveHeaderAsVar(String headerName, String varName) {
        TestVars testVars = LocalThead.getTestVars();
        String headValue = testVars.getResponse().getHeaderValue(headerName);
        testVars.setVariables(varName, headValue);
        log.debug("Saved variable {}, with value {}", varName, headValue);
        LocalThead.setTestVars(testVars);
    }

    private Map<String,String> getMapfromProperties(String propertiesName) throws CustomException {
       PrepareBody prep = new PrepareBody(this.scenario.getUri().replaceFirst(FILE, ""), propertiesName);
       prep.cutPath();
       Properties properties = new Properties();
       HashMap<String,String> map = new HashMap<>();

        try {
            properties.load(new InputStreamReader(new FileInputStream(prep.getFilePath())));
            for(final String name:properties.stringPropertyNames()){
                map.put(name,properties.getProperty(name));
            }
        } catch (IOException e) {
            throw new CustomException(String.format("Файл: %s не найден по пути %s",propertiesName,prep.getFilePath()));
        }

        return map;
    }

    public boolean checkVars(String entry) {
        return entry.contains("$") ? true : false;
    }

    public boolean checkVars(List<String> entryList) {
        for(String entry : entryList) {
            if(entry.contains("$")) {
                return true;
            }
        }
        return false;
    }

    public String replaceTestVariableValue(String oldValue, TestVars testVars) {
        String[] vars = oldValue.split("\\$");
        String finalEntry = oldValue;
        for (int i = 1; i < vars.length; i++) {
            String var = vars[i].substring(vars[i].indexOf('{') + 1, vars[i].indexOf('}'));
            finalEntry = finalEntry.replace("${"+var+"}", testVars.getVariables().get(var));
        }
        return finalEntry;
    }

}

