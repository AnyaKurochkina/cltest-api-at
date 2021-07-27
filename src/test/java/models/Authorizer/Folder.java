package models.Authorizer;

import lombok.*;
import models.Entity;

@Builder
public class Folder extends Entity {
    public String id;
    public String type;
    public String name;
    public boolean isDeleted = false;
}
