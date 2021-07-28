package models.authorizer;

import lombok.*;
import models.Entity;

@Builder
public class Folder extends Entity {
    public String id;
    public String type;
    public String name;
    public String parentId;
    public boolean isDeleted = false;
}
