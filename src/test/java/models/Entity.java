package models;
import core.CacheService;

import java.util.UUID;

public abstract class Entity {
    public String objectClassName;
    public String objectUid;
    protected final CacheService cacheService = new CacheService();
    public void setObjectParams(String className) {
        this.objectClassName = className;
        this.objectUid = UUID.randomUUID().toString();
    }
}
