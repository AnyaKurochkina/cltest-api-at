package models;

import core.helper.IEntity;
import core.helper.JsonHelper;
import core.helper.ObjectPoolEntity;
import core.helper.ObjectPoolService;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class Entity implements AutoCloseable {

    public String objectClassName;

    public abstract Entity create();
    public void delete() {}
//    public abstract void reset();
    protected transient JsonHelper jsonHelper = new JsonHelper();
    public String uuid;

    public void save(){
        ObjectPoolService.saveEntity(this);
    }

    @Override
    public void close() throws Exception {
        IEntity iEntity = ObjectPoolService.getObjectPoolEntity(this);
        iEntity.release();
    }


    public <T extends Entity> T createObject(){
        return createObject(false);
    }

    @SuppressWarnings("unchecked")
    private <T extends Entity> T createObject(boolean exclusiveAccess){
        ObjectPoolService.create(this, exclusiveAccess);
        return (T) this;
    }

    public <T extends Entity> T createObjectExclusiveAccess() throws Exception{
        return createObject(true);
    }

//    public abstract void initDefaultFields();
}
