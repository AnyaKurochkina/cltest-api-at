package models;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import core.enums.ObjectStatus;
import core.exception.CalculateException;
import core.exception.CreateEntityException;
import core.helper.DataFileHelper;
import core.helper.StringUtils;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.Step;
import io.qameta.allure.aspects.StepsAspects;
import io.qameta.allure.model.Parameter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import models.authorizer.ServiceAccount;
import models.keyCloak.ServiceAccountToken;
import models.orderService.interfaces.IProduct;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.platform.engine.support.hierarchical.ForkJoinPoolHierarchicalTestExecutorService;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static io.qameta.allure.Allure.getLifecycle;

@Log4j2
public class ObjectPoolService {
    private static final Map<String, ObjectPoolEntity> entities = Collections.synchronizedMap(new LinkedHashMap<>());
    public static final List<String> deleteClassesName = Collections.synchronizedList(new ArrayList<>());
    public static final List<String> createdEntities = Collections.synchronizedList(new ArrayList<>());

    @SneakyThrows
    public static <T extends Entity> T create(Entity e, boolean exclusiveAccess, boolean isPublic) {
        ObjectPoolEntity objectPoolEntity = createObjectPoolEntity(e);
        objectPoolEntity.setPublic(isPublic);
        objectPoolEntity.lock();
        if (objectPoolEntity.getStatus().equals(ObjectStatus.FAILED)) {
            objectPoolEntity.release();
            throw new CreateEntityException(String.format("Объект: %s, необходимый для выполнения теста был создан с ошибкой:\n%s",
                    objectPoolEntity.getClazz().getSimpleName(), objectPoolEntity.getError()));
        }
        if (objectPoolEntity.getStatus() == ObjectStatus.NOT_CREATED) {
            try {
                e.init();
                e.create();
                e.save();
                createdEntities.add(e.uuid);
                if (!deleteClassesName.contains(e.getClass().getName()))
                    deleteClassesName.add(0, e.getClass().getName());
            } catch (Throwable throwable) {
                if (throwable instanceof CalculateException) {
                    objectPoolEntity.setStatus(ObjectStatus.CREATED);
                } else {
                    objectPoolEntity.setStatus(ObjectStatus.FAILED);
                    objectPoolEntity.setError(throwable);
                }
                objectPoolEntity.release();
                throw throwable;
            }
            objectPoolEntity.setStatus(ObjectStatus.CREATED);
        }
        if (!exclusiveAccess) {
            objectPoolEntity.release();
        }
        T entity = objectPoolEntity.get();
        toStringProductStep(entity);
        if (Objects.nonNull(objectPoolEntity.getError())) {
            throw objectPoolEntity.getError();
        }
        return entity;
    }

    private static final List<ObjectPoolEntity> listObject = new ArrayList<>();

    public static synchronized ObjectPoolEntity createObjectPoolEntity(Entity e) {
        listObject.clear();
        listObject.addAll(entities.values());
        Collections.shuffle(listObject);
        for (ObjectPoolEntity objectPoolEntity : listObject) {
            if (objectPoolEntity.equalsEntity(e)) {
                return objectPoolEntity;
            }
        }
        return writeEntityToMap(e);
    }

    public static void saveEntity(Entity entity) {
        ObjectPoolEntity objectPoolEntity = getObjectPoolEntity(entity);
        if (objectPoolEntity != null) {
            objectPoolEntity.set(entity);
        }
    }

    public static ObjectPoolEntity getObjectPoolEntity(Entity entity) {
        return entities.get(entity.uuid);

    }

    private static ObjectPoolEntity writeEntityToMap(Entity entity) {
        entity.objectClassName = entity.getClass().getName();
        entity.uuid = UUID.randomUUID().toString();
        ObjectPoolEntity objectPoolEntity = new ObjectPoolEntity(entity);
        entities.put(entity.uuid, objectPoolEntity);
        return objectPoolEntity;
    }

    @SneakyThrows
    public static void deleteAllResources() {
        log.debug("##### deleteAllResources start #####");
        ExecutorService threadPool = Executors.newFixedThreadPool(ForkJoinPoolHierarchicalTestExecutorService.parallelism.get());
        Collections.reverse(createdEntities);
        for (String key : createdEntities) {
            ObjectPoolEntity objectPoolEntity = entities.get(key);
            if (objectPoolEntity.getClazz().getName().endsWith("UserToken") || objectPoolEntity.getClazz().getName().endsWith("ServiceAccountToken"))
                continue;
            if (objectPoolEntity.getStatus() != ObjectStatus.CREATED)
                continue;
//            if (objectPoolEntity.isMock())
//                continue;
            Entity entity = objectPoolEntity.get();
            if (entity instanceof IProduct) {
                threadPool.submit(() -> {
                    try {
                        entity.deleteObject();
                    } catch (Throwable e) {
                        objectPoolEntity.setStatus(ObjectStatus.FAILED_DELETE);
                        objectPoolEntity.setError(e);
                        e.printStackTrace();
                    }
                });
            } else {
                try {
                    entity.deleteObject();
                } catch (Throwable e) {
                    objectPoolEntity.setStatus(ObjectStatus.FAILED_DELETE);
                    objectPoolEntity.setError(e);
                    e.printStackTrace();
                }
            }
        }
        awaitTerminationAfterShutdown(threadPool);
        log.debug("##### deleteAllResources end #####");
    }

    public static void removeProducts(Set<Class<?>> currentClassListArgument) {
        for (String key : createdEntities) {
            ObjectPoolEntity objectPoolEntity = entities.get(key);
            synchronized (ObjectPoolService.class) {
                if (objectPoolEntity == null) {
                    log.error("Key " + key + " is null");
                }
                if (objectPoolEntity.getStatus() == null) {
                    log.error("Key getStatus() " + key + " is null");
                }
                if (objectPoolEntity.getStatus() != ObjectStatus.CREATED)
                    continue;
                if (currentClassListArgument.contains(objectPoolEntity.getClazz()))
                    continue;
                Entity entity = objectPoolEntity.get();
                if (!(entity instanceof IProduct))
                    continue;
                objectPoolEntity.setStatus(ObjectStatus.NOT_CREATED);
            }
            removeProduct(objectPoolEntity);
        }
    }

    public static void removeProduct(ObjectPoolEntity objectPoolEntity) {
        Entity entity = objectPoolEntity.get();
        try {
            log.debug("##### removeProduct {} #####", entity);
            entity.deleteObject();
        } catch (Throwable e) {
            objectPoolEntity.setStatus(ObjectStatus.FAILED_DELETE);
            objectPoolEntity.setError(e);
            e.printStackTrace();
        }
    }


    private static void awaitTerminationAfterShutdown(ExecutorService threadPool) {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(1, TimeUnit.HOURS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException ex) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public static void saveEntities(String file) {
        try {
            JSONArray array = new JSONArray();
            for (ObjectPoolEntity objectPoolEntity : entities.values()) {
                array.put(new JSONObject(objectPoolEntity.toString()));
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

    public static void loadEntities(String content) {
        try {
            List<LinkedHashMap<String, Object>> listEntities = new ObjectMapper().readValue(content, new TypeReference<List<LinkedHashMap<String, Object>>>() {
            });
            listEntities.forEach(v -> {
                        ObjectPoolEntity objectPoolEntity = writeEntityToMap(fromJson(new JSONObject(v).toString(), getClassByName(v.get("objectClassName").toString())));
                        objectPoolEntity.setStatus(ObjectStatus.CREATED);
//                            objectPoolEntity.setMock(true);
                    }
            );
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

    private static void toStringProductStep(Entity entity) {
        if (entity instanceof ServiceAccount || entity instanceof ServiceAccountToken)
            return;
        toStringProductStepFunc(entity);
    }


    @Step
    @SneakyThrows
    private static void toStringProductStepFunc(Entity entity) {
        AllureLifecycle allureLifecycle = getLifecycle();
        String id = allureLifecycle.getCurrentTestCaseOrStep().orElse(null);
        List<Parameter> list = new ArrayList<>();
        Map<String, String> parametersMap = new HashMap<>();
        List<Field> fieldList = new ArrayList<>(Arrays.asList(entity.getClass().getSuperclass().getDeclaredFields()));
        fieldList.addAll(Arrays.asList(entity.getClass().getDeclaredFields()));
        for (Field field : fieldList) {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers()))
                continue;
            field.setAccessible(true);
            if (field.get(entity) != null) {
                Parameter parameter = new Parameter();
                parameter.setName(field.getName());
                String value = field.get(entity).toString();
                if (field.getName().equals("password"))
                    value = "<password>";
                parametersMap.put(field.getName(), value);
                parameter.setValue(value);
                list.add(parameter);
            }
        }
        if (Objects.nonNull(id)) {
            allureLifecycle.updateStep(id, s -> s.setName("Получена сущность " + entity.getClass().getSimpleName() + " с параметрами"));
            allureLifecycle.updateStep(id, s -> s.setParameters(list));
        }
        StepsAspects.getCurrentStep().get().setTitle(StringUtils.format("Получена сущность {} с параметрами", entity.getClass().getSimpleName()));
        StepsAspects.getCurrentStep().get().setParameters(parametersMap);
    }
}
