package core.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JsonOrgJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import core.vars.TestVars;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static junit.framework.TestCase.fail;
import static steps.Steps.dataFolder;

@Log4j2
@Data
public class JsonHelper {

    static {
        Configuration.setDefaults(new Configuration.Defaults() {

            private final JsonProvider jsonProvider = new JsonOrgJsonProvider();
            private final MappingProvider mappingProvider = new JacksonMappingProvider();

            @Override
            public JsonProvider jsonProvider() {
                return jsonProvider;
            }

            @Override
            public MappingProvider mappingProvider() {
                return mappingProvider;
            }

            @Override
            public Set<Option> options() {
                return EnumSet.noneOf(Option.class);
            }
        });
    }


    private ConcurrentHashMap<String, ConcurrentHashMap<String, String>> allTests = new ConcurrentHashMap<String, ConcurrentHashMap<String, String>>();
    public ConcurrentHashMap<String, String> testValues = new ConcurrentHashMap<String, String>();
    public volatile static ConcurrentHashMap<String, String> shareData = new ConcurrentHashMap<>();
    volatile static ReadWriteLock lock = new ReentrantReadWriteLock();

    private static void readJsonFileWithPath(String fullFilePath) {
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) JSONValue.parseWithException(DataFileHelper.read(fullFilePath));
        } catch (org.json.simple.parser.ParseException | IOException e) {
            e.printStackTrace();
        }
        //TestVars.setLastJsonData(jsonObject);
    }

    public static void readFileFromPath(String fileName, String catalogPath) {

        String fullPath = catalogPath.trim() + "/" + fileName.trim();
        if ("JSON".equals(FilenameUtils.getExtension(fullPath).toUpperCase()))
            readJsonFileWithPath(fullPath);
    }

    private void loadTest(String filename) {

        String datafolder = Configurier.getInstance().getAppProp("data.folder");
        readFileFromPath(filename, (new File(datafolder)).getAbsolutePath()); //читаем файл с тестовыми данными
        HashMap<String, String> tmp = new HashMap<String, String>(TestVars.getLastJsonData());
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

            ConcurrentHashMap<String, String> map2 = new ConcurrentHashMap<String, String>();
            for (ConcurrentHashMap.Entry<Object, Object> e : props.entrySet()) {
                map2.put(((String) e.getKey()).replaceAll("^\"(.*)\"$", "$1"), ((String) e.getValue()).replaceAll("^\"(.*)\"$", "$1"));
            }

            allTests.put(key, map2);
        }
    }

    // Чтение поля конкретного теста из файла в заданной папке
    public String getTestDataFieldValue(String filename, String TestID, String fieldName) {
        if (allTests.size() == 0) loadTest(filename);
        String res = null;
        if (allTests.containsKey(TestID)) {
            res = allTests.get(TestID).get(fieldName);
            //System.out.println(res);
        }
        if (res == null) {
            fail("Нет такого поля [" + fieldName + "] для теста [" + TestID + "]");
        }
        allTests.clear();
        return res;
    }

    private void loadAllTests() {
        String datafolder = Configurier.getInstance().getAppProp("data.folder");
        readFileFromPath("testdata.json", (new File(datafolder)).getAbsolutePath());
        HashMap<String, String> tmp = new HashMap<String, String>(TestVars.getLastJsonData());
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

            ConcurrentHashMap<String, String> map2 = new ConcurrentHashMap<String, String>();
            for (ConcurrentHashMap.Entry<Object, Object> e : props.entrySet()) {
                map2.put(((String) e.getKey()).replaceAll("^\"(.*)\"$", "$1"), ((String) e.getValue()).replaceAll("^\"(.*)\"$", "$1"));
            }

            allTests.put(key, map2);
        }
    }

    public void putFromJsonToShare(String file) {
        try {
            lock.readLock().lock();
            if (Files.exists(Paths.get(dataFolder + file))) {
                FileInputStream fileInputStream = new FileInputStream(dataFolder + file);
                HashMap result = new ObjectMapper().readValue(fileInputStream, HashMap.class);
                shareData.putAll(result);
            }
            lock.readLock().unlock();
        } catch (Exception ex) {
            lock.readLock().unlock();
            ex.printStackTrace();
        }
    }

    public void writeJsonFileFromHashMap(String filePath) {
        JSONObject jsonObject = new JSONObject();
        for (Map.Entry<String, String> entry : shareData.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            jsonObject.put(key, value);
        }
        try {
            lock.writeLock().lock();
            DataFileHelper.write(filePath, jsonObject.toString());
            lock.writeLock().unlock();
        } catch (Exception ex) {
            lock.writeLock().unlock();
            ex.printStackTrace();
        }
    }

    public String getStringFromFile(String s) {
        try {
            File file = new File(dataFolder + s);
            return FileUtils.readFileToString(file, "UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Error(ex.getMessage());
        }
    }

    public void getAllTestDataValues(String filename, String TestID) {
        if (allTests.size() == 0) {
            loadTest(filename);
        }

        if (allTests.containsKey(TestID)) {

            testValues.putAll(allTests.get(TestID));
        }
    }

    public JSONObject getJsonFromFile(String file) {
        try {
            return new JSONObject(getStringFromFile(file));
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Error(ex.getMessage());
        }
    }

    public JsonTemplate getJsonTemplate(String file) {
        return new JsonTemplate(getJsonFromFile(file));
    }

}
