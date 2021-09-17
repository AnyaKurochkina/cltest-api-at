package models.orderService;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import models.EntityOld;

@EqualsAndHashCode(callSuper = false)
@Data
public class ResourcePool extends EntityOld {
    public String id;
    @Getter
    public String label;
    public String name;
    public String projectId;

    @Override
    public String toString() {
        return String.format("{\"id\": \"%s\", \"name\": \"%s\"}", id, name);
    }
}
