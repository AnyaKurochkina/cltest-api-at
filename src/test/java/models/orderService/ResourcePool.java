package models.orderService;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import models.Entity;
import models.EntityOld;

@EqualsAndHashCode(callSuper = false)
@Data
@Builder
public class ResourcePool extends Entity {
    public String id;
    @Getter
    public String label;
    public String name;
    public String projectId;

    @Override
    public String toString() {
        return String.format("{\"id\": \"%s\", \"name\": \"%s\"}", id, name);
    }

    @Override
    public Entity create() {
        return null;
    }

    @Override
    public void delete() {

    }
}
