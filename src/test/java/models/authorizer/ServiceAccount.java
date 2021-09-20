package models.authorizer;

import lombok.Builder;
import models.Entity;
import models.EntityOld;
import java.util.List;

@Builder
public class ServiceAccount extends Entity {
    @Builder.Default
    public boolean isDeleted = false;
    public String projectId;
    public String secret;
    public String name;
    public List<String> roles;
    @Builder.Default
    public boolean isForOrders = false;

    @Override
    public Entity create() {
        return null;
    }

    @Override
    public void delete() {

    }
}
