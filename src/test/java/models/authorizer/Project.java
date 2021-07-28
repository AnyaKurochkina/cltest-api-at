package models.authorizer;

import lombok.Builder;
import models.Entity;

@Builder
public class Project extends Entity {
    public String orderId;
    public String id;
    public String informationSystem;
    public String projectName;
    public String env;
    public boolean isDeleted = false;
}
