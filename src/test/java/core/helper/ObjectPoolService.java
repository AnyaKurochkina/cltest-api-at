package core.helper;

import lombok.Getter;
import lombok.Setter;
import models.Entity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ObjectPoolService {
    private static final Map<String, String> entities = new ConcurrentHashMap<>();

    public void create(Entity entity){
        entity.create();
        new ObjectPoolEntity(entity);
    }


    private static class ObjectPoolEntity {
        @Getter
        Entity entity;
        Lock lock = new ReentrantLock();

        public ObjectPoolEntity(Entity entity) {
            this.entity = entity;
        }

        public void lock(){
            lock.lock();
        }

        public void unlock(){
            lock.unlock();
        }

    }
}
