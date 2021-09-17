package models.authorizer;

import lombok.Builder;
import models.EntityOld;
import java.util.List;

@Builder
public class ServiceAccount extends EntityOld {
    @Builder.Default
    public boolean isDeleted = false;
    public String projectId;
    public String secret;
    public String name;
    public List<String> roles;
    @Builder.Default
    public boolean isForOrders = false;
}
