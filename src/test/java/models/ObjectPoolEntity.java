package models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import core.enums.ObjectStatus;
import core.exception.CreateEntityException;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import models.cloud.orderService.interfaces.IProduct;
import org.junit.jupiter.api.parallel.ResourceLock;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static core.helper.StringUtils.getStackTrace;
import static org.junit.jupiter.api.parallel.ResourceAccessMode.READ;
import static org.junit.jupiter.api.parallel.ResourceAccessMode.READ_WRITE;

@Log4j2
public class ObjectPoolEntity {
    private static final Map<String, List<ObjectPoolEntity>> threads = new ConcurrentHashMap<>();

    private String entity;
    @Setter
    @Getter
    private boolean isPublic = true;
    @Setter
    @Getter
    private ObjectStatus status = ObjectStatus.NOT_CREATED;
    @Setter
    @Getter
    private Throwable error;
    @Getter
    public final Class<? extends Entity> clazz;
    private final CustomReentrantLock lock = new CustomReentrantLock();

    public ObjectPoolEntity(Entity entity) {
        this.clazz = entity.getClass();
        this.entity = ObjectPoolService.toJson(entity);
    }

    @SneakyThrows
    @ResourceLock(value = "entity", mode = READ)
    public boolean equalsEntity(Object o) {
//        if(status.equals(ObjectStatus.DELETED))
//            return false;
        if (!isPublic)
            return false;
        if (this == o)
            return true;
        if (o == null || clazz != o.getClass())
            return false;
        ObjectMapper mapper = new ObjectMapper();
        String that = new Gson().toJson(o, o.getClass());
        JsonNode jsonNodeThis = mapper.readTree(entity);
        JsonNode jsonNodeThat = mapper.readTree(that);
        List<Field> ignoredFields = Arrays.stream(o.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Entity.Ignore.class))
                .collect(Collectors.toList());
        ignoredFields.forEach(field -> ((ObjectNode)jsonNodeThat).remove(field.getName()));
        removeEmptyNode(jsonNodeThis);
        removeEmptyNode(jsonNodeThat);
        removeNode(removeSystemNode(jsonNodeThis), removeSystemNode(jsonNodeThat));
//        if (o instanceof Rhel)
//            log.warn("jsonNodeThis '{}',  jsonNodeThat '{}' : {}", jsonNodeThis, jsonNodeThat, Objects.equals(jsonNodeThis, jsonNodeThat));
        return Objects.equals(jsonNodeThis, jsonNodeThat);
    }

    private void removeNode(JsonNode jsonNodeThis, JsonNode jsonNodeThat) {
        Iterator<Map.Entry<String, JsonNode>> node = jsonNodeThis.fields();
        while (node.hasNext()) {
            Map.Entry<String, JsonNode> entry = node.next();
            if (entry.getValue() != null && jsonNodeThat.has(entry.getKey())) {
                removeNode(entry.getValue(), jsonNodeThat.get(entry.getKey()));
            }
            if (!jsonNodeThat.hasNonNull(entry.getKey()))
                node.remove();
        }
    }

    private void removeEmptyNode(JsonNode jsonNode) {
        Iterator<Map.Entry<String, JsonNode>> node = jsonNode.fields();
        while (node.hasNext()) {
            Map.Entry<String, JsonNode> entry = node.next();
            if (entry.getValue().isArray()) {
                removeEmptyNode(entry.getValue());
            }
            if (entry.getValue().isNull()) {
                node.remove();
                continue;
            }
            if (entry.getValue().isArray() && entry.getValue().isEmpty()) {
                node.remove();
            }
        }
    }

    private JsonNode removeSystemNode(JsonNode jsonNode) {
        Iterator<Map.Entry<String, JsonNode>> node = jsonNode.fields();
        while (node.hasNext()) {
            Map.Entry<String, JsonNode> entry = node.next();
            if (entry.getKey().equals("configurationId")) {
                node.remove();
            }
            if (entry.getKey().equals("uuid")) {
                node.remove();
            }
        }
        return jsonNode;
    }

    @ResourceLock(value = "entity", mode = READ)
    public <T extends Entity> T get() {
        return ObjectPoolService.fromJson(entity, clazz);
    }

    @ResourceLock(value = "entity", mode = READ_WRITE)
    public void set(Entity entity) {
        this.entity = ObjectPoolService.toJson(entity);
    }

//если другой какой-либо поток заблочил этот ресурс и он заблокирован моим
    private List<String> getLockedThreads(String rootThread) {
        ThreadInfo[] infos = ManagementFactory.getThreadMXBean().dumpAllThreads(true, true);
        List<String> threads = new ArrayList<>();
        for (ThreadInfo info : infos) {
            if (Objects.nonNull(info.getLockOwnerName()))
                if (info.getLockOwnerName().equals(rootThread))
                    if (!threads.contains(info.getThreadName())) {
                        threads.add(info.getThreadName());
                        threads.addAll(getLockedThreads(info.getThreadName()));
                    }
        }
        return threads;
    }

    private boolean isDeadLock(String thread, String threadLock) {
        if(Objects.isNull(thread))
            return false;
        return getLockedThreads(thread).contains(threadLock);
    }

    @SneakyThrows
    public void lock() {
        if(isDeadLock(Thread.currentThread().getName(), lock.getOwnerThreadName()))
            throw new CreateEntityException("Тестовое исключение. Надо перезапустить тест :(");
        lock.tryLock(2, TimeUnit.HOURS);
    }

    public void release() {
        try {
            lock.unlock();
        } catch (IllegalMonitorStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return entity;
    }

    private static void writeLog(String text) {
        log.info("RESOURCE_LOG {} {} \n {}\n", Thread.currentThread().getName(), text, getStackTrace(Thread.currentThread().getStackTrace()));
    }

    private static class CustomReentrantLock extends ReentrantLock {
        @Override
        public boolean tryLock(long timeout, TimeUnit unit) throws InterruptedException {
            return super.tryLock(timeout, unit);
        }

        public String getOwnerThreadName() {
            if(Objects.nonNull(getOwner()))
                return getOwner().getName();
            return null;
        }
    }
}
