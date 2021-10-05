package models.authorizer;

import lombok.Builder;
import models.Entity;
import models.EntityOld;

@Builder
public class AccessGroup extends Entity {
    public String name;
    public String projectName;
    public String user;
    public Boolean isDeleted;
    public Boolean isForOrders;

    @Override
    public void create() {
    }

}
