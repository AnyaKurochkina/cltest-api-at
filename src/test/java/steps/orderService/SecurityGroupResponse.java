package steps.orderService;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import models.AbstractEntity;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@ToString
@EqualsAndHashCode(callSuper = false)
public class SecurityGroupResponse extends AbstractEntity {
    String description;
    String id;
    String name;
    String status;

    transient String projectId;

    @Override
    public void delete() {
        VpcSteps.deleteSecurityGroup(projectId, id);
    }

    @Override
    protected int getPriority() {
        return 2;
    }
}
