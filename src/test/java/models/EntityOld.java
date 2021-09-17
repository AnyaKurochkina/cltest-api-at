package models;
import core.CacheService;
import core.helper.JsonHelper;
import java.util.UUID;

/**
 * Наследники данного класса получают возможность взаимодействовать с CacheService.class
 */
public abstract class EntityOld {
    public String objectClassName;
    public String objectUid;


    protected transient JsonHelper jsonHelper = new JsonHelper();
    protected transient CacheService cacheService = new CacheService();

    public void setObjectParams(String className) {
        this.objectClassName = className;
        this.objectUid = UUID.randomUUID().toString();
    }
}
