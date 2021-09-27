package models;

import core.exception.CustomException;
import core.helper.IEntity;
import core.helper.JsonHelper;
import core.helper.ObjectPoolEntity;
import core.helper.ObjectPoolService;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

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
    public void close() {
        IEntity iEntity = ObjectPoolService.getObjectPoolEntity(this);
        System.out.println(this + " ПреРелиз");
        iEntity.release();
        System.out.println(this + " ПостРелиз");
    }


    public <T extends Entity> T createObject(){
        return createObject(false);
    }

    private <T extends Entity> T createObject(boolean exclusiveAccess){
        return ObjectPoolService.create(this, exclusiveAccess);
    }

    public <T extends Entity> T createObjectExclusiveAccess() {
        return createObject(true);
    }

//    public abstract void initDefaultFields();
}
