package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import core.enums.ObjectStatus;
import core.helper.JsonTemplate;
import core.helper.http.Http;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.SuperBuilder;
import org.json.JSONObject;

@NoArgsConstructor
@SuperBuilder
public abstract class Entity implements AutoCloseable {

    public String objectClassName;

    public abstract Entity init();

    public abstract JSONObject toJson();

    @JsonIgnore
    public JsonTemplate getTemplate(){
        return new JsonTemplate(toJson());
    }

    protected abstract void create();

    protected abstract void delete();

    public String uuid;
    @Setter
    @Getter
    String configurationId;

    public void save() {
        ObjectPoolService.saveEntity(this);
    }

    @Override
    public void close() {
        ObjectPoolEntity objectPoolEntity = ObjectPoolService.getObjectPoolEntity(this);
        if(objectPoolEntity.getStatus() == ObjectStatus.FAILED)
            return;
        objectPoolEntity.release();
    }

    @SneakyThrows
    void deleteObjectV2(){
        ObjectPoolEntity objectPoolEntity = ObjectPoolService.getObjectPoolEntity(this);
        if (objectPoolEntity.getStatus() == ObjectStatus.DELETED)
            return;
        if(objectPoolEntity.getStatus() == ObjectStatus.FAILED_DELETE)
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
        if(objectPoolEntity.getStatus() == ObjectStatus.FAILED_DELETE)
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

    protected  <T extends Entity> T createObject(boolean exclusiveAccess, boolean isPublic) {
        return ObjectPoolService.create(this, exclusiveAccess, isPublic);
    }

    public void negativeCreateRequest(int expectedStatus) {
        try {
            init();
            create();
        } catch (Http.StatusResponseException e) {
            if(e.getStatus() != expectedStatus)
                throw e;
        }
    }

    public void negativeDeleteRequest(int expectedStatus) {
        try {
            delete();
        } catch (Http.StatusResponseException e) {
            if(e.getStatus() != expectedStatus)
                throw e;
        }
    }

    public <T extends Entity> T createObjectExclusiveAccess() {
        return createObject(true, true);
    }

}
