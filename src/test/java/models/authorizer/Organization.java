package models.authorizer;

import lombok.Builder;
import models.Entity;

@Builder
public class Organization extends Entity {
    public String title;
    public String name;
    @Builder.Default
    public boolean isDeleted = false;
}
