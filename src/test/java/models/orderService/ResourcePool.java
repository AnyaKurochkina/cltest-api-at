package models.orderService;

import core.helper.ObjectPoolService;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import models.Entity;
import models.EntityOld;
import models.authorizer.Project;
import steps.orderService.OrderServiceSteps;

@EqualsAndHashCode(callSuper = false)
@Data
@Builder
public class ResourcePool {
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
