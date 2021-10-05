package core.helper;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.Step;
import io.qameta.allure.model.Parameter;
import lombok.SneakyThrows;
import models.Entity;
import models.keyCloak.UserToken;
import models.orderService.interfaces.IProduct;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Assume;

import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static io.qameta.allure.Allure.getLifecycle;


public class ObjectPoolService {
    private static final Map<String, ObjectPoolEntity> entities = Collections.synchronizedMap(new LinkedHashMap<>());
    public static final List<String> deleteClassesName = Collections.synchronizedList(new ArrayList<>());

    public static <T extends Entity> T create(Entity e, boolean exclusiveAccess) {
        ObjectPoolEntity objectPoolEntity = createObjectPoolEntity(e);
        System.out.println(e + "ПреЛок");
        objectPoolEntity.lock();
        System.out.println(e + " ПостЛок");
        if (objectPoolEntity.isFailed()) {
            objectPoolEntity.release();
            Assume.assumeFalse("Object is failed", objectPoolEntity.isFailed());
        }
        if (!objectPoolEntity.isCreated()) {
            try {
                if (!deleteClassesName.contains(e.getClass().getName()))
                    deleteClassesName.add(0, e.getClass().getName());
                e.init();
                e.create();
                e.save();
            } catch (Throwable throwable) {
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
        T entity = objectPoolEntity.get();
        toStringProductStep(entity);
        return entity;
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

    public static ObjectPoolEntity getObjectPoolEntity(Entity entity) {
//        if(entity.uuid == null)
//            System.out.println(1);
        return entities.get(entity.uuid);
    }

    private static ObjectPoolEntity writeEntityToMap(Entity entity) {
        entity.objectClassName = entity.getClass().getName();
        entity.uuid = UUID.randomUUID().toString();
        ObjectPoolEntity objectPoolEntity = new ObjectPoolEntity(entity);
        entities.put(entity.uuid, objectPoolEntity);
        return objectPoolEntity;
    }

    public static void deleteAllResources() {
        List<Thread> threadList = new ArrayList<>();
        List<String> reverseOrderedKeys = new ArrayList<>(entities.keySet());
        Collections.reverse(reverseOrderedKeys);
            for (String key : reverseOrderedKeys) {
                ObjectPoolEntity objectPoolEntity = entities.get(key);
                if(objectPoolEntity.getClazz().getName().endsWith("UserToken") || objectPoolEntity.getClazz().getName().endsWith("ServiceAccountToken"))
                    continue;
                if (!objectPoolEntity.isCreated())
                    continue;
                    Entity entity = objectPoolEntity.get();
                    if(entity instanceof IProduct) {
                        Thread thread = new Thread(entity::deleteObject);
                        threadList.add(thread);
                        thread.start();
                    }
                    else entity.deleteObject();
            }
            threadList.forEach(thread -> {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(1);
            });
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
                            objectPoolEntity.setMock(true);
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

    @Step
    @SneakyThrows
    private static void toStringProductStep(Entity entity) {
        AllureLifecycle allureLifecycle = getLifecycle();
        String id = allureLifecycle.getCurrentTestCaseOrStep().orElse(null);
        if(id == null)
            return;
        List<Parameter> list = new ArrayList<>();
        List<Field> fieldList = new ArrayList<>(Arrays.asList(entity.getClass().getSuperclass().getDeclaredFields()));
        fieldList.addAll(Arrays.asList(entity.getClass().getDeclaredFields()));
        for (Field field : fieldList) {
            if (Modifier.isStatic(field.getModifiers()))
                continue;
            field.setAccessible(true);
            if (field.get(entity) != null) {
                Parameter parameter = new Parameter();
                parameter.setName(field.getName());
                parameter.setValue(field.get(entity).toString());
                list.add(parameter);
            }
        }
        allureLifecycle.updateStep(id, s -> s.setName("Получена сущность " + entity.getClass().getSimpleName() + " с параметрами"));
        allureLifecycle.updateStep(id, s -> s.setParameters(list));
    }


}
