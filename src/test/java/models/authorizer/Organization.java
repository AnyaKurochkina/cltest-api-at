package models.authorizer;

import lombok.Builder;
import models.Entity;

@Builder
public class Organization extends Entity {
    public String title;
    public String name;
    public boolean isDeleted = false;
}
