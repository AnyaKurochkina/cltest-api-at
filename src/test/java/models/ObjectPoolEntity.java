package models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import core.enums.ObjectStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.junit.jupiter.api.parallel.ResourceLock;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.parallel.ResourceAccessMode.READ;
import static org.junit.jupiter.api.parallel.ResourceAccessMode.READ_WRITE;

public class ObjectPoolEntity {
    private String entity;
    @Getter
    @Setter
    private ObjectStatus status = ObjectStatus.NOT_CREATED;
    @Getter
    @Setter
    private Throwable error;
    @Getter
    public final Class<? extends Entity> clazz;
    private final Lock lock = new ReentrantLock();

    public ObjectPoolEntity(Entity entity) {
        this.clazz = entity.getClass();
        this.entity = ObjectPoolService.toJson(entity);
    }

    @SneakyThrows
    @ResourceLock(value = "entity", mode = READ)
    public boolean equalsEntity(Object o) {
        if (this == o)
            return true;
        if (o == null || clazz != o.getClass())
            return false;
        ObjectMapper mapper = new ObjectMapper();
        String that = new Gson().toJson(o, o.getClass());
        JsonNode jsonNodeThis = mapper.readTree(entity);
        JsonNode jsonNodeThat = mapper.readTree(that);
//        Iterator<Map.Entry<String, JsonNode>> node = jsonNodeThis.fields();
//        while (node.hasNext()) {
//            Map.Entry<String, JsonNode> entry = node.next();
//            if (!jsonNodeThat.hasNonNull(entry.getKey()))
//                node.remove();
//        }
        removeEmptyNode(jsonNodeThis);
        removeEmptyNode(jsonNodeThat);
        removeNode(jsonNodeThis, jsonNodeThat);
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

    @ResourceLock(value = "entity", mode = READ)
    public <T extends Entity> T get() {
        return ObjectPoolService.fromJson(entity, clazz);
    }

    @ResourceLock(value = "entity", mode = READ_WRITE)
    public void set(Entity entity) {
        this.entity = ObjectPoolService.toJson(entity);
    }

    public void lock() {
        lock.lock();
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
}
