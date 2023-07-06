package models;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractEntity {
    private static final Map<Long, Set<AbstractEntity>> entities = new ConcurrentHashMap<>();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> entities.forEach((k, v) -> v.forEach(AbstractEntity::deleteEntity))));
    }

    private static void deleteEntity(AbstractEntity e) {
        try {
            e.delete();
        } catch (Throwable ignored) {}
    }

    abstract public void delete();

    public AbstractEntity() {
        System.out.println("test");
        entities.computeIfAbsent(Thread.currentThread().getId(), k -> new HashSet<>()).add(this);
    }

    public static void deleteCurrentTestEntities() {
        Iterator<AbstractEntity> iterator = entities.getOrDefault(Thread.currentThread().getId(), new HashSet<>()).iterator();
        while (iterator.hasNext()) {
            AbstractEntity entity = iterator.next();
            deleteEntity(entity);
            iterator.remove();
        }
    }
}
