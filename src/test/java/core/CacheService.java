package core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import core.helper.DataFileHelper;
import models.Entity;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Assert;
import java.io.FileInputStream;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class CacheService {
    private Class<?> c;
    static Map<String, String> entities = new HashMap<>();

    public EntityObject entity(Class<?> c) {
        this.c = c;
        return new EntityObject();
    }

    public static synchronized void saveEntities(String file) {
        try {
            JSONArray array = new JSONArray();
            JSONParser parser = new JSONParser();
            for (String entity : entities.values()) {
                array.put(parser.parse(entity));
            }
            DataFileHelper.write(file, array.toString(4));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized void loadEntities(String file) {
        try {
            if (Files.exists(Paths.get(file))) {
                FileInputStream fileInputStream = new FileInputStream(file);
                List<LinkedHashMap<String, Object>> listEntities = new ObjectMapper().readValue(fileInputStream, List.class);
                listEntities.forEach(v ->{
                    entities.put((String) v.get("objectUid"), new JSONObject(v).toString());
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void saveEntity(Entity e) {
        Class<?> c = e.getClass();
        if(e.objectUid == null)
            e.setObjectParams(c.getName());
        String serialize = new Gson().toJson(e, c);
        entities.put(e.objectUid, serialize);
    }

    public String toJson(Object e) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        return gsonBuilder.create().toJson(e, e.getClass());
    }

    public class EntityObject {
        Map<String, Comparable> fields = new HashMap<>();

        public synchronized EntityObject withField(String field, Comparable value) {
            fields.put(field, value);
            return this;
        }

        public synchronized <T extends Entity> T getEntityWithoutAssert() {
            for (String shareDataElement : entities.values()) {
                JsonObject jsonObject = JsonParser.parseString(shareDataElement).getAsJsonObject();
                String className = jsonObject.get("objectClassName").getAsString();
                if (c.getName().equals(className)) {
                    Class<T> act = null;
                    try {
                        act = (Class<T>) Class.forName(className);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Entity e = new Gson().fromJson(shareDataElement, (Type) act);
                    boolean matchAll = true;
                    for (Map.Entry<String, Comparable> filter : fields.entrySet()) {
                        JsonElement jsonElement = jsonObject.get(filter.getKey());
                        if (jsonElement != null) {
                            Comparable f = filter.getValue();
                            if (f instanceof String)
                                if (!jsonElement.getAsString().equals(filter.getValue()))
                                    matchAll = false;
                            if (f instanceof Boolean)
                                if (jsonElement.getAsBoolean() != (Boolean) filter.getValue())
                                    matchAll = false;
                            if (f instanceof Integer)
                                if (jsonElement.getAsInt() != (Integer) filter.getValue())
                                    matchAll = false;
                            if (f instanceof Float)
                                if (jsonElement.getAsFloat() != (Float) filter.getValue())
                                    matchAll = false;
                        } else matchAll = false;
                    }
                    if (matchAll)
                        return (T) e;
                }
            }
            return null;
        }

        public synchronized <T extends Entity> T getEntity() {
            Entity e = getEntityWithoutAssert();
            if(e == null)
                Assert.assertTrue("Невозможно получить " + c.getName() + " с параметрами: " + new JSONObject(fields).toString(), false);
            return (T) e;
        }
    }

}
