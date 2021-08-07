package models;
import core.CacheService;
import core.helper.JsonHelper;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@NoArgsConstructor
@SuperBuilder
public abstract class Entity {
    public String objectClassName;
    public String objectUid;

    @Builder.Default
    protected transient JsonHelper jsonHelper = new JsonHelper();
    @Builder.Default
    protected transient CacheService cacheService = new CacheService();

    public void setObjectParams(String className) {
        this.objectClassName = className;
        this.objectUid = UUID.randomUUID().toString();
    }
}
