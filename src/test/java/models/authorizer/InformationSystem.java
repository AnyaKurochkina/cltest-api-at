package models.authorizer;

import lombok.Builder;
import models.Entity;
import models.EntityOld;

@Builder
public class InformationSystem extends Entity {
    public String id;
    public Boolean isForOrders = false;

    @Override
    public Entity create() {
        return null;
    }
}
