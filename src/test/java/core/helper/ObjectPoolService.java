package core.helper;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import models.Entity;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Assume;
import org.junit.jupiter.api.parallel.ResourceLock;

import java.io.FileInputStream;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class ObjectPoolService {
    private static final Map<String, ObjectPoolEntity> entities = new ConcurrentHashMap<>();
    public static final List<String> deleteClassesName = Collections.synchronizedList(new ArrayList<>());

    public static <T extends Entity> T create(Entity e, boolean exclusiveAccess) {
        ObjectPoolEntity objectPoolEntity = createObjectPoolEntity(e);
        System.out.println(e + "ПреЛок");
        objectPoolEntity.lock();
        System.out.println(e + " ПостЛок");
        if(objectPoolEntity.isFailed()) {
            objectPoolEntity.release();
            Assume.assumeFalse("Object is failed", objectPoolEntity.isFailed());
        }
        if (!objectPoolEntity.isCreated()) {
            try {
                if(!deleteClassesName.contains(e.getClass().getName()))
                    deleteClassesName.add(e.getClass().getName());
                objectPoolEntity.get().create().save();
            } catch (Throwable throwable){
                objectPoolEntity.setFailed(true);
                objectPoolEntity.release();
                throw throwable;
            }
            objectPoolEntity.setCreated(true);
        }
        if (!exclusiveAccess) {
            System.out.println(e + " ПреРелизНО");
            objectPoolEntity.release();
            System.out.println(e + " ПостРелизНО");
        }
        return objectPoolEntity.get();
    }

    public static synchronized ObjectPoolEntity createObjectPoolEntity(Entity e) {
        for (ObjectPoolEntity objectPoolEntity : entities.values()) {
            if (objectPoolEntity.equalsEntity(e)) {
                return objectPoolEntity;
            }
        }
        return writeEntityToMap(e);
    }


    public static <T extends Entity> T create(Entity e) {
        return create(e, false);
    }

    public static void saveEntity(Entity entity) {
        getObjectPoolEntity(entity).set(entity);
    }

    public static IEntity getObjectPoolEntity(Entity entity) {
        return entities.get(entity.uuid);
    }

    private static ObjectPoolEntity writeEntityToMap(Entity entity) {
        entity.objectClassName = entity.getClass().getName();
        String uuid = UUID.randomUUID().toString();
        entity.uuid = uuid;
        ObjectPoolEntity objectPoolEntity = new ObjectPoolEntity(entity);
        entities.put(uuid, objectPoolEntity);
        return objectPoolEntity;
    }

    public static void saveEntities(String file) {
        try {
            JSONArray array = new JSONArray();
            JSONParser parser = new JSONParser();
            for (ObjectPoolEntity objectPoolEntity : entities.values()) {
                array.put(parser.parse(objectPoolEntity.toString()));
            }
            DataFileHelper.write(file, array.toString(4));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String toJson(Object entity) {
        return new Gson().toJson(entity, entity.getClass());
    }

    public static <T extends Entity> T fromJson(String json, Class<?> classOfT) {
        return new Gson().fromJson(json, (Type) classOfT);
    }

    public static void loadEntities(String file) {
        try {
            if (Files.exists(Paths.get(file))) {
                FileInputStream fileInputStream = new FileInputStream(file);
                List<LinkedHashMap<String, Object>> listEntities = new ObjectMapper().readValue(fileInputStream, new TypeReference<List<LinkedHashMap<String, Object>>>() {
                });
                listEntities.forEach(v -> {
                            ObjectPoolEntity objectPoolEntity = writeEntityToMap(fromJson(new JSONObject(v).toString(), getClassByName(v.get("objectClassName").toString())));
                            objectPoolEntity.setCreated(true);
                        }
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
