package models.authorizer;

import lombok.Builder;
import models.Entity;

@Builder
public class AccessGroup extends Entity {
    public String name;
    public String projectName;
    public String user;
    @Builder.Default
    public boolean isDeleted = false;
    @Builder.Default
    public boolean isForOrders = false;
}
