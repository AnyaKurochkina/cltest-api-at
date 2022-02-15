package models;

import core.enums.ObjectStatus;
import core.helper.JsonTemplate;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.json.JSONObject;

@NoArgsConstructor
@SuperBuilder
public abstract class Entity implements AutoCloseable {

    public String objectClassName;

    public abstract Entity init();

    public abstract JSONObject toJson();

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
    public void deleteObject() {
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


    public <T extends Entity> T createObject() {
        return createObject(false, true);
    }
    public <T extends Entity> T createObjectPrivateAccess() {
        return createObject(false, false);
    }

    protected  <T extends Entity> T createObject(boolean exclusiveAccess, boolean isPublic) {
        return ObjectPoolService.create(this, exclusiveAccess, isPublic);
    }

    public <T extends Entity> T createObjectExclusiveAccess() {
        return createObject(true, true);
    }

}
