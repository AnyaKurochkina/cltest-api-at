package models.authorizer;

import lombok.Builder;
import models.EntityOld;

@Builder
public class AccessGroup extends EntityOld {
    public String name;
    public String projectName;
    public String user;
    @Builder.Default
    public boolean isDeleted = false;
    @Builder.Default
    public boolean isForOrders = false;
}
