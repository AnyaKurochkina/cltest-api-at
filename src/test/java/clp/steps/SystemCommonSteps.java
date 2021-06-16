package clp.steps;

import clp.core.dbconnector.StatementExecute;
import clp.core.exception.CustomException;
import clp.core.helpers.*;
import clp.core.iso.UniversalRequest;
import clp.core.iso.UniversalRequestBuilder;
import clp.core.iso.core.isorequests.ISOConnection;
import clp.core.iso.core.parse.FieldsMap;
import clp.core.iso.core.requests.ProcessingResponse;
import clp.core.messages.Message;
import clp.core.ssh.SshClient;
import clp.core.testdata.PrepareBody;
import clp.core.testdata.Templater;
import clp.core.testdata.TestData;
import clp.core.testdata.VariableReplacer;
import clp.core.utils.Timer;
import clp.core.utils.Waiting;
import clp.core.vars.LocalThead;
import clp.core.vars.TestVars;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.ru.И;
import cucumber.api.java.ru.Тогда;
import io.cucumber.datatable.DataTable;
import io.qameta.allure.Allure;
import org.apache.commons.io.FilenameUtils;
import org.apache.velocity.runtime.parser.ParseException;
import org.jpos.iso.ISOException;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.junit.Assert.*;


public class SystemCommonSteps {
    private static final Logger log = LoggerFactory.getLogger(SystemCommonSteps.class);
    private Scenario scenario;
    private static final String FILE = "file:";
    private static final String MSG_FOR_SEND = "Message for send";
    private static final String REQUEST = "Request {}";
    private static final String USERNAME = "ssh.user";
    private static final String HOST = "ssh.host";
    private static final String HOST_PORT = "ssh.port";
    private static final String SSH_PASS = "ssh.password";
    private static final String ISO_HOST = "iso.host";
    private static final String ISO_PORT = "iso.port";
    private static final String ROOT_FOLDER = "profile.root.folder";
    private static final String PF_FOLDER = "profile.folder";
    private static final String ACTIVATE_GEN = "/scripts/activateCleanGenerator.sh";
    private static final String CLEAR_LOG = "/sql/checkClearingLog.sql";
    private static final String DRTXNAMT = "DRTXNAMT";
    private static final String CRDNUM = "CRDNUM";
    private static final String CRDNUM_lower = "crdnum";
    private static final Configurier configer = Configurier.getInstance();
    private static final int MON_PERIOD = 15000;

    private static Map<String, String> VarMemory = new HashMap<>();

    @Before
    public void beforeScenario(final Scenario scenario) throws CustomException {
        TestVars testVars = new TestVars();
        LocalThead.setTestVars(testVars);
        this.scenario = scenario;
        log.info("Start test: {}", this.scenario.getUri());
        configer.loadApplicationPropertiesForSegment();
    }

    @After
    public void afterScenario() {
        LocalThead.setTestVars(null);
        log.info("End test: {}", this.scenario.getUri());
    }


    @Тогда("^Вызвать удалённую процедуру с телом ([^\"]*)$")
    public void execCallableStatement(String requestName) throws SQLException, IOException, ParseException, CustomException {
        TestVars testVars = LocalThead.getTestVars();
        String lines = new PrepareBody(this.scenario.getUri().replaceFirst(FILE, ""), requestName).loadBody();
        String filledTempl = new Templater(lines, testVars.getVariables()).fillTemplate();
        Allure.addAttachment(MSG_FOR_SEND, filledTempl);
        log.debug(REQUEST, filledTempl);
        StatementExecute statementExecute = new StatementExecute();
        String response = statementExecute.executeStatement(configer.getApplicationProperties(), filledTempl);
        testVars.setResponse(new Message(response));
        Allure.addAttachment("Response message", response);
        log.debug("Response message {}", response);
        LocalThead.setTestVars(testVars);
        assertNotNull(response);
    }


    @Тогда("^Файл ([^\"]*)? разместить по пути ([a-zA-Z0-9а-яА-Я_/\\.]+)$")
    public void sendFileSftp(String requestName, String sftpPath) throws CustomException, ParseException, IOException {
        TestVars testVars = LocalThead.getTestVars();
        String lines = new PrepareBody(this.scenario.getUri().replaceFirst(FILE, ""), requestName).loadBody();
        String filledTempl = new Templater(lines, testVars.getVariables()).fillTemplate();
        Allure.addAttachment(MSG_FOR_SEND, filledTempl);
        log.debug(REQUEST, filledTempl);
        SftpClient sftpClient = new SftpClient(configer.getAppProp(USERNAME), configer.getAppProp(SSH_PASS), configer.getAppProp(HOST), configer.getAppProp(HOST_PORT));
        assertTrue(sftpClient.sendMessageToSFTP(filledTempl, sftpPath));
        LocalThead.setTestVars(testVars);
    }

    @Тогда("^Получить файл (.*)? по пути ([a-zA-Z0-9а-яА-Я_/\\.]+)$")
    public void getFileSftp(String sftpPath, String requestName) throws CustomException {
        TestVars testVars = LocalThead.getTestVars();
        String parsedRequestName = VariableReplacer.getReplacedString(sftpPath, testVars);
        log.debug("Попытка получения файла");
        SftpClient sftpClient = new SftpClient(configer.getAppProp(USERNAME), configer.getAppProp(SSH_PASS), configer.getAppProp(HOST), configer.getAppProp(HOST_PORT));
        String response = sftpClient.getSftpFile(requestName, parsedRequestName, "30");
        log.debug(REQUEST, response);
        Allure.addAttachment(MSG_FOR_SEND, response);
        testVars.setResponse(new Message(response));
        LocalThead.setTestVars(testVars);
        assertNotNull(response);
    }

    @И("^Сравнить значение \"(.*)\" с полученным из ответа по regexp паттерну \"(.*)\"$")
    public void checkWithOriginal(String origin, String regexpPatt) {
        TestVars testVars = LocalThead.getTestVars();
        StringBuilder sb = new StringBuilder();
        Pattern pattern = Pattern.compile(regexpPatt);
        Matcher matcher = pattern.matcher(testVars.getResponse().getBody());
        while (matcher.find()) {
            sb.append(testVars.getResponse().getBody().substring(matcher.start(), matcher.end()));
        }
        log.debug("Actual value {}", sb);
        Allure.addAttachment("Expected value", origin);
        Allure.addAttachment("Actual value", sb.toString());
        LocalThead.setTestVars(testVars);
        assertEquals(origin, sb.toString());
    }

    @И("^Сравнить переменную \"(.*)\" со значением \"(.*)\"$")
    public void checkVarsOriginal(String var, String origStr) {
        TestVars testVars = LocalThead.getTestVars();
        Allure.addAttachment("Variable value", testVars.getVariables().get(var));
        Allure.addAttachment("Actual value", origStr);
        LocalThead.setTestVars(testVars);
        assertEquals(origStr, testVars.getVariables().get(var));
    }

    @И("^(?:Сгенерировать переменную) ([a-zA-Z0-9а-яА-Я_]+)? по ответному сообщению по regexp \"(.*)\"$")
    public void generateByRegExp(String variableName, String regexpPatt) {
        TestVars testVars = LocalThead.getTestVars();
        StringBuilder sb = new StringBuilder();
        Pattern pattern = Pattern.compile(regexpPatt);
        Matcher matcher = pattern.matcher(testVars.getResponse().getBody());
        while (matcher.find()) {
            sb.append(testVars.getResponse().getBody().substring(matcher.start(), matcher.end()));
        }
        log.debug("Regexp generated value {}", sb);
        testVars.setVariables(variableName, sb.toString());
        Allure.addAttachment(variableName, sb.toString());
        LocalThead.setTestVars(testVars);
        assertNotNull(sb.toString());
    }

    @Тогда("^вывести в консоль переменную ([^\"]*)$")
    public void  printVar(String varname) {
        TestVars testVars = LocalThead.getTestVars();
        System.out.println(testVars.getVariable(varname));
    }

    @Тогда("^(?:Сгенерировать переменную) ([a-zA-Z0-9а-яА-Я_]+)? по ответному http-сообщению")
    public void generateByResponseHttp(String variableName) {
        TestVars testVars = LocalThead.getTestVars();
        String messageBody = testVars.getResponse().getBody();
        testVars.setVariables(variableName, messageBody);
        LocalThead.setTestVars(testVars);
    }

    @Тогда("^(?:Сгенерировать переменную) ([a-zA-Z0-9а-яА-Я_]+)? по ответному сообщению")
    public void generateByResponse(String variableName) {
        generateByRegExp(variableName, ".*");
    }

    @Тогда("^Вызвать команду по ssh ([^\"]*)$")
    public void execSshComand(String comand) throws CustomException {
        log.debug(REQUEST, comand);
        SshClient client = new SshClient(configer.getAppProp(HOST), configer.getAppProp(USERNAME), configer.getAppProp(SSH_PASS));
        client.execCommandToList(comand);
    }

    @Тогда("Выполнить SQL запрос (.*)")
    public void executeSqlRequest(String sqlFile) throws ParseException, IOException, CustomException, SQLException {
        TestVars testVars = LocalThead.getTestVars();
        String lines = new PrepareBody(this.scenario.getUri().replaceFirst(FILE, ""), sqlFile).loadBody();
        String filledTempl = new Templater(lines, testVars.getVariables()).fillTemplate();
        log.debug("Executing SQL Query: {}", filledTempl);
        StatementExecute execute = new StatementExecute();
        execute.executeSQLQueryNew(configer.getApplicationProperties(), filledTempl);
        LocalThead.setTestVars(testVars);
    }

    @Тогда("^Найти и сохранить номер рублевой карты(| с персональным лимитом,| c базовым планом) с балансом более (.*) рублей в переменную (.*)$")
    public void findRoubleCardWithBalance(String personal, String balance, String varName) throws ParseException, SQLException, IOException, CustomException {
        TestVars testVars = LocalThead.getTestVars();
        String realbalance = "";
        try {
            Integer.parseInt(balance);
            realbalance = balance;
        } catch (Exception e) {
            realbalance = testVars.getVariables().get(balance);
        }
        String rubSqlrequest = "";
        if (personal.equals("")) {
            rubSqlrequest = "/sql/findRURAccount.sql";
        } else if (personal.contains("персональным")) {
            rubSqlrequest = "/sql/findRURAccountWithLimit.sql";
        } else rubSqlrequest = "/sql/findRURAccountWithLimit.sql";
        testVars.setVariables("minAvailableBalance", realbalance);
        testVars.setQueryResult(executeSqlRequestWithReturn(rubSqlrequest, testVars.getVariables()));
        //Выбираем случайный ряд
        if (testVars.getQueryResult().size() > 0 && testVars.getQueryResult().containsKey(CRDNUM)) {
            int randRow = (int) (Math.random() * testVars.getQueryResult().size());
            testVars.setVariables(varName, testVars.getQueryResult().get(CRDNUM).get(randRow));
            Allure.addAttachment(varName, testVars.getQueryResult().get(CRDNUM).get(randRow));
        }
        if (testVars.getVariables().get(varName) == null) {
            findRoubleCardWithBalance(personal, balance, varName);
        }
        log.debug("Choosen card № {} and stored in {}", testVars.getVariables().get(varName), varName);
        LocalThead.setTestVars(testVars);
    }

    @Тогда("^Послать ISO запрос (.*) с ожидаемым статусом ответа:(.*)")
    public void sendISORequest(String fileBody, String expectedresponse, DataTable dataTable) throws IOException, CustomException, ISOException {
        TestVars testVars = LocalThead.getTestVars();
        List<String> resplist = Arrays.asList(expectedresponse.split(","));
        PrepareBody prep = new PrepareBody(this.scenario.getUri().replaceFirst(FILE, ""), fileBody);
        prep.loadBody();
        Map<String, String> fieldsMap = testVars.fillMapWithVariables(dataTable.asMap(String.class, String.class));
        UniversalRequest request = UniversalRequestBuilder.createBuilderFromTemplate(new File(prep.getFilePath())).build();
        for (Map.Entry<String, String> entry : fieldsMap.entrySet()) {
            request = UniversalRequestBuilder.createBuilderFromRequest(request).setField(entry.getKey(), entry.getValue()).build();
        }
        testVars.setUniversalRequest(request);
        PrepareBody b24path = new PrepareBody(this.scenario.getUri().replaceFirst(FILE, ""), "/xml/base24_old.xml");
        b24path.cutPath();
        ISOConnection connection = new ISOConnection(configer.getAppProp(ISO_HOST),
                Integer.parseInt(configer.getAppProp(ISO_PORT)),
                b24path.getFilePath(), true);
        request.setISOHeader("A4M08000");
        connection.open();
        ProcessingResponse response = connection.send(request);
        log.debug("Request passed successfully response code {}", response.getResponseCode());
        Allure.addAttachment("response", response.getISOMessageContent().toString());
        testVars.setProcessingResponse(response);
        LocalThead.setTestVars(testVars);
        assertTrue(String.format("Request failed: expected response %s, current response:%s", expectedresponse, response.getResponseCode()), resplist.contains(String.valueOf(response.getResponseCode())));
    }

    @Тогда("Подтвердить ISO операцию с mti (.*)")
    public void confirmISOTransaction(String mtiCode) throws ISOException, IOException {
        TestVars testVars = LocalThead.getTestVars();
        //Я пока до конца не понял как это работает в целом перепилю не говнокодом когда побольше этих подтверждений авторизации посмотрю
        UniversalRequest request = UniversalRequestBuilder.createBuilderFromRequest(testVars.getUniversalRequest()).
                setField("mti", mtiCode).
                setField("authorization_code", testVars.getProcessingResponse().getAuthorizationCode()).
                setField("response_code", String.valueOf(testVars.getProcessingResponse().getResponseCode())).build();

        PrepareBody b24path = new PrepareBody(this.scenario.getUri().replaceFirst(FILE, ""), "/xml/base24_old.xml");
        b24path.cutPath();
        ISOConnection connection = new ISOConnection(configer.getAppProp(ISO_HOST),
                Integer.parseInt(configer.getAppProp(ISO_PORT)),
                b24path.getFilePath(), true);
        connection.open();
        ProcessingResponse response = connection.send(request);
        assertTrue(String.format("Confirmation response code is different to '1', its %s", response.getResponseCode()), response.getResponseCode() == 1);
        testVars.setProcessingResponse(response);
        LocalThead.setTestVars(testVars);
    }


    @Тогда("Сохранить поле (.*) из ответного ISO сообщения в переменную (.*)")
    public void storeVarFromISOField(String fieldname, String varName) {
        TestVars testVars = LocalThead.getTestVars();
        String varVal = testVars.getProcessingResponse().getField(FieldsMap.getISOField(fieldname)).replaceAll("_", "");
        testVars.setVariables(varName, varVal);
        log.debug("Variable {} stored in {}", varName, varVal);
        Allure.addAttachment(varName, varVal);
        LocalThead.setTestVars(testVars);
    }

    @Тогда("Сохранить идентификатор сообщения из таблицы NSMLOG в переменную (.*)")
    public void checkMessageInNSMLOG(String varname) throws ParseException, SQLException, IOException, CustomException {
        TestVars testVars = LocalThead.getTestVars();
        testVars.setQueryResult(executeSqlRequestWithReturn("/sql/checkMessageInNSMLOG.sql", testVars.getVariables()));
        String msgid = "";
        if (testVars.getQueryResult().size() > 0 && testVars.getQueryResult().containsKey("MSGID")) {
            msgid = testVars.getQueryResult().get("MSGID").get(0);
        }
        assertFalse(msgid.equalsIgnoreCase(""));
        testVars.setVariables(varname, msgid);
        log.debug("Variable with value {} stored in {}", msgid, varname);
        LocalThead.setTestVars(testVars);
        Allure.addAttachment(msgid, varname);
    }


    @Тогда("^Проверить (наличие|отсутствие) деталей авторизации$")
    public void checkAuthorizationDetails(String checkStrat) throws ParseException, SQLException, IOException, CustomException {
        TestVars testVars = LocalThead.getTestVars();

        testVars.setQueryResult(executeSqlRequestWithReturn("/sql/checkAuthorisationDetails.sql", testVars.getVariables()));
        if (checkStrat.equals("наличие")) {
            assertTrue("Authdetails request returned empty set", testVars.getQueryResult().size() > 0);
            log.debug("Authdetails found");
        } else {
            assertFalse("Authdetails for transaction are present", testVars.getQueryResult().size() > 0);
            log.debug("No AuthDetails found");
        }
        LocalThead.setTestVars(testVars);
    }

    @Тогда("^Проверить (отсутсвие|наличие) Authorization Exception$")
    public void checkNoAuthExcept(String checkStrat) throws ParseException, SQLException, IOException, CustomException {
        TestVars testVars = LocalThead.getTestVars();
        testVars.setQueryResult(executeSqlRequestWithReturn("/sql/checkMessageInZAUTHEXC.sql", testVars.getVariables()));
        if (checkStrat.equals("отсутсвие")) {
            assertFalse("Exception for current message found", testVars.getQueryResult().size() > 0);
            log.debug("No exceptions found");
        } else {
            assertTrue("Exception for current message not found", testVars.getQueryResult().size() > 0);
            log.debug("Exceptions found");
        }
        LocalThead.setTestVars(testVars);
    }

    @Тогда("Сгенерировать переменную (.*) со значением (.*)")
    public void generateVarWithValue(String varName, String varValue) {
        TestVars testVars = LocalThead.getTestVars();
        testVars.setVariables(varName, varValue);
        log.debug("Genearated variable {} with value {}", varName, varValue);
        Allure.addAttachment(varName, varValue);
        LocalThead.setTestVars(testVars);
    }

    @Тогда("Сгенерировать переменную (.*) из переменной (.*)")
    public void generateVarWithValue2(String varName, String varName2) {
        TestVars testVars = LocalThead.getTestVars();
        testVars.setVariables(varName, testVars.getVariables().get(varName2));
        log.debug("Genearated variable {} with value {}", varName, testVars.getVariables().get(varName2));
        Allure.addAttachment(varName, varName2);
        LocalThead.setTestVars(testVars);
    }

    @Тогда("Сохранить значение переменной (.*) в память")
    public void saveVarInMap(String varName) {
        TestVars testVars = LocalThead.getTestVars();
        VarMemory.put(varName,testVars.getVariables().get(varName));
        String infoString = String.format("Save value <<%s = %s>> in memory", varName,testVars.getVariables().get(varName));
        Allure.addAttachment(varName,testVars.getVariables().get(varName));
        log.debug(infoString);
        LocalThead.setTestVars(testVars);
    }

    @Тогда("Загрузить значение переменной (.*) из памяти")
    public void loadValueIntoMemory(String varName) {
        TestVars testVars = LocalThead.getTestVars();
        testVars.setVariables(varName, VarMemory.get(varName));
        log.debug("Load variable {} into memory ({})", varName, VarMemory.get(varName));
        Allure.addAttachment(varName, VarMemory.get(varName));
        LocalThead.setTestVars(testVars);
    }

    @Тогда("Убедиться в истинности выражения (.*)")
    public void checkEvalisTrue(String eval) throws ScriptException {
        TestVars testVars = LocalThead.getTestVars();
        String streval = eval;

        for (String key : testVars.getVariables().keySet()) {
            if (streval.contains(key)) {
                streval = streval.replaceAll(key, testVars.getVariables().get(key));
            }
        }
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("JavaScript");

        log.info("Checking expression: {}", streval);
        assertTrue("Expression is false", (Boolean) engine.eval(streval));
        LocalThead.setTestVars(testVars);
    }

    @Тогда("Сгенерировать переменную (.*) по выражению:(.*)")
    public void generateVarByMathexpression(String varName, String expression) throws ScriptException {
        TestVars testVars = LocalThead.getTestVars();
        String streval = expression;

        for (String key : testVars.getVariables().keySet()) {
            if (streval.contains(key)) {
                streval = streval.replaceAll(key, testVars.getVariables().get(key));
            }

            streval = streval.replaceAll("\\.00", "");
        }
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("JavaScript");
        Object result = engine.eval(streval);
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String rounded = decimalFormat.format(result).replace(",", ".");
        LocalThead.getTestVars().setVariables(varName, rounded);
        Allure.addAttachment(varName, rounded);
        log.debug("Evaluated value {} stored in {}", rounded, varName);
        LocalThead.setTestVars(testVars);
    }

    @Тогда("^Приостановить выполнение теста на ([0-9]+) секунд$")
    public void pauseTest(String seconds) throws InterruptedException {
        log.debug("Pause {} seconds", seconds);
        Thread.sleep(Integer.parseInt(seconds) * 1000L);
    }


    @Тогда("^Заполнить шаблон значениями$")
    public void fillTemplate(DataTable dataTable) {
        TestVars testVars = LocalThead.getTestVars();
        Map<String, String> tmpl = dataTable.asMap(String.class, String.class);
        for (Map.Entry entry : tmpl.entrySet()
        ) {
            if (entry.getValue().toString().startsWith("$"))
            {
                testVars.setVariables(entry.getKey().toString(), VarMemory.get(entry.getValue().toString().replaceAll("\\$","")));
                //log.debug("****** {}  -  {}", entry.getKey().toString(),VarMemory.get(entry.getValue().toString().replaceAll("\\$","")));
            }
            else if (entry.getValue().toString().contains("$")) {
                VarMemory.put(entry.getKey().toString(), entry.getValue().toString());
                testVars.setVariables(entry.getKey().toString(), replaceTestVariableValue(entry.getValue().toString(), testVars));
            } else {
                testVars.setVariables(entry.getKey().toString(), entry.getValue().toString());
                //log.debug("****** {}  -  {}", entry.getKey().toString(),entry.getValue().toString());
            }
        }
        LocalThead.setTestVars(testVars);
    }


    @И("^Сгенерировать переменную (.*) по значению тэга из ответного сообщения по xpath (.*)")
    public void getTagValueReceivedMessagedForXpath(String variableName, String xpath) throws CustomException {
        TestVars testVars = LocalThead.getTestVars();
        String tagValue = XMLManager.getInstance().getNodeValueForXmlString(testVars.getResponse().getBody(), xpath);
        LocalThead.getTestVars().setVariables(variableName, tagValue);
        String infoString = String.format("Generated variable %s by xpath %s", variableName, tagValue);
        Allure.addAttachment(infoString, tagValue);
        log.debug(infoString);
        assertNotNull(tagValue);
        LocalThead.setTestVars(testVars);
    }

    @Тогда("^Сравнить полученный ответ с шаблоном (.*)? (|без учета регистра)$")
    public void compareReceivedDataWithExpected(String templateFile, String without) throws IOException, CustomException, SAXException {
        boolean isAllEqual = compareReceivedMessageWithExpected(templateFile, org.apache.commons.lang3.StringUtils.isBlank(without));
        assertTrue("Recieved message does not match template", isAllEqual);
    }

    @И("^(?:Сгенерировать переменную) ([a-zA-Z0-9а-яА-Я_]+)? (?:по формату) (.*)?$")
    public void generateTestDataVariableForFormat(String variableName, String variableFormat) throws CustomException {
        TestVars testVars = LocalThead.getTestVars();
        String varByFormat = TestData.generateValueByFormat(variableFormat);
        String infoString = String.format("Generated variable %s by format %s variable value %s", variableName, variableFormat, varByFormat);
        testVars.setVariables(variableName, varByFormat);
        Allure.addAttachment(infoString, varByFormat);
        log.info(infoString);
        assertNotNull(varByFormat);
        LocalThead.setTestVars(testVars);
    }

    private boolean compareReceivedMessageWithExpected(String templateFile, boolean isCaseSensitive) throws IOException, CustomException, SAXException {
        TestVars testVars = LocalThead.getTestVars();
        String templateBody = new PrepareBody(this.scenario.getUri().replaceFirst(FILE, ""), templateFile).loadBody();
        String currentBody = testVars.getResponse().getBody();
        Allure.addAttachment("Recieved message:", currentBody);
        Allure.addAttachment("Template message:", templateBody);
        if (!isCaseSensitive) {
            templateBody = templateBody.toLowerCase();
            currentBody = currentBody.toLowerCase();
        }
        XmlComparator xmlComparator = XmlComparator.create(XmlUtil.createXml(currentBody), XmlUtil.createXml(templateBody));
        if (xmlComparator.isXmlsSimilar()) {
            log.debug("Message is similar to expected");
        } else {
            log.debug("Message is different to expected");
            log.debug("Found message: {}", xmlComparator.getTestXml());
            log.debug("Expected message: {}", xmlComparator.getControlXml());
            log.debug(xmlComparator.getDifferences());
            Allure.addAttachment("Differences between messages:", xmlComparator.getDifferences());

        }
        return xmlComparator.isXmlsSimilar();

    }

    private Map<String, List<String>> executeSqlRequestWithReturn(String sqlFile, Map<String, String> templateParam) throws ParseException, IOException, CustomException, SQLException {
        String lines = new PrepareBody(this.scenario.getUri().replaceFirst(FILE, ""), sqlFile).loadBody();
        String filledTempl = new Templater(lines, templateParam).fillTemplate();
        log.debug("Executing SQL Query: {}", filledTempl);
        StatementExecute exec = new StatementExecute();
        return exec.executeSQLQueryNew(configer.getApplicationProperties(), filledTempl);
    }

    @Тогда("^Дождаться окончания выполнения задачи (.*)$")
    public void checkJob(String jobname) throws SQLException, IOException, ParseException, CustomException, InterruptedException {
        TestVars testVars = LocalThead.getTestVars();
        testVars.setVariables("jobname", testVars.getVariables().get(jobname));
        testVars.setQueryResult(this.executeSqlRequestWithReturn("/sql/checkJob.sql", testVars.getVariables()));

        while (testVars.getQueryResult().size() > 0){
            log.debug("Pause {} minutes", "1");
            Thread.sleep(60 * 1000L);
			testVars.setQueryResult(this.executeSqlRequestWithReturn("/sql/checkJob.sql", testVars.getVariables()));
        }
        log.debug("JOB {} not found", testVars.getVariables().get(jobname));
        LocalThead.setTestVars(testVars);
    }

    @Тогда("^Ожидать статус (.*) в поле (.*) на запрос к БД (.*) в течении (.*) секунд")
    public void sendAndWait(String expectedresponse, String xpath, String fileBody, String timeout) throws IOException, CustomException, SQLException, ParseException {
        Timer timer = new Timer(Integer.parseInt(timeout) * 1000);
        boolean gotExpectedStatus = getAndCheck(fileBody, xpath, expectedresponse);
        timer.start();
        log.debug("Старт ожидания. Таймаут : {}", timeout);
        while (!gotExpectedStatus && timer.check()) {
            Waiting.sleep(MON_PERIOD);
            gotExpectedStatus = getAndCheck(fileBody, xpath, expectedresponse);
        }
        timer.stop();
        log.debug("Стоп ожидания. Таймер {}. Результат : {}", timer.getDuration(), timer.check());
        assertTrue("Статус соответствует ожиданиям", gotExpectedStatus);
    }

    @Тогда("^Изменить значение переменной (.*) на (.*)")
    public void changeTestVariable(String varName, String varValue){
        TestVars testVars = LocalThead.getTestVars();
        if(checkVars(varValue)) {
           varValue = replaceTestVariableValue(varValue, testVars);
        }
        testVars.setVariables(varName, varValue);
        LocalThead.setTestVars(testVars);
        replaceAllTestVariableValue();
    }

    @Тогда("^Убедиться что строка (.*) содержит переменную (.*)")
    public void checkVariableInString(String string, String varName){
        TestVars testVars = LocalThead.getTestVars();
        String var = "\""+varName+"\":"+testVars.getVariables().get(varName);
        checkStrInString(string, var);
    }

    @Тогда("^Убедиться что строка (.*) содержит значение (.*)")
    public void checkStrInString(String string, String str){
        if(checkVars(str)) {
            TestVars testVars = LocalThead.getTestVars();
            str = replaceTestVariableValue(str, testVars);
        }
        assertTrue("Статус не соответствует ожиданиям", string.contains(str));
    }

    @Тогда("^Убедиться что в строке (.*) содержится значение переменной (.*)")
    public void checkVarStrInString(String string, String varName){
        TestVars testVars = LocalThead.getTestVars();
        checkStrInString(string, testVars.getVariables().get(varName));
    }

    @Тогда("^Убедиться что переменная (.*) содержит значение (.*)")
    public void checkStrInVar(String varName, String str){
        TestVars testVars = LocalThead.getTestVars();
        checkStrInString(testVars.getVariables().get(varName), str);
    }

    @Тогда("^Убедиться что в переменной (.*) содержится значение переменной (.*)")
    public void checkStrVarInVar(String varName, String str){
        TestVars testVars = LocalThead.getTestVars();
        checkStrInString(testVars.getVariables().get(varName), testVars.getVariables().get(str));
    }

    @Тогда("^Убедиться что переменная (.*) содержит переменную (.*)")
    public void checkVariableInVariable(String varName, String checkingVarName){
        TestVars testVars = LocalThead.getTestVars();
        checkVariableInString(testVars.getVariables().get(varName), checkingVarName);
    }

    @Тогда("^Сравнить массив из переменной (.*) с массивом$")
    public void compareMassVar(String varName, DataTable dataTable) throws IOException {
        TestVars testVars = LocalThead.getTestVars();
        List<String> comperableTable = new ArrayList<String>(dataTable.asList());
        if(checkVars(comperableTable)) {
            comperableTable = fillVarValue(comperableTable);
        }
        Type itemsListType = new TypeToken<List<String>>() {}.getType();
        List<String> comperableList = new Gson().fromJson(testVars.getVariables().get(varName), itemsListType);
        Assert.assertTrue("Значение переменной отличается от ожидаемого результата", comperableList.equals(comperableTable));
    }

    @Тогда("^Сравнить массив из переменной (.*) с массивом вне зависимости от порядка$")
    public void compareMassVarNoSort(String varName, DataTable dataTable) throws IOException {
        TestVars testVars = LocalThead.getTestVars();
        List<String> comperableTable = new ArrayList<String>(dataTable.asList());
        Type itemsListType = new TypeToken<List<String>>() {}.getType();
        List<String> comperableList = new ArrayList<String>(new Gson().fromJson(testVars.getVariables().get(varName), itemsListType));
        if(checkVars(comperableTable)) {
            comperableTable = fillVarValue(comperableTable);
        }
        Collections.sort(comperableTable);
        Collections.sort(comperableList);
        Assert.assertTrue("Значение переменной отличается от ожидаемого результата", comperableList.equals(comperableTable));
    }

    @Тогда("^Сравнить массив из переменной (.*) с массивом из переменной ([^\"]*) с учетом порядка")
    public void compareMassVar(String varName, String varMassName) throws IOException {
        TestVars testVars = LocalThead.getTestVars();
        Type itemsListType = new TypeToken<List<String>>() {}.getType();
        List<String> comperableList = new ArrayList<String>(new Gson().fromJson(testVars.getVariables().get(varName), itemsListType));
        List<String> comperableTable = new ArrayList<String>(new Gson().fromJson(testVars.getVariables().get(varMassName), itemsListType));
        Assert.assertTrue("Значение переменной отличается от ожидаемого результата", comperableList.equals(comperableTable));
    }

    @Тогда("^Сравнить массив из переменной (.*) с массивом из переменной ([^\"]*) вне зависимости от порядка")
    public void compareMassVarNoSort(String varName, String varMassName) throws IOException {
        TestVars testVars = LocalThead.getTestVars();
        Type itemsListType = new TypeToken<List<String>>() {}.getType();
        List<String> comperableList = new Gson().fromJson(testVars.getVariables().get(varName), itemsListType);
        List<String> comperableTable = new Gson().fromJson(testVars.getVariables().get(varMassName), itemsListType);
        Collections.sort(comperableTable);
        Collections.sort(comperableList);
        Assert.assertTrue("Значение переменной отличается от ожидаемого результата", comperableList.equals(comperableTable));
    }

    @Тогда("^Проверить что значение переменной (.*) содержится в массиве (.*)")
    public void checkVarInMass(String varName, String varMassName){
        TestVars testVars = LocalThead.getTestVars();
        Type itemsListType = new TypeToken<List<String>>() {}.getType();
        List<String> list = new Gson().fromJson(testVars.getVariables().get(varMassName), itemsListType);
        Assert.assertTrue("Переменная не найдена в массиве", list.contains(testVars.getVariables().get(varName)));
    }

    @Тогда("^Присвоить переменной (.*) значение (.*)")
    public void insertVar(String varName, String varValue){
        TestVars testVars = LocalThead.getTestVars();
        if (varName.equals("clientsCount")) {
            // если эта переменная инициализирована переменной среды count, то значение переменной среды считается приоритетным
            if (!isEmpty(configer.getCount())) {
                testVars.setVariables(varName, configer.getCount());
                LocalThead.setTestVars(testVars);
            }
            return;
        }
        if (varName.equals("waveId")) {
            if (!isEmpty(configer.getWave())) {
                testVars.setVariables(varName, configer.getWave());
                LocalThead.setTestVars(testVars);
            }
            return;
        }
        if(checkVars(varValue)) {
            varValue = replaceTestVariableValue(varValue, testVars);
        }
        testVars.setVariables(varName, varValue);
        LocalThead.setTestVars(testVars);
    }

    public boolean getAndCheck(String proc, String xpath, String actualStat) throws SQLException, CustomException, ParseException, IOException {
        execCallableStatement(proc);
        TestVars testVars = LocalThead.getTestVars();
        String tagValue = XMLManager.getInstance().getNodeValueForXmlString(testVars.getResponse().getBody(), xpath);
        LocalThead.setTestVars(testVars);
        return actualStat.equals(tagValue);
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

    public void replaceAllTestVariableValue() {
        TestVars testVars = LocalThead.getTestVars();
        for (Map.Entry entry : testVars.getVariables().entrySet()) {
            if(Objects.nonNull(VarMemory.get(entry.getKey())) && VarMemory.get(entry.getKey()).contains("$")) {
                testVars.setVariables(entry.getKey().toString(), replaceTestVariableValue(VarMemory.get(entry.getKey()), testVars));
            }
        }
        LocalThead.setTestVars(testVars);
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

    public List<String> fillVarValue(List<String> entryList) {
        TestVars testVars = LocalThead.getTestVars();
        for(String entry : entryList) {
            if(entry.contains("$")) {
                int ind = entryList.indexOf(entry);
                String varValue = testVars.getVariables().get(entry.substring(entry.indexOf('{') + 1, entry.indexOf('}')));
                entryList.set(ind, varValue);
            }
        }
        return entryList;
    }


    @Тогда("^Трансформировать дату из переменной (.*), записанной в формате (.*) в формат (.*) и сохранить её в переменную (.*)")
    public void changeTime(String oldDateVarName, String oldFormatExp, String newFormatExp, String newDateVarName) throws java.text.ParseException {
        TestVars testVars = LocalThead.getTestVars();
        String oldDateVarValue = testVars.getVariables().get(oldDateVarName);
        SimpleDateFormat oldDateFormat = new SimpleDateFormat(oldFormatExp, Locale.US);
        SimpleDateFormat newDateFormat = new SimpleDateFormat(newFormatExp);
        Date date = oldDateFormat.parse(oldDateVarValue);
        String newDateVarValue = newDateFormat.format(date);
        testVars.setVariables(newDateVarName, newDateVarValue);
        LocalThead.setTestVars(testVars);
    }


    @Тогда("^Изменить дату из переменной (.*) на (.*) минут")
    public void changeTime(String changeDateVarName, Integer correctTime){
        TestVars testVars = LocalThead.getTestVars();
        String changeDateVarValue = testVars.getVariables().get(changeDateVarName);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy'T'HH:mm", Locale.US);
        LocalDateTime localDateTime = LocalDateTime.parse(changeDateVarValue, formatter);
        LocalDateTime timeResult = correctTime < 0 ? localDateTime.minusMinutes(Math.abs(correctTime)) : localDateTime.plusMinutes(correctTime);
        testVars.setVariables(changeDateVarName, timeResult.format(formatter));
        LocalThead.setTestVars(testVars);
        replaceAllTestVariableValue();
    }

    //Данные задаются в feature file под строкой, например
    //  * пользователь сохраняет файл "filename" в каталог "catalog" c данными
    //     |   c1r1data       |  c2r1data    |
    //     |   c1r2data       |  c2r2data    |
    //     |   c1r3data       |  c2r3data    |
    @Тогда("^пользователь сохраняет файл \"([^\"]*)\" в каталог \"([^\"]*)\" c данными$")
    public void createFileToPathWithData(String fileName, String catalogPath, DataTable dataTable) throws IOException {

        String fullPath = catalogPath.trim() + "/" + fileName.trim();
        log.info("пользователь сохраняет файл " + fileName + " в каталог " + catalogPath + " c данными");
        if ("JSON".equals(FilenameUtils.getExtension(fileName).toUpperCase())) {
            writeJsonFile(dataTable, fullPath);
        }
    }

    //данные из HashMap "testData" пишутся в файл "fileName" в папке проекта
    @Тогда("^пользователь сохраняет данные из HashMap (testData|lastJsonData) в файл \"([^\"]*)\" в папке проекта$")
    public void createFileWithDataFromHashMap(String hashmap, String fileName) throws IOException {

        String fullPath = Configurier.getInstance().getAppProp("data.folder").trim() + "/" + fileName.trim();
        log.info("пользователь сохраняет файл " + fileName + " в каталог " + Configurier.getInstance().getAppProp("data.folder") + " c данными");
        if ("JSON".equals(FilenameUtils.getExtension(fileName).toUpperCase())) {
            writeJsonFileFromHashMap(hashmap, fullPath);
        }
    }

    //данные из HashMap "testData" пишутся в файл "fileName" в указанный каталог "catalogPath"
    @Тогда("^пользователь сохраняет данные из HashMap (testData|lastJsonData) в файл \"([^\"]*)\" в каталоге \"([^\"]*)\"$")
    public void createFileToPathWithDataFromHashMap(String hashmap, String fileName, String catalogPath) throws IOException {

        String fullPath = catalogPath.trim() + "/" + fileName.trim();
        log.info("пользователь сохраняет файл " + fileName + " в каталог " + catalogPath + " c данными");
        if ("JSON".equals(FilenameUtils.getExtension(fileName).toUpperCase())) {
            writeJsonFileFromHashMap(hashmap, fullPath);
        }
    }

    // Пользователь читает файл "fileName" в каталоге "catalogPath" и помещает данные в HashMap lastJsonData
    @Тогда("^пользователь читает файл \"([^\"]*)\" в каталоге \"([^\"]*)\"$")
    public void readFileFromPath(String fileName, String catalogPath) {

        String fullPath = catalogPath.trim() + "/" + fileName.trim();

        log.info("пользователь читает файл " + fullPath);
        if ("JSON".equals(FilenameUtils.getExtension(fullPath).toUpperCase()))
            readJsonFileWithPath(fullPath);
    }

    // Пользователь читает файл "fileName" в папке проекта и помещает данные в HashMap lastJsonData
    @Тогда("^пользователь читает файл \"([^\"]*)\" из папки проекта$")
    public void readFileFromPrjFolder(String fileName) {
        String fullPath = Configurier.getInstance().getAppProp("data.folder").trim() + "/" + fileName.trim();
        log.info("пользователь читает файл " + fileName + " из папки проекта " + Configurier.getInstance().getAppProp("data.folder") + " и помещает данные в HashMap lastJsonData");
        if ("JSON".equals(FilenameUtils.getExtension(fullPath).toUpperCase()))
            readJsonFileWithPath(fullPath);
    }

    // Пользователь читает файл "fileName" с типом "fileType" в каталоге "catalogPath" и помещает данные в HashMap lastJsonData
    @Тогда("^пользователь читает ([^\"]*) файл \"([^\"]*)\" находящийся в каталоге \"([^\"]*)\"")
    public void readFileInCatalog(String fileType, String fileName, String filePath) {
        String fullFilePath = filePath.trim() + "/" + fileName.trim() + "." + fileType.trim();
        log.info("пользователь читает файл " + fullFilePath);
        if ("JSON".equals(fileType.toUpperCase())) {
            readJsonFileWithPath(fullFilePath);
        }
    }


    @Тогда("^Выгрузка тестовых данных из БД запросом \"([^\"]*)\" в каталог \"([^\"]*)\"")
    public void writeJsonFilefromdb(String SQLText, String filePath) {
//       Теперь этот метод вызывается в Aspect efr.aop.JsonHandleAspect, здесь не должно быть реализации
    }

    @Тогда("^пользователь читает файлы из папки проекта$")
    public void readFilesFromPrjFolder(DataTable dataTable) {
        String fileName;
        List<List<String>> table = dataTable.cells();
        log.info("пользователь читает файлы и переносит их данные в одну hashmap testData");
        for (List m : table) {
            fileName = m.get(0).toString();
            readFileFromPrjFolder(fileName);
            log.info("пользователь добавляет данные из lastJsonData к уже имеющимся в testData");
            TestVars.getTestData().putAll(TestVars.getLastJsonData());
        }
        log.info("пользователь очищает lastJsonData и заполняет ее данными из testData");
        TestVars.getLastJsonData().clear();
        TestVars.getLastJsonData().putAll(TestVars.getTestData());
        TestVars.getTestData().clear();

    }


    private void writeJsonFile(DataTable dataTable, String filePath) throws IOException {
        List<List<String>> table = dataTable.cells();
        JSONObject jsonObject = new JSONObject();
        for (List list : table) {
            jsonObject.put(list.get(0).toString(), list.get(1).toString());
        }
        DataFileHelper.write(filePath, jsonObject.toJSONString());

    }

    // Используется указанная HashMap
    private void writeJsonFileFromHashMap(String hashmap, String filePath) throws IOException {
        JSONObject jsonObject = new JSONObject();
        HashMap<String, String> tmp = new HashMap<String, String>();
        switch (hashmap) {
            case "testData":
                tmp = new HashMap<String, String>(TestVars.getTestData());
                break;
            case "lastJsonData":
                tmp = new HashMap<String, String>(TestVars.getLastJsonData());
                break;
        }

        for (Map.Entry<String, String> entry : tmp.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            jsonObject.put(key, value);
        }
        DataFileHelper.write(filePath, jsonObject.toJSONString());

    }


    // Используется HashMap lastJsonData
    private void readJsonFileWithPath(String fullFilePath) {
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) JSONValue.parseWithException(DataFileHelper.read(fullFilePath));
        } catch (org.json.simple.parser.ParseException | IOException e) {
            e.printStackTrace();
        }
        TestVars.setLastJsonData(jsonObject);
    }

    @Тогда("^пользователь добавляет содержимое (lastJsonData в testData|testData в lastJsonData)$")
    public void putAllFromOneToAnotherHashmap(String hashmap) {
        switch (hashmap) {
            case ("lastJsonData в testData"):
                TestVars.getTestData().putAll(TestVars.getLastJsonData());
                break;
            case ("testData в lastJsonData"):
                TestVars.getLastJsonData().putAll(TestVars.getTestData());
                break;
        }
    }

}
