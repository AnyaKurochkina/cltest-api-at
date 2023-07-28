package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import core.enums.ObjectStatus;
import core.helper.JsonTemplate;
import core.helper.http.StatusResponseException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.SuperBuilder;
import org.json.JSONObject;

import java.lang.annotation.*;

@NoArgsConstructor
@SuperBuilder
@JsonIgnoreProperties(value = {"objectClassName", "uuid", "configurationId"})
public abstract class Entity implements AutoCloseable {
    public String objectClassName;
    public String uuid;
    @Setter
    @Getter
    String configurationId;

    public abstract Entity init();

    public abstract JSONObject toJson();

    @JsonIgnore
    public JsonTemplate getTemplate() {
        return new JsonTemplate(toJson());
    }

    protected abstract void create();

    protected abstract void delete();

    public void save() {
        ObjectPoolService.saveEntity(this);
    }

    @SneakyThrows
    public static JSONObject serialize(Object object) {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return new JSONObject(objectMapper.writeValueAsString(object));
    }

    @SneakyThrows
    protected JSONObject serialize() {
        return serialize(this);
    }

    @Override
    public void close() {
        ObjectPoolEntity objectPoolEntity = ObjectPoolService.getObjectPoolEntity(this);
        if (objectPoolEntity.getStatus() == ObjectStatus.FAILED)
            return;
        objectPoolEntity.release();
    }

    @SneakyThrows
    void deleteObjectV2() {
        ObjectPoolEntity objectPoolEntity = ObjectPoolService.getObjectPoolEntity(this);
        if (objectPoolEntity.getStatus() == ObjectStatus.DELETED)
            return;
        if (objectPoolEntity.getStatus() == ObjectStatus.FAILED_DELETE)
            throw objectPoolEntity.getError();
        try {
            delete();
            objectPoolEntity.setStatus(ObjectStatus.DELETED);
        } catch (Throwable e) {
            objectPoolEntity.setError(e);
            objectPoolEntity.setStatus(ObjectStatus.FAILED_DELETE);
        }
    }

    @SneakyThrows
    public void deleteObject() {
        ObjectPoolEntity objectPoolEntity = ObjectPoolService.getObjectPoolEntity(this);
        if (objectPoolEntity.getStatus() == ObjectStatus.DELETED)
            return;
        if (objectPoolEntity.getStatus() == ObjectStatus.FAILED_DELETE)
            throw objectPoolEntity.getError();
        try {
            delete();
        } finally {
            objectPoolEntity.setStatus(ObjectStatus.DELETED);
        }
    }

    public <T extends Entity> T createObject() {
        return createObject(false, true);
    }

    public <T extends Entity> T createObjectPrivateAccess() {
        return createObject(false, false);
    }

    protected <T extends Entity> T createObject(boolean exclusiveAccess, boolean isPublic) {
        return ObjectPoolService.create(this, exclusiveAccess, isPublic);
    }

    @SneakyThrows
    public void negativeCreateRequest(int expectedStatus) {
        try {
            init();
            create();

        } catch (StatusResponseException e) {
            if (e.getStatus() != expectedStatus)
                throw e;
            return;
        }
        throw new Exception("Статус код в ответе не совпадает с ожидаемым");
    }

    public void negativeDeleteRequest(int expectedStatus) {
        try {
            delete();
        } catch (StatusResponseException e) {
            if (e.getStatus() != expectedStatus)
                throw e;
        }
    }

    public <T extends Entity> T createObjectExclusiveAccess() {
        return createObject(true, true);
    }

    @Documented
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    protected @interface Ignore {
    }

}
