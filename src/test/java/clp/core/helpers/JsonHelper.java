package clp.core.helpers;

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

    public static HashMap<String, String> getTestByID(String TestID) throws Exception {

        if (allTests == null) loadAllTests();

        if (allTests.containsKey(TestID)) {
            return allTests.get(TestID);
        } else {
            throw new Exception("Нет такого теста [" + TestID + "]");
        }
    }

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

    public static String getTestData(String TestID, String fieldName) {
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



