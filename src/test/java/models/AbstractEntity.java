package models;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractEntity {
    private static final Map<Long, LinkedList<AbstractEntity>> entities = new ConcurrentHashMap<>();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> entities.forEach((k, v) -> v.forEach(AbstractEntity::deleteEntity))));
    }

    private static void deleteEntity(AbstractEntity e) {
        try {
            e.delete();
        } catch (Throwable ignored) {
        }
    }

    abstract public void delete();

    public AbstractEntity() {
        entities.computeIfAbsent(Thread.currentThread().getId(), k -> new LinkedList<>()).add(this);
    }

    public static void deleteCurrentTestEntities() {
        Iterator<AbstractEntity> iterator = entities.getOrDefault(Thread.currentThread().getId(), new LinkedList<>()).descendingIterator();
        while (iterator.hasNext()) {
            AbstractEntity entity = iterator.next();
            deleteEntity(entity);
            iterator.remove();
        }
    }
}
