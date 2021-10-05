package models.authorizer;

import lombok.Builder;
import models.Entity;
import models.EntityOld;

@Builder
public class ProjectEnvironment extends Entity {
    public String id;
    public String envType;
    public String env;
    public Boolean isForOrders;

    @Override
    public void create() {
    }
}
