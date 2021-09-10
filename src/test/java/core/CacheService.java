package core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import core.helper.DataFileHelper;
import models.Entity;
import models.orderService.interfaces.IProduct;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.jupiter.api.parallel.ResourceLock;

import java.io.FileInputStream;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.parallel.ResourceAccessMode.*;

public class CacheService {
    private Class<?> c;
    private Map<String, Comparable> fields = new HashMap<>();
    private static final Map<String, String> entities = new ConcurrentHashMap<>();

    public CacheService entity(Class<?> c) {
        fields.clear();
        this.c = c;
        return this;
    }

    @ResourceLock(value = "entities", mode = READ_WRITE)
    public void saveEntity(Entity e) {
        Class<?> c = e.getClass();
        if (e.objectUid == null)
            e.setObjectParams(c.getName());
        String serialize = new Gson().toJson(e, c);
        entities.put(e.objectUid, serialize);
    }

    public static String toJson(Object e) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        return gsonBuilder.create().toJson(e, e.getClass());
    }

    public CacheService withField(String field, Comparable value) {
        fields.put(field, value);
        return this;
    }

    public CacheService forOrders(boolean isForOrders) {
        fields.put("isForOrders", isForOrders);
        return this;
    }

    @ResourceLock(value = "entities", mode = READ)
    public <T extends Entity> T getEntityWithoutAssert() {
        for (String shareDataElement : entities.values()) {
            JsonObject jsonObject = JsonParser.parseString(shareDataElement).getAsJsonObject();
            String classNameJson = jsonObject.get("objectClassName").getAsString();
            String className = c.getName();
            if (c.equals(IProduct.class))
                className = "models.orderService.products.";
            boolean isClass;
            if (classNameJson.endsWith(".")) {
                isClass = classNameJson.startsWith(className);
            } else {
                isClass = classNameJson.equals(className);
            }
            if (isClass) {
                Class<T> act = null;
                try {
                    act = (Class<T>) Class.forName(classNameJson);
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

    public <T extends Entity> T getEntity() {
        Entity e = getEntityWithoutAssert();
        Assume.assumeNotNull("Невозможно получить " + c.getName() + " с параметрами: " + new JSONObject(fields).toString(), e);
        return (T) e;
    }

    @ResourceLock(value = "entities", mode = READ_WRITE)
    public static void saveEntities(String file) {
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

    @ResourceLock(value = "entities", mode = READ)
    public static void loadEntities(String file) {
        try {
            if (Files.exists(Paths.get(file))) {
                FileInputStream fileInputStream = new FileInputStream(file);
                List<LinkedHashMap<String, Object>> listEntities = new ObjectMapper().readValue(fileInputStream, List.class);
                listEntities.forEach(v -> {
                    entities.put((String) v.get("objectUid"), new JSONObject(v).toString());
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
