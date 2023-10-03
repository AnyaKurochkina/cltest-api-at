package ui.t1.tests.engine;

import lombok.SneakyThrows;

import java.util.Objects;
import java.util.function.Supplier;

public class EntitySupplier<T> {
    private Throwable error;
    private final Supplier<T> executable;
    private T entity;
    private boolean isRun;

    public EntitySupplier(Supplier<T> executable) {
        this.executable = executable;
    }

    public EntitySupplier<T> copy() {
        return new EntitySupplier<T>(executable);
    }

    public void run(){
        get();
    }

    @SneakyThrows
    public T get() {
        if (Objects.nonNull(error))
            throw error;
        if (!isRun) {
            try {
                entity = executable.get();
            } catch (Throwable e) {
                error = e;
                throw e;
            }
            finally {
                isRun = true;
            }
        }
        return entity;
    }
}
