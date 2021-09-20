package models.authorizer;

import lombok.Builder;
import models.Entity;
import models.EntityOld;

@Builder
public class Project extends Entity {
    public String orderId;
    public String id;
    public String informationSystem;
    public String projectName;
    public String env;
    @Builder.Default
    public boolean isDeleted = false;
    @Builder.Default
    public boolean isForOrders = false;

    @Override
    public Entity create() {
        return null;
    }

    @Override
    public void delete() {

    }
}
