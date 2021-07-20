package core.vars;


import io.restassured.response.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestVars {
    private Response respAssured;
    private Map<String, String> variables = new HashMap<>();

    private static ThreadLocal<HashMap<String, String>> lastJsonData = ThreadLocal.withInitial(() -> new HashMap<>());
    private static ThreadLocal<HashMap<String, String>> testData = new ThreadLocal<>();
    private static ThreadLocal<HashMap<String, String>> fieldParam = new ThreadLocal<>();
    private static ThreadLocal<HashMap<String, String>> tableData = new ThreadLocal<>();
    private static ThreadLocal<HashMap<String, String>> userData = new ThreadLocal<>();


    public static HashMap<String, String> getLastJsonData() {
        return lastJsonData.get();
    }

    public static void setLastJsonData(HashMap<String, String> hashMap) {
        lastJsonData.set(hashMap);
    }

    public Response getResp() {
        return respAssured;
    }

    public void setResp(Response respAssured) {
        this.respAssured = respAssured;
    }

    public static String getLastJsonDataValue(String key) {
        String value = "";
        if (isContainsInLastJsonData(key)) {
            return getLastJsonData().get(key);
        }
        return value;
    }

    public static boolean isContainsInLastJsonData(String key) {
        return getLastJsonData().containsKey(key);
    }
    public static HashMap<String, String> getTestData() {
        if (TestVars.testData.get() == null) {
            TestVars.testData.set(new HashMap<>());
        }
        return testData.get();
    }

    public static void setTestData(String key, String value) {
        getTestData().put(key, value);
    }

    public static String getTestDataValue(String key) {
        String value = "";
        if (isContainsInTestData(key)) {
            return getTestData().get(key);
        }
        return value;
    }

    public static boolean isContainsInTestData(String key) {
        return getTestData().containsKey(key);
    }

    /**
     * Для одного поля
     */
    public static void setFieldParam(String key, String value) {
        getFieldParam().put(key, value);
    }

    public static String getFieldParamValue(String key) {
        if (isContainsInFieldParam(key)) {
            return getFieldParam().get(key);
        }
        return key;
    }

    public static HashMap<String, String> getFieldParam() {
        if (TestVars.fieldParam.get() == null) {
            TestVars.fieldParam.set(new HashMap<>());
        }
        return fieldParam.get();
    }

    public static void setUserData(String key, String value) {
        getUserData().put(key, value);
    }

    public static String getUserDataValue(String key) {
        if (isContainsInUserData(key)) {
            return getUserData().get(key);
        }
        return key;
    }

    public static HashMap<String, String> getUserData() {
        if (TestVars.userData.get() == null) {
            TestVars.userData.set(new HashMap<>());
        }
        return userData.get();
    }

    public static boolean isContainsInUserData(String key) {
        return getUserData().containsKey(key);
    }

    public static boolean isContainsInFieldParam(String key) {
        return getFieldParam().containsKey(key);
    }
    /**-----------------*/

    /**
     * Для таблицы
     */
    public static void setTableData(String key, String value) {
        getTableData().put(key, value);
    }

    public static String getTableDataValue(String key) {
        String value = "";
        if (isContainsInTableData(key)) {
            return getTableData().get(key);
        }
        return value;
    }

    public static HashMap<String, String> getTableData() {
        if (TestVars.tableData.get() == null) {
            TestVars.tableData.set(new HashMap<>());
        }
        return tableData.get();
    }

    public static boolean isContainsInTableData(String key) {
        return getTableData().containsKey(key);
    }

    // Конец тестовых данных

    public Map<String, List<String>> getQueryResult() {
        return queryResult;
    }


    private Map<String, List<String>> queryResult;


    public TestVars() {
        //Comment for Sonar, explaining why this method is empty. Its empty because I decided it to be
    }

    public void setVariables(String variableName, String tagValue) {
        variables.put(variableName, tagValue);
    }

    public void setVariablesFromJson(Response response, String param) {
        if(response.jsonPath().get("[0]." + param)
                .getClass().getName().equalsIgnoreCase("java.lang.Integer")){
            setVariables(param, response.jsonPath().get("[0]." + param).toString());
        } else if(response.jsonPath().get("[0]." + param)
                .getClass().getName().equalsIgnoreCase("java.util.ArrayList")){
            ArrayList<String> arrayList = response.jsonPath().get("[0]." + param);
            setVariables(param, arrayList.get(0));
        }
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

    public String getVariable(String key){
        return variables.get(key);
    }

    public Map<String, String> getVariables() {
        return this.variables;
    }


}
