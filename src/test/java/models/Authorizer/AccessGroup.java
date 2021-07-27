package models.Authorizer;

import lombok.*;
import models.Entity;

@Builder
public class AccessGroup extends Entity {
    public String name;
    public String projectName;
    public boolean isDeleted = false;
}
