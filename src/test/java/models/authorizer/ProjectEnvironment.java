package models.authorizer;

import lombok.Builder;
import models.EntityOld;

@Builder
public class ProjectEnvironment extends EntityOld {
    public String id;
    public String envType;
    public String env;
    @Builder.Default
    public boolean isForOrders = false;
}
