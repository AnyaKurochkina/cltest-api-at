package models;

import core.helper.JsonHelper;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class Entity {

    public String objectClassName;

    public abstract Entity create();
    public abstract void delete();
//    public abstract void reset();
    protected transient JsonHelper jsonHelper = new JsonHelper();

//    public abstract void initDefaultFields();
}
