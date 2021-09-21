package core.helper;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import models.Entity;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileInputStream;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;



public class ObjectPoolService {
    private static final Map<String, ObjectPoolEntity> entities = new ConcurrentHashMap<>();

    public static <T extends Entity> T create(Entity e, boolean exclusiveAccess){
        for(ObjectPoolEntity objectPoolEntity : entities.values()) {
            if(objectPoolEntity.equalsEntity(e)){
                if(exclusiveAccess)
                    objectPoolEntity.lock();
                return objectPoolEntity.get();
            }
        }
        ObjectPoolEntity objectPoolEntity = writeEntityToMap(e.create());
        if(exclusiveAccess)
            objectPoolEntity.lock();
        return objectPoolEntity.get();
    }

    public static <T extends Entity> T create(Entity e){
        return create(e,false);
    }

    public static void saveEntity(Entity entity){
        getObjectPoolEntity(entity).set(entity);
    }

    public static IEntity getObjectPoolEntity(Entity entity){
        return entities.get(entity.uuid);
    }

    private static ObjectPoolEntity writeEntityToMap(Entity entity){
        entity.objectClassName = entity.getClass().getName();
        String uuid = UUID.randomUUID().toString();
        entity.uuid = uuid;
        ObjectPoolEntity objectPoolEntity = new ObjectPoolEntity(entity);
        entities.put(uuid,  objectPoolEntity);
        return objectPoolEntity;
    }

    public static void saveEntities(String file) {
        try {
            JSONArray array = new JSONArray();
            JSONParser parser = new JSONParser();
            for(ObjectPoolEntity objectPoolEntity : entities.values()) {
                array.put(parser.parse(objectPoolEntity.toString()));
            }
            DataFileHelper.write(file, array.toString(4));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String toJson(Object entity){
        return new Gson().toJson(entity, entity.getClass());
    }

    public static  <T extends Entity> T fromJson(String json, Class<?> classOfT) {
        return new Gson().fromJson(json, (Type) classOfT);
    }

    public static void loadEntities(String file) {
        try {
            if (Files.exists(Paths.get(file))) {
                FileInputStream fileInputStream = new FileInputStream(file);
                List<LinkedHashMap<String, Object>> listEntities = new ObjectMapper().readValue(fileInputStream, new TypeReference<List<LinkedHashMap<String, Object>>>() {});
                listEntities.forEach(v ->
                            writeEntityToMap(fromJson(new JSONObject(v).toString(), getClassByName(v.get("objectClassName").toString())))
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Class<?> getClassByName(String name) {
        Class<?> act = null;
        try {
            act = Class.forName(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return act;
    }




}
