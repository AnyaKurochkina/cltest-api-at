package models.authorizer;

import lombok.Builder;
import models.EntityOld;

@Builder
public class Folder extends EntityOld {
    public String id;
    public String type;
    public String name;
    public String parentId;
    @Builder.Default
    public boolean isDeleted = false;
}
