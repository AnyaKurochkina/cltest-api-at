package tests.suites.Authorizer.models;

import lombok.Builder;
import tests.suites.Entity;

@Builder
public class Project extends Entity {
    public String id;
    public String informationSystem;
    public String projectName;
    public boolean isDeleted = false;
}
