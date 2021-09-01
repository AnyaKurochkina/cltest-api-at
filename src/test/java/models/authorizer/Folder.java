package models.authorizer;

import lombok.Builder;
import models.Entity;

@Builder
public class Folder extends Entity {
    public String id;
    public String type;
    public String name;
    public String parentId;
    @Builder.Default
    public boolean isDeleted = false;
}
