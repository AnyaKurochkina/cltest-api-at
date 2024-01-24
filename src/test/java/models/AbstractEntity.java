package models;

import lombok.extern.log4j.Log4j2;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static models.AbstractEntity.Mode.*;
import static models.ObjectPoolService.awaitTerminationAfterShutdown;

@Log4j2
public abstract class AbstractEntity {
    @SuppressWarnings("unchecked")
    private static final Map<Long, Set<AbstractEntity>>[] entities = new ConcurrentHashMap[10];
    private Mode mode = AFTER_TEST;
    private boolean deleted;

    @SuppressWarnings("unchecked")
    public <T extends AbstractEntity> T deleteMode(Mode mode) {
        this.mode = mode;
        addEntity(this);
        return (T) this;
    }

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

    private static void deleteEntity(AbstractEntity entity) {
        if (entity.deleted)
            return;
        try {
            entity.delete();
            entity.deleted = true;
        } catch (Throwable e) {
            log.error("Ошибка при удалении сущности " + entity, e);
        }
    }

    abstract public void delete();

    private static void deleteCurrentThreadEntities(Mode mode) {
        for (Map<Long, Set<AbstractEntity>> map : entities) {
            ExecutorService threadPool = Executors.newFixedThreadPool(5);
            Iterator<AbstractEntity> iterator = map.getOrDefault(Thread.currentThread().getId(), new HashSet<>()).iterator();
            while (iterator.hasNext()) {
                AbstractEntity entity = iterator.next();
                if (mode != AFTER_RUN && entity.mode != mode)
                    continue;
                threadPool.submit(() -> deleteEntity(entity));
                iterator.remove();
            }
            awaitTerminationAfterShutdown(threadPool);
        }
    }

    public static void deleteCurrentClassEntities() {
        deleteCurrentThreadEntities(AFTER_CLASS);
    }

    public static void deleteCurrentTestEntities() {
        deleteCurrentThreadEntities(AFTER_TEST);
    }

    public static void deleteTestRunEntities() {
        deleteCurrentThreadEntities(AFTER_RUN);
    }

    private static void addEntity(AbstractEntity e) {
        entities[e.getPriority()].computeIfAbsent(Thread.currentThread().getId(), k -> new HashSet<>()).add(e);
    }

    /**
     * AFTER_CLASS Только для SAME_THREAD классов
     */
    public enum Mode {
        AFTER_TEST,
        AFTER_CLASS,
        AFTER_RUN
    }
}
