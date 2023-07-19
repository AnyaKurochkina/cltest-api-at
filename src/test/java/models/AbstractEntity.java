package models;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static models.ObjectPoolService.awaitTerminationAfterShutdown;

public abstract class AbstractEntity {
    @SuppressWarnings("unchecked")
    private static final Map<Long, Set<AbstractEntity>>[] entities = new ConcurrentHashMap[10];

    protected int getPriority() {
        return 0;
    }

    static {
        for (int i = 0; i < entities.length; i++)
            entities[i] = new ConcurrentHashMap<>();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (Map<Long, Set<AbstractEntity>> map : entities) {
                ExecutorService threadPool = Executors.newFixedThreadPool(10);
                threadPool.submit(() -> map.forEach((k, v) -> v.forEach(AbstractEntity::deleteEntity)));
                awaitTerminationAfterShutdown(threadPool);
            }
        }));
    }

    private static void deleteEntity(AbstractEntity e) {
        try {
            e.delete();
        } catch (Throwable ignored) {
        }
    }

    abstract public void delete();

    public static void deleteCurrentTestEntities() {
        for (Map<Long, Set<AbstractEntity>> map : entities) {
            ExecutorService threadPool = Executors.newFixedThreadPool(5);
            Iterator<AbstractEntity> iterator = map.getOrDefault(Thread.currentThread().getId(), new HashSet<>()).iterator();
            while (iterator.hasNext()) {
                AbstractEntity entity = iterator.next();
                threadPool.submit(() -> deleteEntity(entity));
                iterator.remove();
            }
            awaitTerminationAfterShutdown(threadPool);
        }
    }

    public static void addEntity(AbstractEntity e) {
        entities[e.getPriority()].computeIfAbsent(Thread.currentThread().getId(), k -> new HashSet<>()).add(e);
    }
}
