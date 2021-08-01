package models.orderService;

import lombok.Builder;
import models.Entity;

@Builder
public class ResourcePool extends Entity {
    public String id;
    public String label;
    public String projectId;
}
