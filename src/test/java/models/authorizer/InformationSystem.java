package models.authorizer;

import lombok.Builder;
import models.EntityOld;

@Builder
public class InformationSystem extends EntityOld {
    public String id;
    @Builder.Default
    public boolean isForOrders = false;
}
