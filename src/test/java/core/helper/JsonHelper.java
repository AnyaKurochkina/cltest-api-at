package core.helper;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JsonOrgJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import java.io.*;
import java.util.*;
import static steps.Steps.dataJson;

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


 //   private ConcurrentHashMap<String, ConcurrentHashMap<String, String>> allTests = new ConcurrentHashMap<>();
 //   public ConcurrentHashMap<String, String> testValues = new ConcurrentHashMap<>();
//    public volatile static ConcurrentHashMap<String, String> shareData = new ConcurrentHashMap<>();
//    volatile static ReadWriteLock lock = new ReentrantReadWriteLock();


//    public void putFromJsonToShare(String file) {
//        try {
//            lock.readLock().lock();
//            if (Files.exists(Paths.get(dataFolder + file))) {
//                FileInputStream fileInputStream = new FileInputStream(dataFolder + file);
//                Map result = new ObjectMapper().readValue(fileInputStream, HashMap.class);
//                shareData.putAll(result);
//            }
//            lock.readLock().unlock();
//        } catch (Exception ex) {
//            lock.readLock().unlock();
//            ex.printStackTrace();
//        }
//    }

//    public void writeJsonFileFromHashMap(String filePath) {
//        JSONObject jsonObject = new JSONObject();
//        for (Map.Entry<String, String> entry : shareData.entrySet()) {
//            String key = entry.getKey();
//            String value = entry.getValue();
//            jsonObject.put(key, value);
//        }
//        try {
//            lock.writeLock().lock();
//            DataFileHelper.write(filePath, jsonObject.toString());
//            lock.writeLock().unlock();
//        } catch (Exception ex) {
//            lock.writeLock().unlock();
//            ex.printStackTrace();
//        }
//    }

    public String getStringFromFile(String s) {
        try {
            File file = new File(dataJson + s);
            return FileUtils.readFileToString(file, "UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Error(ex.getMessage());
        }
    }

//    public JSONObject getJsonObjectFromFile(String filename, String key) {
//        JSONObject jsonObject = getJsonFromFile(filename);
//        return (JSONObject) jsonObject.query(key);
//    }

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
