package tests.suites.Authorizer.models;

import lombok.*;
import tests.suites.Entity;

@Builder
public class Folder extends Entity {
    public String id;
    public String type;
    public String name;
    public boolean isDeleted = false;
}
