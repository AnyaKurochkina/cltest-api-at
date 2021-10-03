package models;

import core.helper.JsonHelper;
import core.helper.ObjectPoolEntity;
import core.helper.ObjectPoolService;

public abstract class Entity implements AutoCloseable {

    public String objectClassName;

    public abstract Entity create();

    protected void delete() {
    }


    protected transient JsonHelper jsonHelper = new JsonHelper();
    public String uuid;

    public void save() {
        ObjectPoolService.saveEntity(this);
    }

    @Override
    public void close() {
        ObjectPoolEntity objectPoolEntity = ObjectPoolService.getObjectPoolEntity(this);
        System.out.println(this + " ПреРелиз");
        objectPoolEntity.release();
        System.out.println(this + " ПостРелиз");
    }

    public void deleteObject() {
        ObjectPoolEntity objectPoolEntity = ObjectPoolService.getObjectPoolEntity(this);
        objectPoolEntity.setCreated(false);
        delete();
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

//    public abstract void initDefaultFields();
}
