package models;

import core.helper.JsonHelper;
import core.enums.ObjectStatus;
import core.helper.JsonTemplate;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
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

    protected void delete() {
    }

    public String uuid;

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
        return createObject(false);
    }

    private <T extends Entity> T createObject(boolean exclusiveAccess) {
        return ObjectPoolService.create(this, exclusiveAccess);
    }

    public <T extends Entity> T createObjectExclusiveAccess() {
        return createObject(true);
    }

}
