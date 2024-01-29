package steps.vpc;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import models.AbstractEntity;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@ToString
@Log4j2
@EqualsAndHashCode(callSuper = false)
public class SecurityGroupResponse extends AbstractEntity {
    String description;
    String id;
    String name;
    String status;

    transient String projectId;

    @Override
    public void delete() {
        log.debug("Удаление группы безопасности id = {}, projectId = {}", id, projectId);
        VpcSteps.deleteSecurityGroup(projectId, id);
    }

    @Override
    protected int getPrioritise() {
        return 2;
    }
}
