package models;

import core.helper.JsonHelper;
import core.enums.ObjectStatus;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.SuperBuilder;
import org.json.JSONObject;

@NoArgsConstructor
@SuperBuilder
public abstract class Entity implements AutoCloseable {

    public String objectClassName;

    public void init() {

    }

    public abstract JSONObject toJson();

    protected abstract void create();

    protected void delete() {
    }

    @Builder.Default
    protected transient JsonHelper jsonHelper = new JsonHelper();
    public String uuid;

    public void save() {
        ObjectPoolService.saveEntity(this);
    }

    @Override
    public void close() {
        ObjectPoolEntity objectPoolEntity = ObjectPoolService.getObjectPoolEntity(this);
        objectPoolEntity.release();
    }

    @SneakyThrows
    public void deleteObject() {
        ObjectPoolEntity objectPoolEntity = ObjectPoolService.getObjectPoolEntity(this);
        if (objectPoolEntity.getStatus() == ObjectStatus.DELETED)
            return;
        if(objectPoolEntity.getStatus() == ObjectStatus.FAILED_DELETE)
        throw objectPoolEntity.getError();
        delete();
        objectPoolEntity.setStatus(ObjectStatus.DELETED);
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
