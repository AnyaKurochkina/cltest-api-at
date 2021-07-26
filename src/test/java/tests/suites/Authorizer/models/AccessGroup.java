package tests.suites.Authorizer.models;

import lombok.*;
import tests.suites.Entity;

@Builder
public class AccessGroup extends Entity {
    public String name;
    public String projectName;
    public boolean isDeleted = false;
}
