package core.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.SneakyThrows;
import models.Entity;
import org.junit.jupiter.api.parallel.ResourceLock;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.parallel.ResourceAccessMode.READ;
import static org.junit.jupiter.api.parallel.ResourceAccessMode.READ_WRITE;

public class ObjectPoolEntity implements IEntity {
    private String entity;
    private final Class<? extends Entity> c;
    private final Lock lock = new ReentrantLock();

    public ObjectPoolEntity(Entity entity) {
        this.c = entity.getClass();
        this.entity = ObjectPoolService.toJson(entity);
    }

    @SneakyThrows
    @ResourceLock(value = "entity", mode = READ)
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
    @ResourceLock(value = "entity", mode = READ)
    public <T extends Entity> T get() {
        return ObjectPoolService.fromJson(entity, c);
    }

    @Override
    @ResourceLock(value = "entity", mode = READ_WRITE)
    public void set(Entity entity) {
        this.entity = ObjectPoolService.toJson(entity);
    }

    public void lock() {
        lock.lock();
    }

    @Override
    public void release() {
        if(lock.tryLock())
            lock.unlock();
    }

    @Override
    public String toString(){
        return entity;
    }
}
