package clp.core.vars;


import io.cucumber.datatable.DataTable;
import org.apache.kafka.clients.producer.Producer;
import clp.core.iso.UniversalRequest;
import clp.core.iso.core.requests.ProcessingResponse;
import clp.core.kafka.impl.KafkaConsumerImpl;
import clp.core.messages.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestVars {
    private DataTable dataTable;
    private Message request;
    private Message response;
    private Map<String, String> variables = new HashMap<>();
    private Map<String, Producer> kafkaProducerMap = new HashMap<>();
    private Map<String, KafkaConsumerImpl> kafkaConsumerMap = new HashMap<>();
    private Map<String, Message> messages = new HashMap<>();

    public Map<String, KafkaConsumerImpl> getKafkaConsumerMap() {
        return kafkaConsumerMap;
    }

    public void addConsumer(String kafkaProducerAlias, KafkaConsumerImpl kafkaConsumer){
        kafkaConsumerMap.put(kafkaProducerAlias, kafkaConsumer);
    }

    public KafkaConsumerImpl getKafkaConsumer(String kafkaConsumerAlias){
        return kafkaConsumerMap.get(kafkaConsumerAlias);
    }

    public void addProducer(String kafkaProducerAlias, Producer kafkaProducer){
        kafkaProducerMap.put(kafkaProducerAlias, kafkaProducer);
    }

    public Producer getKafkaProducer(String kafkaProducerAlias){
        return kafkaProducerMap.get(kafkaProducerAlias);
    }

    public Map<String, Producer> getKafkaProducerMap(){
        return kafkaProducerMap;
    }

    public Map<String, List<String>> getQueryResult() {
        return queryResult;
    }

    public void setQueryResult(Map<String, List<String>> queryResult) {
        this.queryResult = queryResult;
    }

    public UniversalRequest getUniversalRequest() {
        return universalRequest;
    }

    public void setUniversalRequest(UniversalRequest universalRequest) {
        this.universalRequest = universalRequest;
    }

    public ProcessingResponse getProcessingResponse() {
        return processingResponse;
    }

    public void setProcessingResponse(ProcessingResponse processingResponse) {
        this.processingResponse = processingResponse;
    }

    private Map<String, List<String>> queryResult;
    private UniversalRequest universalRequest;
    private ProcessingResponse processingResponse;

    public TestVars() {
        //Comment for Sonar, explaining why this method is empty. Its empty because I decided it to be
    }


    public Map<String, String> getVariables() {
        return this.variables;
    }

    public DataTable getDataTable() {
        return dataTable;
    }

    public void setDataTable(DataTable dataTable) {
        this.dataTable = dataTable;
    }

    public Message getRequest() {
        return request;
    }

    public void setRequest(Message request) {
        this.request = request;
    }

    public Message getResponse() {
        return response;
    }

    public void setResponse(Message response) {
        this.response = response;
    }

    public void setVariables(String variableName, String tagValue) {
        variables.put(variableName, tagValue);
    }

    public Map<String, String> fillMapWithVariables(Map<String,String> map){
        Map<String,String> resultMap = new HashMap<>();
        resultMap.putAll(map);
        for (Map.Entry<String, String> entry: resultMap.entrySet()){
            if (resultMap.get(entry.getKey()).startsWith("$") && resultMap.get(entry.getKey()).endsWith("$")){
                String valueAlias = resultMap.get(entry.getKey()).replaceAll("\\$","");
                resultMap.put(entry.getKey(),variables.get(valueAlias));
            }
        }
        return resultMap;
    }
    public Message getMessage(String key){
        return messages.get(key);
    }

    public String getVariable(String key){
        return variables.get(key);
    }

    public void addMessage(String key, Message message){
        messages.put(key,message);
    }
}
