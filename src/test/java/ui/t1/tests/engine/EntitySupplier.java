package ui.t1.tests.engine;

import lombok.SneakyThrows;

import java.util.Objects;
import java.util.function.Supplier;

public class EntitySupplier<T> {
    Throwable error;
    Supplier<T> executable;
    T entity;

    public EntitySupplier(Supplier<T> executable) {
        this.executable = executable;
    }

    public EntitySupplier<T> copy() {
        return new EntitySupplier<T>(executable);
    }

    @SneakyThrows
    public T get() {
        if (Objects.nonNull(error))
            throw error;
        if (Objects.isNull(entity)) {
            try {
                entity = executable.get();
            } catch (Throwable e) {
                error = e;
                throw e;
            }
        }
        return entity;
    }
}
