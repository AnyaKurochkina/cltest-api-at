package models.authorizer;

import lombok.Builder;
import models.Entity;
import models.EntityOld;
import java.util.List;

@Builder
public class ServiceAccount extends Entity {
    public Boolean isDeleted;
    public String projectId;
    public String secret;
    public String name;
    public List<String> roles;
    public Boolean isForOrders;

    @Override
    public Entity create() {
        return null;
    }

    @Override
    public void delete() {

    }
}
