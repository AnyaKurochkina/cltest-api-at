package models.authorizer;

import lombok.Builder;
import models.Entity;

@Builder
public class ProjectEnvironment extends Entity {
    public String id;
    public String env;
}
