package clp.steps;


import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.ru.Тогда;
import gherkin.deps.com.google.gson.Gson;
import io.cucumber.datatable.DataTable;
import io.qameta.allure.Allure;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.velocity.runtime.parser.ParseException;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import clp.core.dbconnector.StatementExecute;
import clp.core.exception.CustomException;
import clp.core.kafka.impl.KafkaConsumerImpl;
import clp.core.kafka.impl.KafkaProducererImpl;
import clp.core.testdata.PrepareBody;
import clp.core.testdata.Templater;
import clp.core.helpers.Configurier;
import clp.core.messages.KafkaMessage;
import clp.core.vars.LocalThead;
import clp.core.vars.TestVars;

import java.io.*;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainSteps {
    private static final Logger log = LoggerFactory.getLogger(MainSteps.class);
    private Scenario scenario;
    private static final Configurier configer = Configurier.getInstance();
    private static final String FILE = "file:";
    private static final String MSG_FOR_SEND = "Message for send";
    private static final String REQUEST = "Request {}";


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
        TestVars testVars = LocalThead.getTestVars();
        LocalThead.setTestVars(null);
        log.info("End test: {}", this.scenario.getUri());
    }

    @Тогда("^Послать kafka-сообщение ([^\"]*) в топик ([^\" ]*)$")
    public void sendKafkaMessage(String msgname, String topicName) throws ParseException, IOException, CustomException {
        TestVars testVars = LocalThead.getTestVars();
        String lines = new PrepareBody(this.scenario.getUri().replaceFirst(FILE, ""), msgname).loadBody();
        String filledTempl = new Templater(lines, testVars.getVariables()).fillTemplate();
        KafkaProducererImpl kafkaProducer;
        Allure.addAttachment(MSG_FOR_SEND, filledTempl);
        log.debug(REQUEST, filledTempl);
        try {
            Map<String, String> headers = getMapfromProperties("header.properties");
            kafkaProducer = new KafkaProducererImpl(filledTempl, topicName, headers);
        } catch (FileNotFoundException e) {
            log.info("Header file doesnt found, send without headers");
            kafkaProducer = new KafkaProducererImpl(filledTempl, topicName);
        }
        kafkaProducer.doProduce();
    }


    @Тогда("^Послать kafka-сообщение ([^\"]*) в топик ([^\"]*) с заголовками ([^\"]*)$")
    public void sendKafkaMessage(String msgname, String topicName, String headerfile) throws ParseException, IOException, CustomException {
        TestVars testVars = LocalThead.getTestVars();
        String lines = new PrepareBody(this.scenario.getUri().replaceFirst(FILE, ""), msgname).loadBody();
        String filledTempl = new Templater(lines, testVars.getVariables()).fillTemplate();
        KafkaProducererImpl kafkaProducer;
        Allure.addAttachment(MSG_FOR_SEND, filledTempl);
        log.debug(REQUEST, filledTempl);
        try {
            Map<String, String> headers = getMapfromProperties(headerfile);
            kafkaProducer = new KafkaProducererImpl(filledTempl, topicName, headers);
        } catch (FileNotFoundException e) {
            throw new CustomException("Header file doesnt found");
        }
        kafkaProducer.doProduce();
    }

    @Тогда("^Поднять kafka-consumer ([^\"]*) на топик ([^\"]*)$")
    public void startKafkaConsumer(String kafkaConsumerAlias, String topicname) {
        TestVars testVars = LocalThead.getTestVars();
        KafkaConsumerImpl kafkaConsumerer = new KafkaConsumerImpl(topicname);
        Thread thread = new Thread(kafkaConsumerer);
        thread.start();

        testVars.addConsumer(kafkaConsumerAlias, kafkaConsumerer);
    }

    @Тогда("^Убедиться что kafka-consumer ([^\"]*) получил сообщение")
    public void checkRecievedMessage(String kafkaConsumerAlias) {
        TestVars testVars = LocalThead.getTestVars();
        KafkaConsumerImpl consumer = testVars.getKafkaConsumer(kafkaConsumerAlias);
        Assert.assertTrue(consumer.getConsumerRecordList().size() > 0);
    }

    @Тогда("^Выполнить SQL-запрос ([^\"]*) в БД ([^\"]*) и сохранить значение ячейки в переменную ([^\"]*)$")
    public void executeSQLqueryAndSaveVal(String sqlrequest, String dbAlias, String var) throws CustomException, SQLException, ParseException, IOException {
        TestVars testVars = LocalThead.getTestVars();
        testVars.setQueryResult(executeSqlRequestWithReturn(sqlrequest, testVars.getVariables(), dbAlias));
        Map<String, List<String>> queryresult = testVars.getQueryResult();
        if (queryresult.keySet().size() > 1) {
            throw new CustomException("Результат выполнения скрипта выдал больше одного столбца. Невозможно сохранить в переменную");
        } else {
            for (String key : queryresult.keySet()) {
                if (queryresult.get(key).size() > 1) {
                    throw new CustomException("Результат выполнения скрипта выдал больше одной строки. Невозможно сохранить в переменную");
                } else if (queryresult.get(key).size() < 1) {
                    throw new CustomException("Результат выполнения скрипта выдал пустое множество");
                } else {
                    testVars.setVariables(var, queryresult.get(key).get(0));
                }
            }
        }
        log.info(String.format("Variable %s stored with value %s", var, testVars.getVariables().get(var)));
    }

    @Тогда("^Выполнить SQL-запрос ([^\"]*) в БД ([^\"]*) и сохранить массив в переменную ([^\"]*)$")
    public void executeSQLqueryAndSaveMassInVal(String sqlrequest, String dbAlias, String var) throws CustomException, SQLException, ParseException, IOException {
        TestVars testVars = LocalThead.getTestVars();
        testVars.setQueryResult(executeSqlRequestWithReturn(sqlrequest, testVars.getVariables(), dbAlias));
        Map<String, List<String>> queryresult = testVars.getQueryResult();
        if (queryresult.keySet().size() > 1) {
            List<String> resultList = new ArrayList<>();
            for (String key : queryresult.keySet()) {
                if (queryresult.get(key).size() < 1) {
                    throw new CustomException("Результат выполнения скрипта выдал пустое множество");
                }
                resultList.add(queryresult.get(key).get(0));
            }
            String jsonVar = new Gson().toJson(resultList);
            testVars.setVariables(var, jsonVar);
        } else {
            for (String key : queryresult.keySet()) {
                if (queryresult.get(key).size() >= 1) {
                    String jsonVar = new Gson().toJson(queryresult.get(key));
                    testVars.setVariables(var, jsonVar);
                } else if (queryresult.get(key).size() < 1) {
                    throw new CustomException("Результат выполнения скрипта выдал пустое множество");
                }
            }
        }
        log.info(String.format("Variable %s stored with value %s", var, testVars.getVariables().get(var)));
    }

    private Map<String, List<String>> executeSqlRequestWithReturn(String sqlFile, Map<String, String> templateParam, String dbalias) throws ParseException, IOException, CustomException, SQLException {
        String lines = new PrepareBody(this.scenario.getUri().replaceFirst(FILE, ""), sqlFile).loadBody();
        String filledTempl = new Templater(lines, templateParam).fillTemplate();
        log.debug("Executing SQL Query: {}", filledTempl);
        StatementExecute exec = new StatementExecute();
        return exec.executeSQLQueryNew(configer.getApplicationProperties(), filledTempl, dbalias);
    }

    private void executeSqlRequestWithoutReturn(String sqlFile, Map<String, String> templateParam, String dbalias) throws ParseException, IOException, CustomException, SQLException {
        String lines = new PrepareBody(this.scenario.getUri().replaceFirst(FILE, ""), sqlFile).loadBody();
        String filledTempl = new Templater(lines, templateParam).fillTemplate();
        log.debug("Executing SQL Query: {}", filledTempl);
        StatementExecute exec = new StatementExecute();
        exec.executeUpdateOrInsertSQLQuery(configer.getApplicationProperties(), filledTempl, dbalias);
    }

    @Тогда("^Убедиться в наличии записей в БД ([^\"]*) по запросу ([^\"]*)$")
    public void checkIfDbHasData(String dbAlias, String sqlrequest) throws CustomException, SQLException, ParseException, IOException {
        TestVars testVars = LocalThead.getTestVars();
        testVars.setQueryResult(executeSqlRequestWithReturn(sqlrequest, testVars.getVariables(), dbAlias));
        Map<String, List<String>> queryresult = testVars.getQueryResult();
        boolean isReturnNonEmpty = false;
        for (String s : queryresult.keySet()) {
            if (queryresult.get(s).size() > 0) {
                isReturnNonEmpty = true;
                break;
            }
        }
        Assert.assertTrue(String.format("Выборка по запросу %s пуста", sqlrequest), isReturnNonEmpty);
        log.info(String.format("Выборка по запросу %s не пуста", sqlrequest));
    }

    @Тогда("^Убедиться что kafka-consumer ([^\"]*) получил сообщение содержащее ([^\"]*)")
    public void checkConsumerRecievedContainsMessage(String kafkaConsumerAlias, String containsvar) throws CustomException {
        TestVars testVars = LocalThead.getTestVars();
        KafkaConsumerImpl consumer = testVars.getKafkaConsumer(kafkaConsumerAlias);

        boolean isMessageFound = false;
        if (consumer.getConsumerRecordList().size() > 0) {
            for (ConsumerRecord<String, String> consumerRecord : consumer.getConsumerRecordList()) {
                if (consumerRecord.value().contains(testVars.getVariables().get(containsvar))) {

                    isMessageFound = true;
                }
            }
        } else {
            throw new CustomException("Consumer hasn't recieved no messages");
        }
        Assert.assertTrue(String.format("No messages with value %s, found", containsvar), isMessageFound);
        log.info(String.format("Messages with value %s, found", containsvar));
    }

    @Тогда("^Убедиться что в топике кафки ([^\"]*) в период с ([^\"]*) по ([^\"]*) пришло сообщение содержащее ([^\"]*)$")
    public void checkKafkaTopicContainsBetweenDates(String topicname, String startDateString, String endDateString, String containsVar) throws java.text.ParseException {
        TestVars testVars = LocalThead.getTestVars();
        List<ConsumerRecord<String, String>> filteredConsRecords = getfilteredRecords(topicname, startDateString, endDateString);
        boolean isContainsFound = false;
        for (ConsumerRecord<String, String> consumerRecord : filteredConsRecords) {
            if (consumerRecord.value().contains(testVars.getVariables().get(containsVar))) {
                isContainsFound = true;
                break;
            }
        }
        Assert.assertTrue(String.format("No messages with value %s, found", containsVar), isContainsFound);
        log.info(String.format("Messages with value %s, found", containsVar));
    }

    @Тогда("^Убедиться что в топике кафки ([^\"]*) в период ([^\"]*) - ([^\"]*) пришло сообщение содержащее ([^\"]*)$")
    public void checkKafkaTopicContainsBetweenDatesInVariable(String topicname, String variableStartDate, String variableEndDate, String containsVar) throws java.text.ParseException {
        TestVars testVars = LocalThead.getTestVars();
        String startDate = testVars.getVariables().get(variableStartDate);
        String endDate = testVars.getVariables().get(variableEndDate);
        checkKafkaTopicContainsBetweenDates(topicname, startDate, endDate, containsVar);
    }

    @Тогда("^Выполнить обновление БД ([^\"]*) запросом ([^\"]*)")
    public void makeInsertOrUpdateDB(String dbalias, String sqlrequest) throws CustomException, SQLException, ParseException, IOException {
        TestVars testVars = LocalThead.getTestVars();
        executeSqlRequestWithoutReturn(sqlrequest, testVars.getVariables(), dbalias);
    }

    @Тогда("^Сохранить kafka-сообщение ([^\"]*) содержащее ([^\"]*), пришедшее топик ([^\"]*) в период ([^\"]*) - ([^\"]*)$")
    public void saveKafkaMessage(String kafkaMessageAlias, String containsVar, String topicname, String startDateString, String endDateString) throws java.text.ParseException {
        TestVars testVars = LocalThead.getTestVars();
        List<ConsumerRecord<String, String>> filteredConsRecords = getfilteredRecords(topicname, startDateString, endDateString);
        boolean isMessageStored = false;

        for (ConsumerRecord<String, String> consumerRecord : filteredConsRecords) {
            if (consumerRecord.value().contains(testVars.getVariables().get(containsVar))) {
                testVars.addMessage(kafkaMessageAlias, new KafkaMessage(consumerRecord.value(), consumerRecord.key(), consumerRecord.headers()));
                isMessageStored = true;
                break;
            }
        }
        Assert.assertTrue(String.format("Message with %s not found", containsVar), isMessageStored);

    }

    @Тогда("^Сохранить ключ kafka-сообщения ([^\"]*) в переменную ([^\"]*)$")
    public void saveMessageKeyToVar(String messageAlias, String varname) {
        TestVars testVars = LocalThead.getTestVars();
        KafkaMessage kafkaMessage = (KafkaMessage) testVars.getMessage(messageAlias);
        testVars.setVariables(varname, kafkaMessage.getKey());
        log.info(String.format("Variable with value %s stored to %s", kafkaMessage.getKey(), varname));
    }

    @Тогда("^Сохранить ([^\"]*) из json-тела сообщения ([^\"]*) в переменную ([^\"]*)$")
    public void saveJsonValue(String jsonPath, String messageAlias, String varname) {
        TestVars testVars = LocalThead.getTestVars();
        String messagebody = testVars.getMessage(messageAlias).getBody();
        String jsonPathVal = getValueFromJsonPath(messagebody, jsonPath);
        testVars.setVariables(varname, jsonPathVal);
        log.info(String.format("Variable with value %s stored to %s", jsonPathVal, varname));
    }

    @Тогда("^Сохранить ([^\"]*) из json-тела http-сообщения ([^\"]*) в переменную ([^\"]*)$")
    public void saveJsonValueHttp(String jsonPath, String messageAlias, String varname) {
        TestVars testVars = LocalThead.getTestVars();
        String messagebody = testVars.getVariables().get(messageAlias);
        String jsonPathVal = getValueFromJsonPath(messagebody, jsonPath);
        testVars.setVariables(varname, jsonPathVal);
        log.info(String.format("Variable with value %s stored to %s", jsonPathVal, varname));
    }

    @Тогда("^Сохранить ([^\"]*) из json-тела последнего http-сообщения в переменную ([^\"]*)$")
    public void saveJsonValueHttp(String jsonPath, String varname) {
        TestVars testVars = LocalThead.getTestVars();
        String messagebody = testVars.getResponse().getBody();
        String jsonPathVal = getValueFromJsonPath(messagebody, jsonPath);
        testVars.setVariables(varname, jsonPathVal);
        log.info(String.format("Variable with value %s stored to %s", jsonPathVal, varname));
    }

    @Тогда("^Найти и сохранить kafka-сообщение ([^\"]*) из топика ([^\"]*), пришедшее в период ([^\"]*) - ([^\"]*) отвечающее шаблону")
    public void findAndSaveJsonMessage(String messageAlias, String topicName, String startDateVar, String endDateVar, DataTable dataTable) throws java.text.ParseException {
        ConsumerRecord<String, String> foundrecord = getMessageComparedToDataTable(startDateVar, endDateVar, topicName, dataTable);
        TestVars testVars = LocalThead.getTestVars();
        testVars.addMessage(messageAlias, new KafkaMessage(foundrecord.value(), foundrecord.key(), foundrecord.headers()));
        System.out.println("ddddd");
    }

    @Тогда("^Убедиться в наличии kafka-сообщения из топика ([^\"]*), пришедшее в период ([^\"]*) - ([^\"]*) отвечающее шаблону")
    public void findAndSaveJsonMessage(String topicName, String startDateVar, String endDateVar, DataTable dataTable) throws java.text.ParseException {
        getMessageComparedToDataTable(startDateVar, endDateVar, topicName, dataTable);
    }

    @Тогда("Сохранить заголовок ([^\"]*) kafka-сообщения ([^\"]*) в переменную ([^\"]*)")
        public void saveHeaderToVar(String headkey,String mesaageAlias,String variable){
        TestVars testVars = LocalThead.getTestVars();
        KafkaMessage message = (KafkaMessage) testVars.getMessage(mesaageAlias);
        Header[] headers = message.getKafkaHeaders();
        boolean isvarSet = false;
        for (Header header:headers) {
            if (header.key().equals(headkey)){
                testVars.setVariables(variable,new String(header.value()));
                isvarSet=true;
                log.info(String.format("Header value %s saved as %s",new String(header.value()),variable));
                break;
            }
        }
        Assert.assertTrue(String.format("Header %s was not found",headkey),isvarSet);
    }


    private Map<String, String> getMapfromProperties(String propertiesName) throws CustomException, IOException, ParseException {
        TestVars testVars = LocalThead.getTestVars();
        String prep = new PrepareBody(this.scenario.getUri().replaceFirst(FILE, ""), propertiesName).loadBody();
        String filledTempl = new Templater(prep, testVars.getVariables()).fillTemplate();

////        Templater filledTempl = new Templater(prep, testVars.getVariables()).fillTemplate();
//        prep.cutPath();
        Properties properties = new Properties();
        HashMap<String, String> map = new HashMap<>();
        properties.load(new StringReader(filledTempl));
        for (final String name : properties.stringPropertyNames()) {
            map.put(name, properties.getProperty(name));
        }


        return map;
    }

    private List<ConsumerRecord<String, String>> getfilteredRecords(String topicname, String startDateString, String endDateString) throws java.text.ParseException {
        KafkaConsumerImpl kafkaConsumerer = new KafkaConsumerImpl(topicname, true);
        kafkaConsumerer.doConsume();
        List<ConsumerRecord<String, String>> consumerRecords = kafkaConsumerer.getConsumerRecordList();
        kafkaConsumerer.stopConsume();

        DateFormat df = new SimpleDateFormat("dd.MM.yyyy'T'HH:mm");
        Date startDate = df.parse(startDateString);
        Date endDate = df.parse(endDateString);
        List<ConsumerRecord<String, String>> filteredConsRecords = new ArrayList<>();


        for (int i = 0; i < consumerRecords.size(); i++) {
            Date checkdate = new Date(consumerRecords.get(i).timestamp());
            if (checkdate.after(startDate) && checkdate.before(endDate)) {
                filteredConsRecords.add(consumerRecords.get(i));
            }
        }
        return filteredConsRecords;
    }

    private String getValueFromJsonPath(String jsonmessage, String jsonPath) {
        JSONObject jsonObject = new JSONObject(jsonmessage);
        String jsonPaths[] = jsonPath.split(":");
        for (int i = 0; i < jsonPaths.length - 1; i++) {
            try {
                jsonObject = (JSONObject) jsonObject.get(jsonPaths[i]);
            }catch (JSONException e){
                return "NOCURRENTKEY";
            }
        }
        return jsonObject.get(jsonPaths[jsonPaths.length - 1]).toString();
    }

    String replaceTestVariableValue(String oldValue, TestVars testVars) {
        String[] vars = oldValue.split("\\$");
        String finalEntry = oldValue;
        for (int i = 1; i < vars.length; i++) {
            String var = vars[i].substring(vars[i].indexOf('{') + 1, vars[i].indexOf('}'));
            finalEntry = finalEntry.replace("${" + var + "}", testVars.getVariables().get(var));
        }
        return finalEntry;
    }

    private ConsumerRecord<String, String> getMessageComparedToDataTable(String startDateVar, String endDateVar, String topicName, DataTable dataTable) throws java.text.ParseException {
        TestVars testVars = LocalThead.getTestVars();
        String startDate = testVars.getVariables().get(startDateVar);
        String endDate = testVars.getVariables().get(endDateVar);
        List<ConsumerRecord<String, String>> filteredConsRecords = getfilteredRecords(topicName, startDate, endDate);
        HashMap<String, String> valuesmap = new HashMap<>();
        valuesmap.putAll(dataTable.asMap(String.class, String.class));
        ConsumerRecord<String, String> foundrecord = filteredConsRecords.get(0); //Костыль, т.к. необходимо проинициировать, иначе при возврате будет ругаться.

        for (String key : valuesmap.keySet()) {
            valuesmap.put(key, replaceTestVariableValue(valuesmap.get(key), testVars));
        }

        boolean isMessageFound = true;
        for (ConsumerRecord<String, String> consumerRecord : filteredConsRecords) {
            isMessageFound = true;
            foundrecord = consumerRecord;

            for (String s : valuesmap.keySet()) {
                if (s.startsWith("HEADER:")) {
                    if (!checkHeaderValue(s, consumerRecord, valuesmap)) {
                        isMessageFound = false;
                        break;
                    }
                } else {
                    if (!getValueFromJsonPath(consumerRecord.value(), s).equals(valuesmap.get(s))) {
                        isMessageFound = false;
                        break;
                    }
                }
            }
            if (isMessageFound) {
                break;
            }
        }
        if (isMessageFound) {
            return foundrecord;
        } else {
            Assert.assertTrue(String.format("Сообщение по шаблону не найдено"), isMessageFound);
            return null;
        }

    }

    private boolean checkHeaderValue(String input, ConsumerRecord<String, String> consumerRecord, HashMap<String, String> valuesmap) {
        String key = input;
        Header[] headers = consumerRecord.headers().toArray();
        boolean result = false;
        for (Header header : headers) {
            if (header.key().equals(key.replaceAll("HEADER:", "")) && new String(header.value()).equals(valuesmap.get(key))) {
                result = true;
                break;
            }
        }
        return result;
    }
}
