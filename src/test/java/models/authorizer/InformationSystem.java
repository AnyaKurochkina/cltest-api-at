package models.authorizer;

import lombok.Builder;
import lombok.Getter;
import models.Entity;
import models.EntityOld;

@Builder
@Getter
public class InformationSystem extends Entity {
    public String id;
    public Boolean isForOrders;

    @Override
    public void create() {
    }
}
