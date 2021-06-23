package clp.core.helpers;

import clp.core.exception.CustomException;
import clp.core.vars.TestVars;
import com.google.gson.Gson;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static clp.core.vars.TestVars.getSystemCommonSteps;
import static junit.framework.TestCase.fail;

public class JsonHelper {

    // Общие методы

    public List getJsonData(String file) throws IOException, ParseException {
        String filePath = Configurier.getInstance().getAppProp("data.folder") + "/" + file;

        FileReader reader = new FileReader(filePath);
        JSONParser jsonparser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonparser.parse(reader);
        String user = (String) jsonObject.get("пользователь");
        JSONArray arrayOfAuthorities = (JSONArray) jsonObject.get("полномочия");
        List<?> jsonList = new Gson().fromJson(arrayOfAuthorities.toString(),JSONArray.class);
        return jsonList;
    }

    // Клиенты из файла учетных данных clients.json

    private static HashMap<String, HashMap<String, String>> allClients;

    public static HashMap<String, String> getClientByType(String ClientType) throws Exception {
        if (allClients == null) loadAllClients();

        if (allClients.containsKey(ClientType)) {
            return allClients.get(ClientType);
        } else {
            throw new Exception("Нет такого клиента [" + ClientType + "]");
        }
    }

    private static void loadAllClients() {
        getSystemCommonSteps().readFileFromPath("clients.json", (new File("src/test/resources")).getAbsolutePath() + "/json/tests"); //читаем клиентов
        HashMap<String, String> tmp = new HashMap<String, String>(TestVars.getLastJsonData());

        allClients = new HashMap<String, HashMap<String, String>>();

        for (Map.Entry<String, String> entry : tmp.entrySet()) {

            String key = entry.getKey();
            Object val = entry.getValue();
            String str = val.toString();

            Properties props = new Properties();
            try {
                props.load(new StringReader(str.substring(1, str.length() - 1).replace(",", "\n")));
            } catch (Exception e) {
                fail("Не удалось распарсить значение из json для клиента[" + key + "]");
            }

            HashMap<String, String> map2 = new HashMap<String, String>();
            for (Map.Entry<Object, Object> e : props.entrySet()) {
                map2.put(((String) e.getKey()).replaceAll("^\"(.*)\"$", "$1"), ((String) e.getValue()).replaceAll("^\"(.*)\"$", "$1"));
            }

            allClients.put(key, map2);
        }
    }

    public static String getClientData(String ClientType, String fieldName) {
        if (allClients == null) loadAllClients();
        String res = null;
        if (allClients.containsKey(ClientType)) {
            res = allClients.get(ClientType).get(fieldName);
            System.out.println(res);
        }
        if (res == null) {
            // fail("Нет такого поля[" + fieldName + "] для типа клиента [" + ClientType + "]");
            res = "";
        }
        return res;
    }

    //  Клиенты из файла учетных данных clients.json - конец

    // Параметры теста из файла учетных данных testdata.json

    private static HashMap<String, HashMap<String, String>> allTests;

    public static HashMap<String, String> testValues = new HashMap<String,String>();

    public static HashMap<String, String> getTestByID(String TestID) throws Exception {

        if (allTests == null) loadAllTests();

        if (allTests.containsKey(TestID)) {
            return allTests.get(TestID);
        } else {
            throw new Exception("Нет такого теста [" + TestID + "]");
        }
    }

    // Чтение файла из папки внутри "src/test/resources"
    private static void loadTest(String filename, String testfolder) {
        getSystemCommonSteps().readFileFromPath(filename, (new File("src/test/resources")).getAbsolutePath() + testfolder); //читаем файл с тестовыми данными
        HashMap<String, String> tmp = new HashMap<String, String>(TestVars.getLastJsonData());

        allTests = new HashMap<String, HashMap<String, String>>();

        for (Map.Entry<String, String> entry : tmp.entrySet()) {

            String key = entry.getKey();
            Object val = entry.getValue();
            String str = val.toString();

            Properties props = new Properties();
            try {
                props.load(new StringReader(str.substring(1, str.length() - 1).replace(",", "\n")));
            } catch (Exception e) {
                fail("Не удалось распарсить значение из json для теста[" + key + "]");
            }

            HashMap<String, String> map2 = new HashMap<String, String>();
            for (Map.Entry<Object, Object> e : props.entrySet()) {
                map2.put(((String) e.getKey()).replaceAll("^\"(.*)\"$", "$1"), ((String) e.getValue()).replaceAll("^\"(.*)\"$", "$1"));
            }

            allTests.put(key, map2);
        }
    }
    // Чтение тестовых данных файла "testdata.json" из папки  "/json/tests" в каталоге  "src/test/resources"
    private static void loadAllTests() {
        getSystemCommonSteps().readFileFromPath("testdata.json", (new File("src/test/resources")).getAbsolutePath() + "/json/tests"); //читаем клиентов
        HashMap<String, String> tmp = new HashMap<String, String>(TestVars.getLastJsonData());

        allTests = new HashMap<String, HashMap<String, String>>();

        for (Map.Entry<String, String> entry : tmp.entrySet()) {

            String key = entry.getKey();
            Object val = entry.getValue();
            String str = val.toString();

            Properties props = new Properties();
            try {
                props.load(new StringReader(str.substring(1, str.length() - 1).replace(",", "\n")));
            } catch (Exception e) {
                fail("Не удалось распарсить значение из json для теста[" + key + "]");
            }

            HashMap<String, String> map2 = new HashMap<String, String>();
            for (Map.Entry<Object, Object> e : props.entrySet()) {
                map2.put(((String) e.getKey()).replaceAll("^\"(.*)\"$", "$1"), ((String) e.getValue()).replaceAll("^\"(.*)\"$", "$1"));
            }

            allTests.put(key, map2);
        }
    }
    // Чтение поля конкретного теста из файла в заданной папке
    public static String getTestDataFieldValue(String filename, String datafolder, String TestID, String fieldName) {
        if (allTests == null) loadTest(filename, datafolder);
        String res = null;
        if (allTests.containsKey(TestID)) {
            res = allTests.get(TestID).get(fieldName);
            System.out.println(res);
        }
        if (res == null) {
            fail("Нет такого поля [" + fieldName + "] для теста [" + TestID + "]");
        }
        allTests = null;
        return res;
    }

    // Чтение нескольких полей конкретного теста из файла в заданной папке
    public static void getAllTestDataValues(String filename, String datafolder, String TestID) {
        if (allTests == null) loadTest(filename, datafolder);

        if (allTests.containsKey(TestID)) {

            testValues.putAll(allTests.get(TestID));
            allTests = null;

        }
    }
    // Чтение массива полей конкретного теста из файла в заданной папке
    public static void getTestDataValues(String filename, String datafolder, String TestID, String[] TestFields) {
        if (allTests == null) loadTest(filename, datafolder);

        for (int i=0; i<TestFields.length;i++) {

            String value = "";
            if (allTests.containsKey(TestID)) {

                value = allTests.get(TestID).get(TestFields[i]);
                try {
                    testValues.put(TestFields[i], value);
                }
                catch (Exception exception) {
                    System.out.println(exception);
                }

            }
            if (value == null) {
                fail("Нет такого поля [" + TestFields[i] + "] для теста [" + TestID + "]");
            }
       }

        allTests = null;

    }


    // Чтение полей теста в дефолтной папке дефолтного файла
    public static String getAllTestData(String TestID, String fieldName) {
        if (allTests == null) loadAllTests();
        String res = null;
        if (allTests.containsKey(TestID)) {
            res = allTests.get(TestID).get(fieldName);
            System.out.println(res);
        }
        if (res == null) {
             fail("Нет такого поля [" + fieldName + "] для теста [" + TestID + "]");
        }
        allTests = null;
        return res;
    }


//  Параметры теста ты из файла учетных данных testdata.json - конец

}



