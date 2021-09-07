package models.authorizer;

import lombok.Builder;
import models.Entity;

@Builder
public class ProjectEnvironment extends Entity {
    public String id;
    public String envType;
    public String env;
    @Builder.Default
    public boolean isForOrders = false;
}
