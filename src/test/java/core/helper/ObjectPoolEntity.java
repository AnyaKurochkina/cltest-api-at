package core.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.ToString;
import models.Entity;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ObjectPoolEntity implements IEntity {
    private String entity;
    private final Class<? extends Entity> c;
    private final Lock lock = new ReentrantLock();

    public ObjectPoolEntity(Entity entity) {
        this.c = entity.getClass();
        this.entity = ObjectPoolService.toJson(entity);
    }

    @SneakyThrows
    public boolean equalsEntity(Object o) {
        if (this == o)
            return true;
        if (o == null || c != o.getClass())
            return false;
        ObjectMapper mapper = new ObjectMapper();
        String that = new Gson().toJson(o, o.getClass());
        JsonNode jsonNodeThis = mapper.readTree(entity);
        JsonNode jsonNodeThat = mapper.readTree(that);
        Iterator<Map.Entry<String, JsonNode>> node = jsonNodeThis.fields();
        while (node.hasNext()) {
            Map.Entry<String, JsonNode> entry = node.next();
            if (!jsonNodeThat.hasNonNull(entry.getKey()))
                node.remove();
        }

        return Objects.equals(jsonNodeThis, jsonNodeThat);
    }

    @Override
    public <T extends Entity> T get() {
        return ObjectPoolService.fromJson(entity, c);
    }

    @Override
    public void set(Entity entity) {
        this.entity = ObjectPoolService.toJson(entity);
    }

    public void lock() {
        lock.lock();
    }

    @Override
    public void release() {
        lock.unlock();
    }

    @Override
    public String toString(){
        return entity;
    }
}
