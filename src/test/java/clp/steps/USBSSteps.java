package clp.steps;

import clp.core.helpers.JsonHelper;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.ru.Тогда;
import io.cucumber.datatable.DataTable;
import org.apache.velocity.runtime.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import clp.core.exception.CustomException;
import clp.core.helpers.Configurier;
import clp.core.helpers.NetworkUtils;
import clp.core.messages.HttpMessage;
import clp.core.messages.Message;
import clp.core.testdata.PrepareBody;
import clp.core.testdata.Templater;
import clp.core.vars.LocalThead;
import clp.core.vars.TestVars;
import sun.usagetracker.UsageTrackerClient;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static clp.core.helpers.JsonHelper.testValues;
import static clp.steps.SystemCommonSteps.*;
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

    }

    @After
    public void afterScenario() {
        LocalThead.setTestVars(null);
    }

    @Тогда("^Запрос на получение токена$")
    public void sendHttp() throws IOException {
        String urlParameters  = "client_id=cloud_autotest&client_secret=1b365e15669fc8315245f844c84c4970&grant_type=client_credentials";
        byte[] postData = urlParameters.getBytes( StandardCharsets.UTF_8 );
        int postDataLength = postData.length;
        String request = "http://dev-keycloak.apps.d0-oscp.corp.dev.vtb/auth/realms/Portal/protocol/openid-connect/token";
        URL url = new URL( request );
        HttpURLConnection conn= (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setInstanceFollowRedirects(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("charset", "utf-8");
        conn.setRequestProperty("Content-Length", Integer.toString(postDataLength ));
        conn.setUseCaches(false);

        System.out.println(postDataLength);

        try(DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {

            conn.getOutputStream().write(postData);
            Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            for (int c; (c = in.read()) >= 0;)
                System.out.print((char)c);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Тогда("^Получение токена для пользователя")
    public void AuthHttp(DataTable dataTable) throws IOException, ParseException, CustomException {

        TestVars testVars = LocalThead.getTestVars();
        String tagName = String.valueOf(scenario.getSourceTagNames());
        String testNum = tagName.replaceAll("[^A-Za-zА-Яа-я0-9]", "");
        testVars.setVariables("testNum", testNum);

        log.info(testNum);
        // Читаем ендпоинт кейклок из конфигурационного файла
        String endPoint = Configurier.getInstance().getAppProp("host_kk");
        
        // Читаем тестовые данные для получения токена

        String[] testFields = new String [] {"client_id", "client_secret", "grant_type"};

        JsonHelper.getAllTestDataValues(testNum + ".json", "/json/tests", "Токен" );

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

        message.setHeader("Content-Type","application/x-www-form-urlencoded");
        if(checkVars(endPoint)) {
            endPoint = replaceTestVariableValue(endPoint, testVars);
        }

        testVars.setResponse(NetworkUtils.sendHttp(message, endPoint));

        String messagebody = testVars.getResponse().getBody();
        log.debug("Get response with body: {}", messagebody);

        String jsonTokenVal = getValueFromJsonPath(messagebody, "access_token");
        testVars.setVariables("access_token", jsonTokenVal);
        String jsonTokenType = getValueFromJsonPath(messagebody, "token_type");
        testVars.setVariables("token_type", jsonTokenType);
        log.debug(String.format("Variable with value %s stored to %s", jsonTokenType, "token_type"));
        log.debug(String.format("Variable with value %s stored to %s", jsonTokenVal, "access_token"));

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

