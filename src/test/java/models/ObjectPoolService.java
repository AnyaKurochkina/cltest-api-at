package models;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.google.gson.Gson;
import core.enums.ObjectStatus;
import core.exception.CalculateException;
import core.exception.CreateEntityException;
import core.exception.NotFoundElementException;
import core.helper.Configure;
import core.helper.DataFileHelper;
import core.helper.StringUtils;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.Step;
import io.qameta.allure.model.Parameter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import models.cloud.authorizer.ServiceAccount;
import models.cloud.keyCloak.ServiceAccountToken;
import models.cloud.keyCloak.Token;
import models.cloud.keyCloak.UserToken;
import models.cloud.orderService.interfaces.IProduct;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.platform.engine.support.hierarchical.ForkJoinPoolHierarchicalTestExecutorService;
import org.opentest4j.TestAbortedException;
import ru.testit.junit5.StepsAspects;

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
    public static final ArrayDeque<String> createdEntities = new ArrayDeque<>();

    private static boolean isNeedLock(ObjectPoolEntity e, boolean exclusive) {
        return e.getStatus() == ObjectStatus.NOT_CREATED || exclusive;
    }

    @SneakyThrows
    public static <T extends Entity> T create(Entity e, boolean exclusiveAccess, boolean isPublic) {
        if (e.isSkip())
            throw new TestAbortedException("Нет конфигураций для объекта " + e.getClass().getSimpleName());
        ObjectPoolEntity objectPoolEntity;
        synchronized (ObjectPoolService.class) {
            objectPoolEntity = createObjectPoolEntity(e);
            objectPoolEntity.setPublic(isPublic);
        }
        if (isNeedLock(objectPoolEntity, exclusiveAccess)) {
            objectPoolEntity.lock();
            if (!exclusiveAccess && objectPoolEntity.getStatus().equals(ObjectStatus.CREATED))
                objectPoolEntity.release();
        }

        if (Configure.isTestItCreateAutotest && e instanceof IProduct) {
            if (isNeedLock(objectPoolEntity, exclusiveAccess))
                objectPoolEntity.release();
            throw new CreateEntityException("Создание объекта пропущено (isTestItCreateAutotest = true)");
        }

        if (objectPoolEntity.getStatus().equals(ObjectStatus.FAILED)) {
            if (isNeedLock(objectPoolEntity, exclusiveAccess))
                objectPoolEntity.release();
            if (e instanceof IProduct)
                ((IProduct) e).addLinkProduct();
            final CreateEntityException createEntityException = new CreateEntityException(String.format("Объект: %s, необходимый для выполнения теста был создан с ошибкой:\n%s",
                    objectPoolEntity.getClazz().getSimpleName(), objectPoolEntity.getError()));
            createEntityException.addSuppressed(objectPoolEntity.getError());
            throw createEntityException;
        }
        if (objectPoolEntity.getStatus() == ObjectStatus.NOT_CREATED) {
            try {
                e.init();
                e.create();
                e.uuid = objectPoolEntity.get().uuid;
                e.save();
                createdEntities.push(Objects.requireNonNull(e.uuid));
                if (!deleteClassesName.contains(e.getClass().getName()))
                    deleteClassesName.add(0, e.getClass().getName());
            } catch (Throwable throwable) {
                if (throwable instanceof CalculateException) {
                    objectPoolEntity.setStatus(ObjectStatus.CREATED);
                    createdEntities.push(Objects.requireNonNull(e.uuid));
                } else {
                    objectPoolEntity.setStatus(ObjectStatus.FAILED);
                    objectPoolEntity.setError(throwable);
                }
                if (e instanceof IProduct)
                    ((IProduct) e).addLinkProduct();
                objectPoolEntity.release();
                throw throwable;
            }
            objectPoolEntity.setStatus(ObjectStatus.CREATED);
            if (!exclusiveAccess)
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

    public static ObjectPoolEntity createObjectPoolEntity(Entity e) {
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

    public static <T extends Entity> T onlyGetObject(Entity e) {
        for (ObjectPoolEntity objectPoolEntity : entities.values()) {
            if (objectPoolEntity.equalsEntity(e)) {
                return objectPoolEntity.get();
            }
        }
        throw new NotFoundElementException("Элемента {} нет в коллекции", new Gson().toJson(e, e.getClass()));
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
        boolean isTestItCreateAutotest = Configure.isTestItCreateAutotest;
        Configure.isTestItCreateAutotest = false;


        List<ObjectPoolEntity> entityList = new ArrayList<>();
        for (String key : createdEntities) {
            ObjectPoolEntity objectPoolEntity = entities.get(key);
            if (objectPoolEntity.getStatus() != ObjectStatus.CREATED)
                continue;
            Entity entity = objectPoolEntity.get();
            if (entity instanceof IProduct) {
                entityList.add(objectPoolEntity);
            }
        }
        deleteAllVm(entityList);

        try {
            while (createdEntities.peek() != null) {
                String key = createdEntities.pop();
                ObjectPoolEntity objectPoolEntity = entities.get(key);
                if (objectPoolEntity.getClazz().isAssignableFrom(Token.class))
                    continue;
                if (objectPoolEntity.getStatus() != ObjectStatus.CREATED)
                    continue;
                Entity entity = objectPoolEntity.get();
                try {
                    entity.deleteObject();
                } catch (Throwable e) {
                    objectPoolEntity.setStatus(ObjectStatus.FAILED_DELETE);
                    objectPoolEntity.setError(e);
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Configure.isTestItCreateAutotest = isTestItCreateAutotest;
        log.debug("##### deleteAllResources end #####");
    }

    public static void deleteAllVm(List<ObjectPoolEntity> entityList) {
        ExecutorService threadPool = Executors.newFixedThreadPool(ForkJoinPoolHierarchicalTestExecutorService.parallelism.get());
        for (ObjectPoolEntity objectPoolEntity : entityList) {
            Entity entity = objectPoolEntity.get();
            threadPool.submit(() -> {
                try {
                    entity.deleteObject();
                } catch (Throwable e) {
                    objectPoolEntity.setStatus(ObjectStatus.FAILED_DELETE);
                    objectPoolEntity.setError(e);
                    log.error("##### deleteAllVm error: " + e + "\n" + Throwables.getStackTraceAsString(e));
                }
            });
        }
        awaitTerminationAfterShutdown(threadPool);
    }

    private static boolean containsClassOrSuperclass(Set<Class<?>> classSet, Class<?> targetClass) {
        for (Class<?> clazz : classSet) {
            if (clazz.isAssignableFrom(targetClass)) {
                return true;
            }
        }
        return false;
    }

    public static void removeProducts(Set<Class<?>> currentClassListArgument) {
        List<String> createdEntitiesCopy = new ArrayList<>(createdEntities);
        for (String key : createdEntitiesCopy) {
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
                if (containsClassOrSuperclass(currentClassListArgument, objectPoolEntity.getClazz()))
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


    public static void awaitTerminationAfterShutdown(ExecutorService threadPool) {
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
            listEntities.forEach(v -> addEntity(fromJson(new JSONObject(v).toString(), getClassByName(v.get("objectClassName").toString()))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addEntity(Entity entity){
        ObjectPoolEntity objectPoolEntity = writeEntityToMap(entity);
        objectPoolEntity.setStatus(ObjectStatus.CREATED);
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
        if (entity instanceof ServiceAccount || entity instanceof ServiceAccountToken || entity instanceof UserToken)
            return;
        if (Objects.isNull(getLifecycle().getCurrentTestCaseOrStep().orElse(null)))
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
        if (Configure.isIntegrationTestIt()) {
            if (StepsAspects.getCurrentStep().get() != null) {
                String title = StringUtils.format("Получена сущность {} с параметрами", entity.getClass().getSimpleName());
                StepsAspects.getCurrentStep().get().setTitle(title);
                log.debug(title + ": " + org.apache.commons.lang3.StringUtils.join(parametersMap));
                StepsAspects.getCurrentStep().get().setParameters(parametersMap);
            }
        }
    }
}
