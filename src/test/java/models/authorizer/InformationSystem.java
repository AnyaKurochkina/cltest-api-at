package models.authorizer;

import lombok.Builder;
import models.Entity;

@Builder
public class InformationSystem extends Entity {
    public String id;
    @Builder.Default
    public boolean isForOrders = false;
}
