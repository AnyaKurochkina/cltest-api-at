package models.authorizer;

import lombok.Builder;
import models.EntityOld;

@Builder
public class Organization extends EntityOld {
    public String title;
    public String name;
    @Builder.Default
    public boolean isDeleted = false;
}
